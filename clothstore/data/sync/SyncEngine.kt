package com.imran.clothstore.data.sync

import com.imran.clothstore.data.backup.BackupFabricGroup
import com.imran.clothstore.data.backup.BackupPayload
import com.imran.clothstore.data.backup.BackupWeeklyReport

/**
 * ওয়েব অ্যাপের dbPush()/_mergeArrByTs()/_delIdsMerge() (index.html লাইন ৫০৯১-৫৩৩০)
 * এর হুবহু Kotlin পোর্ট — স্থানীয় (Room) ও দূরবর্তী (Firestore) BackupPayload merge করে।
 *
 * দুই ধরনের merge, আইটেম ৪ (roadmap):
 * ১. id-ভিত্তিক union (is_c1_list..c4_list, fabricPurchaseData_v2) — স্থানীয় সব রাখা হয়,
 *    দূরবর্তী থেকে শুধু সেগুলো যোগ হয় যাদের id স্থানীয়তে নেই এবং tombstone-এ নেই।
 *    (dbPush()-এ "local-wins" নয় — কনফ্লিক্ট হলে **স্থানীয় কপি** জেতে, যেহেতু merged
 *    অ্যারে local দিয়ে শুরু হয় এবং remote শুধু অনুপস্থিত id যোগ করে)
 * ২. ts-ভিত্তিক merge (chart_data) — একই ts থাকলে local জেতে (byTs ম্যাপে local আগে বসে,
 *    remote সেই key থাকলে ওভাররাইট করে না); তারপর একই year+week থাকলে সবচেয়ে নতুন ts রাখা হয়।
 */
object SyncEngine {

    /** ওয়েব অ্যাপের dbPush()-এ _FB_KEYS.forEach ব্লকের is_c1_list..is_c4_list অংশের সমতুল্য */
    fun <T> mergeById(local: List<T>, remote: List<T>, idOf: (T) -> Long, deadIds: Set<Long>): List<T> {
        val localIds = local.map(idOf).toSet()
        val merged = local.toMutableList()
        remote.forEach { r ->
            val id = idOf(r)
            if (id !in localIds && id !in deadIds) merged.add(r)
        }
        return merged
    }

    /** fabricPurchaseData_v2-এর জন্য mergeById + প্রতিটা গ্রুপের ভেতরের purchases-ও merge (id দিয়ে) */
    fun mergeFabricGroups(
        local: List<BackupFabricGroup>,
        remote: List<BackupFabricGroup>,
        deadGroupIds: Set<Long>,
        deadPurchaseIds: Set<Long>
    ): List<BackupFabricGroup> {
        val localIds = local.map { it.id }.toSet()
        val remoteById = remote.associateBy { it.id }
        val merged = mutableListOf<BackupFabricGroup>()

        // ১. স্থানীয় প্রতিটা গ্রুপ রাখা হয়; একই id-র remote গ্রুপ থাকলে (দুই ডিভাইসে edit
        //    হয়েছে) তার ভেতরের purchases-ও merge করা হয়, নাহলে নতুন remote purchase হারিয়ে যেত
        local.forEach { localGroup ->
            val remoteGroup = remoteById[localGroup.id]
            if (remoteGroup != null) {
                val mergedPurchases = mergeById(localGroup.purchases, remoteGroup.purchases, { it.id }, deadPurchaseIds)
                merged.add(localGroup.copy(purchases = mergedPurchases))
            } else {
                merged.add(localGroup)
            }
        }
        // ২. যে remote গ্রুপ স্থানীয়তে নেই এবং tombstone-এ নেই, সেগুলো যোগ করা হয়
        remote.forEach { remoteGroup ->
            if (remoteGroup.id !in localIds && remoteGroup.id !in deadGroupIds) {
                merged.add(remoteGroup)
            }
        }
        return merged
    }

    /** ওয়েব অ্যাপের _mergeArrByTs() এর হুবহু পোর্ট — chart_data-র জন্য */
    fun mergeWeeklyReportsByTs(
        local: List<BackupWeeklyReport>,
        remote: List<BackupWeeklyReport>,
        deadTs: Set<Long>,
        maxEntries: Int = 26
    ): List<BackupWeeklyReport> {
        val byTs = LinkedHashMap<Long, BackupWeeklyReport>()
        local.forEach { e -> if (e.ts !in deadTs) byTs.putIfAbsent(e.ts, e) }
        remote.forEach { e -> if (e.ts !in deadTs) byTs.putIfAbsent(e.ts, e) }
        val merged = byTs.values.toList()

        // একই year+week থাকলে সবচেয়ে নতুন ts রাখা হয়
        val byWeek = LinkedHashMap<String, BackupWeeklyReport>()
        merged.forEach { e ->
            val key = "${e.year}_${e.week}"
            val existing = byWeek[key]
            if (existing == null || e.ts > existing.ts) byWeek[key] = e
        }
        return byWeek.values.sortedBy { it.ts }.takeLast(maxEntries)
    }

    /** ওয়েব অ্যাপের _delIdsMerge() — দুই is_deleted_ids ম্যাপ union করে (প্রতিটা key-র লিস্ট মার্জ) */
    fun mergeTombstones(
        local: Map<String, List<Long>>,
        remote: Map<String, List<Long>>
    ): Map<String, List<Long>> {
        val result = local.mapValues { it.value.toMutableSet() }.toMutableMap()
        remote.forEach { (key, ids) ->
            val set = result.getOrPut(key) { mutableSetOf() }
            set.addAll(ids)
        }
        return result.mapValues { it.value.toList() }
    }

    /**
     * পুরো payload merge — dbPush()-এর সম্পূর্ণ ব্লকের সমতুল্য: আগে tombstone union,
     * তারপর প্রতিটা ফিল্ড তার নিজস্ব merge কৌশল দিয়ে (id-ভিত্তিক বা ts-ভিত্তিক)।
     * profitStripData_v1-এর জন্য single-object last-write-wins — local অগ্রাধিকার পায়
     * (যেহেতু local-ই সাম্প্রতিক ব্যবহারকারীর কার্যকলাপ প্রতিফলিত করে সাধারণত)।
     */
    fun merge(local: BackupPayload, remote: BackupPayload): BackupPayload {
        val mergedDeleted = mergeTombstones(local.is_deleted_ids, remote.is_deleted_ids)
        fun deadSetFor(key: String): Set<Long> = (mergedDeleted[key] ?: emptyList()).toSet()

        return BackupPayload(
            secret = local.secret,
            updatedAt = System.currentTimeMillis(),
            is_c1_list = mergeById(local.is_c1_list, remote.is_c1_list, { it.id }, deadSetFor("is_c1_list")),
            is_c2_list = mergeById(local.is_c2_list, remote.is_c2_list, { it.id }, deadSetFor("is_c2_list")),
            is_c3_list = mergeById(local.is_c3_list, remote.is_c3_list, { it.id }, deadSetFor("is_c3_list")),
            is_c4_list = mergeById(local.is_c4_list, remote.is_c4_list, { it.id }, deadSetFor("is_c4_list")),
            fabricPurchaseData_v2 = mergeFabricGroups(
                local.fabricPurchaseData_v2, remote.fabricPurchaseData_v2,
                deadSetFor("fabricPurchaseData_v2"), deadSetFor("fabricPurchaseData_v2_purchases")
            ),
            chart_data = mergeWeeklyReportsByTs(local.chart_data, remote.chart_data, deadSetFor("chart_data")),
            profitStripData_v1 = local.profitStripData_v1 ?: remote.profitStripData_v1,
            is_deleted_ids = mergedDeleted
        )
    }
}
