package com.imran.clothstore.data.backup

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.imran.clothstore.AppSingletons
import com.imran.clothstore.data.local.LocalCacheRepository
import com.imran.clothstore.data.sync.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * ওয়েব অ্যাপের dbPush()/dbPull()/রিয়েল-টাইম listener লজিকের Kotlin সংস্করণ —
 * এখন অফলাইন-ফার্স্ট (আইটেম #১): Room (LocalCacheRepository) হলো সোর্স অফ ট্রুথ, যা থেকে
 * সব UI read করে; Firestore-এ push/pull ব্যাকগ্রাউন্ডে SyncWorker (data/sync/) করে।
 *
 * এই ক্লাসের পাবলিক API (মেথডের নাম/প্যারামিটার) ইচ্ছাকৃতভাবে অপরিবর্তিত রাখা হয়েছে —
 * FvListViewModel, PaikkariViewModel, DetailViewModel ইত্যাদি কোনো caller-এ কোনো
 * পরিবর্তন লাগেনি, শুধু ভেতরের ডেটা-উৎস বদলেছে (Firestore সরাসরি → Room স্থানীয় ক্যাশ)।
 * appContext নতুন, ঐচ্ছিক প্যারামিটার — দেওয়া থাকলে প্রতিটা write-এর পর অবিলম্বে sync
 * ট্রিগার করার চেষ্টা হয় (নেটওয়ার্ক থাকলে); না দিলে শুধু পরবর্তী পিরিয়ডিক sync-এ প্রতিফলিত হবে।
 */
class BackupRepository(
    private val local: LocalCacheRepository = com.imran.clothstore.AppSingletons.localCacheRepository,
    private val appContext: Context? = AppSingletons.appContext
) {
    // Firebase Firestore initialization wrapped in lazy to prevent crash when google-services.json is missing
    private val db: FirebaseFirestore
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            throw IllegalStateException("Firebase is not initialized. Please add google-services.json", e)
        }
    companion object {
        const val COLLECTION = "imran_store"
        const val DOCUMENT = "backup"
        const val MAX_CHART_ENTRIES = 26
    }

    private val docRef get() = db.collection(COLLECTION).document(DOCUMENT)

    /** এখন Room ক্যাশ থেকে রিয়েল-টাইম পর্যবেক্ষণ করে — অফলাইনেও কাজ করে, তাৎক্ষণিক এমিট হয়
     *  (নেটওয়ার্ক রাউন্ড-ট্রিপের জন্য অপেক্ষা করতে হয় না)। Firestore থেকে pull করে Room
     *  আপডেট করার কাজ SyncWorker করে, যা এই Flow-কে স্বয়ংক্রিয়ভাবে আপডেট করে দেয়। */
    fun observeBackup(): Flow<BackupPayload> = local.observe()

    /** Room ক্যাশ থেকে এক-বারের read — নেটওয়ার্ক লাগে না */
    suspend fun fetchOnce(): BackupPayload = local.getOnce()

    /**
     * সরাসরি Firestore থেকে বর্তমান রিমোট ডকুমেন্ট পড়ে — SyncWorker ব্যবহার করে দূরবর্তী
     * পরিবর্তনের সাথে merge করার জন্য। সাধারণ UI read-এর জন্য নয় (সেটা fetchOnce()/
     * observeBackup() ব্যবহার করবে, যেগুলো Room থেকে পড়ে)।
     */
    suspend fun fetchRemoteOnce(): BackupPayload {
        val snapshot = docRef.get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(BackupPayload::class.java) ?: BackupPayload()
        } else {
            BackupPayload()
        }
    }

    /** SyncWorker এই মেথড দিয়ে Firestore-এ push করে — সরাসরি docRef ব্যবহার, Room জড়িত না */
    suspend fun pushToRemote(payload: BackupPayload) {
        docRef.set(payload).await()
    }

    /**
     * সাধারণ read-modify-write হেল্পার — সব write অপারেশন এটার মধ্য দিয়ে যায়। এখন Room-কে
     * আগে পড়ে, বদলায়, Room-এই আবার লেখে (pendingSync=true চিহ্নিত করে) — নেটওয়ার্ক ছাড়াই
     * তাৎক্ষণিক সম্পন্ন হয়। SyncWorker পরে ব্যাকগ্রাউন্ডে এই পরিবর্তন Firestore-এ push করবে।
     *
     * লেখার আগে is_deleted_ids (tombstone) অনুযায়ী প্রতিটা তালিকা থেকে ডিলিট হওয়া আইটেম
     * ফিল্টার করে বাদ দেওয়া হয় — এটাই ওয়েব অ্যাপের onSnapshot listener-এ থাকা
     * "filtered = data[k].filter(r => !deletedForKey.has(r.id))" চেকের সমতুল্য।
     */
    private suspend fun updatePayload(transform: (BackupPayload) -> BackupPayload) {
        val current = local.getOnce()
        val tombstoneFiltered = applyTombstones(current)
        val updated = transform(tombstoneFiltered).copy(updatedAt = System.currentTimeMillis())
        local.save(applyTombstones(updated), markPendingSync = true)
        appContext?.let { SyncWorker.triggerImmediate(it) }
    }

    /** is_deleted_ids অনুযায়ী is_c1_list..is_c4_list, fabricPurchaseData_v2 (গ্রুপ-লেভেল) ও
     *  fabricPurchaseData_v2_purchases (গ্রুপের ভেতরের একক purchase-লেভেল) থেকে ডিলিট হওয়া
     *  id বাদ দেয়। purchase-লেভেল টম্বস্টোন ছাড়া একটা ডিভাইসে ডিলিট করা একক purchase অন্য
     *  ডিভাইসের stale sync-এ ফিরে আসতে পারত। */
    fun applyTombstones(payload: BackupPayload): BackupPayload {
        val deleted = payload.is_deleted_ids
        fun deadSetFor(key: String): Set<Long> = (deleted[key] ?: emptyList()).toSet()

        val c1Dead = deadSetFor("is_c1_list")
        val c2Dead = deadSetFor("is_c2_list")
        val c3Dead = deadSetFor("is_c3_list")
        val c4Dead = deadSetFor("is_c4_list")
        val fabricGroupDead = deadSetFor("fabricPurchaseData_v2")
        val fabricPurchaseDead = deadSetFor("fabricPurchaseData_v2_purchases")

        val fabricGroups = if (fabricGroupDead.isEmpty()) {
            payload.fabricPurchaseData_v2
        } else {
            payload.fabricPurchaseData_v2.filterNot { it.id in fabricGroupDead }
        }
        val fabricGroupsWithPurchasesFiltered = if (fabricPurchaseDead.isEmpty()) {
            fabricGroups
        } else {
            fabricGroups.map { group ->
                group.copy(purchases = group.purchases.filterNot { it.id in fabricPurchaseDead })
            }
        }

        return payload.copy(
            is_c1_list = if (c1Dead.isEmpty()) payload.is_c1_list else payload.is_c1_list.filterNot { it.id in c1Dead },
            is_c2_list = if (c2Dead.isEmpty()) payload.is_c2_list else payload.is_c2_list.filterNot { it.id in c2Dead },
            is_c3_list = if (c3Dead.isEmpty()) payload.is_c3_list else payload.is_c3_list.filterNot { it.id in c3Dead },
            is_c4_list = if (c4Dead.isEmpty()) payload.is_c4_list else payload.is_c4_list.filterNot { it.id in c4Dead },
            fabricPurchaseData_v2 = fabricGroupsWithPurchasesFiltered
        )
    }

    // ── entries (is_c1_list..is_c4_list) ──

    fun getEntryList(payload: BackupPayload, category: com.imran.clothstore.data.model.EntryCategory): List<BackupEntry> =
        when (category.backupKey) {
            "is_c1_list" -> payload.is_c1_list
            "is_c2_list" -> payload.is_c2_list
            "is_c3_list" -> payload.is_c3_list
            "is_c4_list" -> payload.is_c4_list
            else -> emptyList()
        }

    private fun withEntryList(
        payload: BackupPayload,
        category: com.imran.clothstore.data.model.EntryCategory,
        list: List<BackupEntry>
    ): BackupPayload = when (category.backupKey) {
        "is_c1_list" -> payload.copy(is_c1_list = list)
        "is_c2_list" -> payload.copy(is_c2_list = list)
        "is_c3_list" -> payload.copy(is_c3_list = list)
        "is_c4_list" -> payload.copy(is_c4_list = list)
        else -> payload
    }

    suspend fun addOrUpdateEntry(category: com.imran.clothstore.data.model.EntryCategory, entry: BackupEntry) {
        updatePayload { payload ->
            val list = getEntryList(payload, category)
            val exists = list.any { it.id == entry.id }
            val newList = if (exists) list.map { if (it.id == entry.id) entry else it } else list + entry
            withEntryList(payload, category, newList)
        }
    }

    suspend fun deleteEntry(category: com.imran.clothstore.data.model.EntryCategory, entryId: Long) {
        updatePayload { payload ->
            val list = getEntryList(payload, category).filterNot { it.id == entryId }
            val newDeleted = payload.is_deleted_ids.toMutableMap().apply {
                val forKey = (this[category.backupKey] ?: emptyList()).toMutableList()
                if (!forKey.contains(entryId)) forKey.add(entryId)
                this[category.backupKey] = forKey
            }
            withEntryList(payload, category, list).copy(is_deleted_ids = newDeleted)
        }
    }

    // ── chart_data (weekly reports, ts দিয়ে merge, ২৬-এর ক্যাপ) ──

    /** saveToChart() এর সমতুল্য — একই year+week থাকলে replace, নাহলে prepend, তারপর ২৬-এ ক্যাপ */
    suspend fun saveOrUpdateWeeklyReport(report: BackupWeeklyReport) {
        updatePayload { payload ->
            val existingIdx = payload.chart_data.indexOfFirst { it.year == report.year && it.week == report.week }
            var newList = if (existingIdx >= 0) {
                payload.chart_data.toMutableList().apply { set(existingIdx, report) }
            } else {
                listOf(report) + payload.chart_data
            }
            if (newList.size > MAX_CHART_ENTRIES) {
                newList = newList.sortedBy { it.ts }.takeLast(MAX_CHART_ENTRIES)
            }
            payload.copy(chart_data = newList)
        }
    }

    suspend fun deleteWeeklyReport(reportId: Long) {
        updatePayload { payload ->
            payload.copy(chart_data = payload.chart_data.filterNot { it.id == reportId })
        }
    }

    // ── profitStripData_v1 (single-object, last-write-wins) ──

    suspend fun saveProfitStrip(net: Double, gross: Double) {
        updatePayload { payload -> payload.copy(profitStripData_v1 = ProfitStripData(net = net, gross = gross)) }
    }

    // ── fabricPurchaseData_v2 (id দিয়ে merge) ──

    suspend fun addFabricPurchase(groupName: String, unit: String, purchase: BackupFabricPurchase) {
        updatePayload { payload ->
            // ওয়েব অ্যাপ (লাইন ১২০৩২) name.toLowerCase() === name.toLowerCase() দিয়ে
            // case-insensitive মেলায় — নাহলে ভিন্ন ক্যাপিটালাইজেশনে ডুপ্লিকেট গ্রুপ তৈরি
            // হয়ে গড়-দর (DCA) হিসাব ভুল হতো।
            val existingIdx = payload.fabricPurchaseData_v2.indexOfFirst { it.name.equals(groupName, ignoreCase = true) }
            val newList = if (existingIdx >= 0) {
                payload.fabricPurchaseData_v2.toMutableList().apply {
                    val group = this[existingIdx]
                    this[existingIdx] = group.copy(purchases = listOf(purchase) + group.purchases)
                }
            } else {
                payload.fabricPurchaseData_v2 + BackupFabricGroup(
                    id = System.currentTimeMillis(), name = groupName, unit = unit, purchases = listOf(purchase)
                )
            }
            payload.copy(fabricPurchaseData_v2 = newList)
        }
    }

    suspend fun addPurchaseToGroup(groupId: Long, purchase: BackupFabricPurchase) {
        updatePayload { payload ->
            val newList = payload.fabricPurchaseData_v2.map { group ->
                if (group.id == groupId) group.copy(purchases = listOf(purchase) + group.purchases) else group
            }
            payload.copy(fabricPurchaseData_v2 = newList)
        }
    }

    /** fabricDeletePurchase() এর সমতুল্য — গ্রুপ খালি হয়ে গেলে গ্রুপও মুছে যায়, এবং প্রতিটা
     *  ডিলিট হওয়া purchase-এর id fabricPurchaseData_v2_purchases টম্বস্টোনে যোগ হয় যাতে
     *  অন্য ডিভাইসের stale sync থেকে ফিরে না আসে (গ্রুপ-লেভেল টম্বস্টোনের পাশাপাশি,
     *  আলাদা key — আইটেম #৯ দেখুন)। */
    suspend fun deleteFabricPurchase(groupId: Long, purchaseId: Long) {
        updatePayload { payload ->
            val newList = payload.fabricPurchaseData_v2.mapNotNull { group ->
                if (group.id != groupId) return@mapNotNull group
                val remaining = group.purchases.filterNot { it.id == purchaseId }
                if (remaining.isEmpty()) null else group.copy(purchases = remaining)
            }
            val newDeleted = payload.is_deleted_ids.toMutableMap().apply {
                val forKey = (this["fabricPurchaseData_v2_purchases"] ?: emptyList()).toMutableList()
                if (!forKey.contains(purchaseId)) forKey.add(purchaseId)
                this["fabricPurchaseData_v2_purchases"] = forKey
            }
            payload.copy(fabricPurchaseData_v2 = newList, is_deleted_ids = newDeleted)
        }
    }

    // ── সম্পূর্ণ ডেটা মুছে ফেলা (DB Connect Modal এর wipe অ্যাকশন) ──

    /** এখন Room-এও সাথে সাথে খালি করে দেয় (pendingSync=true) — SyncWorker ব্যাকগ্রাউন্ডে
     *  Firestore-এও একই wipe push করবে, ঠিক অন্য যেকোনো পরিবর্তনের মতো। */
    suspend fun wipeAll() {
        local.save(BackupPayload(updatedAt = System.currentTimeMillis()), markPendingSync = true)
        appContext?.let { SyncWorker.triggerImmediate(it) }
    }
}
