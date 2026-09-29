package com.imran.clothstore.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * পুরো BackupPayload (ওয়েব অ্যাপের imran_store/backup ডকুমেন্টের হুবহু সমতুল্য) একটা
 * একক-রো JSON ব্লব হিসেবে লোকাল Room ডেটাবেসে সংরক্ষণ করা হয় — প্রতিটা তালিকার জন্য আলাদা
 * টেবিল না বানিয়ে। কারণ:
 *  ১. Firestore-এও পুরো ডেটা একটামাত্র ডকুমেন্ট — শেপ মেলানো থাকলে
 *     sync/merge/tombstone লজিক (BackupRepository.applyTombstones ইত্যাদি) হুবহু পুনর্ব্যবহার
 *     করা যায়, নতুন করে normalize/denormalize কোড লিখতে হয় না।
 *  ২. এই এপে একসাথে অসংখ্য কনকারেন্ট ইউজার/রো নেই (একটা দোকানের একক ব্যবহারকারী) — তাই
 *     normalized টেবিলের কর্মক্ষমতা সুবিধা এখানে প্রাসঙ্গিক না, কিন্তু ঝুঁকিপূর্ণ schema
 *     migration এড়ানোর সুবিধা গুরুত্বপূর্ণ।
 *
 * id সবসময় ধ্রুবক ০ — টেবিলে সবসময় ঠিক একটাই রো থাকে (upsert প্যাটার্ন, দেখুন LocalCacheDao)।
 */
@Entity(tableName = "backup_cache")
data class BackupCacheEntity(
    @PrimaryKey val id: Int = SINGLETON_ID,
    /** সম্পূর্ণ BackupPayload-এর org.json দিয়ে এনকোড করা JSON — দেখুন BackupJson.kt */
    val payloadJson: String,
    /** এই লোকাল রো সবশেষ কখন লেখা হয়েছে — sync worker conflict-resolution এ ব্যবহৃত */
    val localUpdatedAt: Long = System.currentTimeMillis(),
    /**
     * true হলে এই লোকাল অবস্থা এখনো Firestore-এ push হয়নি (অফলাইনে করা পরিবর্তন)।
     * sync worker সফলভাবে push করলে false করে দেয়।
     */
    val pendingSync: Boolean = false
) {
    companion object {
        const val SINGLETON_ID = 0
    }
}
