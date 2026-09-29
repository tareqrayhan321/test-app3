package com.imran.clothstore.data.sync

import com.imran.clothstore.data.backup.BackupEntry
import com.imran.clothstore.data.backup.BackupFabricGroup
import com.imran.clothstore.data.backup.BackupFabricPurchase
import com.imran.clothstore.data.backup.BackupWeeklyReport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncEngineTest {

    // ── mergeById: normal — local ও remote-এর ভিন্ন id একসাথে union হবে ──
    @Test
    fun mergeById_normal_unionsDistinctIds() {
        val local = listOf(BackupEntry(id = 1, name = "A"))
        val remote = listOf(BackupEntry(id = 2, name = "B"))
        val result = SyncEngine.mergeById(local, remote, { it.id }, emptySet())
        assertEquals(setOf(1L, 2L), result.map { it.id }.toSet())
    }

    // ── mergeById: boundary — খালি local + খালি remote → খালি ফলাফল ──
    @Test
    fun mergeById_boundary_bothEmpty() {
        val result = SyncEngine.mergeById(emptyList<BackupEntry>(), emptyList(), { it.id }, emptySet())
        assertTrue(result.isEmpty())
    }

    // ── mergeById: conflict — একই id local ও remote দুই জায়গায় থাকলে local জেতে (ওভাররাইট হয় না) ──
    @Test
    fun mergeById_conflict_localWinsOnSameId() {
        val local = listOf(BackupEntry(id = 1, name = "স্থানীয় সংস্করণ"))
        val remote = listOf(BackupEntry(id = 1, name = "দূরবর্তী সংস্করণ"))
        val result = SyncEngine.mergeById(local, remote, { it.id }, emptySet())
        assertEquals(1, result.size)
        assertEquals("স্থানীয় সংস্করণ", result.first().name)
    }

    // ── mergeById: tombstone — dead id হলে remote থেকে ফিরে আসবে না ──
    @Test
    fun mergeById_tombstonedRemoteId_excluded() {
        val local = listOf(BackupEntry(id = 1, name = "থাকা"))
        val remote = listOf(BackupEntry(id = 1, name = "থাকা"), BackupEntry(id = 2, name = "ডিলিটেড"))
        val result = SyncEngine.mergeById(local, remote, { it.id }, setOf(2L))
        assertEquals(setOf(1L), result.map { it.id }.toSet())
    }

    // ── mergeFabricGroups: normal — একই id-র গ্রুপের ভেতরের purchases merge হবে ──
    @Test
    fun mergeFabricGroups_normal_mergesPurchasesWithinSameGroup() {
        val local = listOf(
            BackupFabricGroup(id = 1, name = "কটন", purchases = listOf(BackupFabricPurchase(id = 10)))
        )
        val remote = listOf(
            BackupFabricGroup(id = 1, name = "কটন", purchases = listOf(BackupFabricPurchase(id = 11)))
        )
        val result = SyncEngine.mergeFabricGroups(local, remote, emptySet(), emptySet())
        assertEquals(1, result.size)
        assertEquals(setOf(10L, 11L), result.first().purchases.map { it.id }.toSet())
    }

    // ── mergeFabricGroups: boundary — case-sensitivity-এর বাইরে, একদম নতুন গ্রুপ id হলে যোগ হবে ──
    @Test
    fun mergeFabricGroups_boundary_newRemoteGroupAdded() {
        val local = emptyList<BackupFabricGroup>()
        val remote = listOf(BackupFabricGroup(id = 5, name = "সিল্ক"))
        val result = SyncEngine.mergeFabricGroups(local, remote, emptySet(), emptySet())
        assertEquals(1, result.size)
        assertEquals(5L, result.first().id)
    }

    // ── mergeFabricGroups: tombstone — group tombstone এর ভেতরের purchase tombstone আলাদাভাবে কাজ করবে ──
    @Test
    fun mergeFabricGroups_purchaseTombstone_excludesDeadPurchaseOnly() {
        val local = listOf(BackupFabricGroup(id = 1, purchases = listOf(BackupFabricPurchase(id = 100))))
        val remote = listOf(
            BackupFabricGroup(id = 1, purchases = listOf(BackupFabricPurchase(id = 100), BackupFabricPurchase(id = 101)))
        )
        val result = SyncEngine.mergeFabricGroups(local, remote, emptySet(), deadPurchaseIds = setOf(101L))
        assertEquals(setOf(100L), result.first().purchases.map { it.id }.toSet())
    }

    // ── mergeWeeklyReportsByTs: normal — ভিন্ন ts, ভিন্ন সপ্তাহ → দুটোই থাকবে ──
    @Test
    fun mergeWeeklyReportsByTs_normal_keepsDistinctWeeks() {
        val local = listOf(BackupWeeklyReport(ts = 100, year = 2026, week = 1))
        val remote = listOf(BackupWeeklyReport(ts = 200, year = 2026, week = 2))
        val result = SyncEngine.mergeWeeklyReportsByTs(local, remote, emptySet())
        assertEquals(2, result.size)
    }

    // ── mergeWeeklyReportsByTs: boundary — ২৬-এর বেশি এন্ট্রি হলে সবচেয়ে পুরাতনগুলো বাদ পড়বে ──
    @Test
    fun mergeWeeklyReportsByTs_boundary_capsAt26KeepingNewest() {
        val local = (1..30).map { BackupWeeklyReport(ts = it.toLong(), year = 2026, week = it) }
        val result = SyncEngine.mergeWeeklyReportsByTs(local, emptyList(), emptySet(), maxEntries = 26)
        assertEquals(26, result.size)
        assertEquals(5L, result.first().ts) // ১..৪ বাদ পড়েছে, সবচেয়ে পুরনো রাখা ৫
        assertEquals(30L, result.last().ts)
    }

    // ── mergeWeeklyReportsByTs: conflict — একই year+week-এ দুইটা ts থাকলে সবচেয়ে নতুনটা থাকবে ──
    @Test
    fun mergeWeeklyReportsByTs_conflict_sameWeekKeepsNewestTs() {
        val local = listOf(BackupWeeklyReport(ts = 100, year = 2026, week = 1, sales = 500.0))
        val remote = listOf(BackupWeeklyReport(ts = 200, year = 2026, week = 1, sales = 900.0))
        val result = SyncEngine.mergeWeeklyReportsByTs(local, remote, emptySet())
        assertEquals(1, result.size)
        assertEquals(900.0, result.first().sales, 0.001)
    }

    // ── mergeTombstones: normal — দুই ভিন্ন key একসাথে union হবে ──
    @Test
    fun mergeTombstones_normal_unionsAcrossKeys() {
        val local = mapOf("is_c1_list" to listOf(1L, 2L))
        val remote = mapOf("is_c1_list" to listOf(2L, 3L), "is_c2_list" to listOf(9L))
        val result = SyncEngine.mergeTombstones(local, remote)
        assertEquals(setOf(1L, 2L, 3L), result["is_c1_list"]!!.toSet())
        assertEquals(setOf(9L), result["is_c2_list"]!!.toSet())
    }

    // ── mergeTombstones: boundary — উভয়ই খালি ম্যাপ ──
    @Test
    fun mergeTombstones_boundary_emptyMaps() {
        val result = SyncEngine.mergeTombstones(emptyMap(), emptyMap())
        assertTrue(result.isEmpty())
    }
}
