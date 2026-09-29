package com.imran.clothstore.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.imran.clothstore.data.backup.BackupPayload
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.local.AppDatabase
import com.imran.clothstore.data.local.LocalCacheRepository
import java.util.concurrent.TimeUnit

/**
 * ওয়েব অ্যাপের dbPush()/dbPull() (index.html লাইন ৫০৯১, ৫১৫৫) এর ব্যাকগ্রাউন্ড
 * সমতুল্য — WorkManager দিয়ে চালানো, যাতে ইউজারকে UI ব্লক করে অপেক্ষা করতে না হয়
 * এবং নেটওয়ার্ক না থাকলে স্বয়ংক্রিয়ভাবে রিট্রাই হয় (roadmap আইটেম #১, ধাপ ৩)।
 *
 * প্রবাহ, প্রতিবার চলার সময়:
 * ১. Room থেকে স্থানীয় অবস্থা পড়া (pendingSync থাকলেই কেবল push করার দরকার, নাহলে শুধু pull)
 * ২. Firestore থেকে দূরবর্তী অবস্থা fetch
 * ৩. SyncEngine.merge() দিয়ে দুটো merge করা (ওয়েব অ্যাপের dbPush() লজিক হুবহু)
 * ৪. merged ফলাফল Firestore-এ push + Room-এ pendingSync=false দিয়ে সেভ
 *
 * নেটওয়ার্ক না থাকলে Firestore কল ব্যর্থ হবে, WorkManager স্বয়ংক্রিয়ভাবে ব্যাকঅফ-সহ
 * রিট্রাই করবে (retry() রিটার্ন করলে) — ততক্ষণ Room-এর স্থানীয় ডেটাই UI-তে দেখা যাবে,
 * অ্যাপ অকেজো হবে না।
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.getInstance(applicationContext)
            val local = LocalCacheRepository(db.backupCacheDao())
            
            // Handle missing google-services.json gracefully
            val firestore = try {
                FirebaseFirestore.getInstance()
            } catch (e: Exception) {
                return Result.failure() // Can't sync without Firebase
            }
            
            val repository = BackupRepository(local, applicationContext)

            val hasPending = local.hasPendingSync()
            val localPayload = local.getOnce()
            val remotePayload = try {
                repository.fetchRemoteOnce()
            } catch (e: Exception) {
                // নেটওয়ার্ক নেই বা Firestore আনরিচেবল — এখনো কিছু push করা যায়নি,
                // পরে আবার চেষ্টা করার জন্য retry() রিটার্ন করা হচ্ছে
                return Result.retry()
            }

            val merged: BackupPayload = if (hasPending) {
                SyncEngine.merge(local = localPayload, remote = remotePayload)
            } else {
                // কোনো স্থানীয় পরিবর্তন পেন্ডিং নেই — শুধু remote-কে সত্য ধরে নেওয়া (সাধারণ pull)
                remotePayload
            }

            if (hasPending) {
                repository.pushToRemote(merged)
            }
            // pull-এর ফলাফল (অন্য ডিভাইসের পরিবর্তনসহ) Room-এ প্রতিফলিত করা, pendingSync ক্লিয়ার করা
            local.save(merged, markPendingSync = false)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val PERIODIC_WORK_NAME = "backup_sync_periodic"
        private const val ONE_TIME_WORK_NAME = "backup_sync_immediate"

        /** ClothStoreApplication.onCreate() থেকে একবার কল হয় — প্রতি ১৫ মিনিটে ব্যাকগ্রাউন্ড pull/push */
        fun schedulePeriodic(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        /** একটা এন্ট্রি সেভ/ডিলিট হওয়ার সাথে সাথে দ্রুত sync করার চেষ্টা (নেটওয়ার্ক থাকলে) —
         *  BackupRepository-এর প্রতিটা write-এর পর এটা কল করা হবে (ধাপ ৩ সংযোগ, দেখুন
         *  FvListViewModel ইত্যাদির saveEntry()/deleteEntry() কলের ঠিক পরে) */
        fun triggerImmediate(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
