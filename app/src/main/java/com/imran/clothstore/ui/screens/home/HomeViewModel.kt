package com.imran.clothstore.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.backup.entriesFor
import com.imran.clothstore.data.model.CategorySummary
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.util.formatTaka
import com.imran.clothstore.util.toBengaliDigits
import com.imran.clothstore.AppSingletons
import com.imran.clothstore.data.sync.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * হোম ড্যাশবোর্ড স্ক্রিনের জন্য ViewModel — imran_store/backup ডকুমেন্ট থেকে
 * প্রতিটা ক্যাটাগরির সামারি (মোট জন · টাকার পরিমাণ) রিয়েল-টাইম বের করে।
 *
 * PARTY (পার্টি) কার্ডের নিজস্ব কোনো তালিকা নেই — ওয়েব অ্যাপের মতোই এটা রেগুলার + ইর-রেগুলার
 * কাস্টমারের সম্মিলিত সামারি দেখায় (partyOpen() → Party Ledger)।
 */
class HomeViewModel(
    private val repository: BackupRepository = BackupRepository()
) : ViewModel() {

    private val trackedCategories = listOf(
        EntryCategory.REGULAR_CUSTOMER,
        EntryCategory.IRREGULAR_CUSTOMER,
        EntryCategory.REGULAR_SUPPLIER,
        EntryCategory.IRREGULAR_SUPPLIER
    )

    private val _summaries = MutableStateFlow<Map<EntryCategory, CategorySummary>>(emptyMap())
    val summaries: StateFlow<Map<EntryCategory, CategorySummary>> = _summaries.asStateFlow()

    private val _dbConnected = MutableStateFlow<Boolean?>(null) // null = যাচাই হচ্ছে
    val dbConnected: StateFlow<Boolean?> = _dbConnected.asStateFlow()

    /** ডাটাবেইজ কার্ডের নিচের লেখা — "Real-time বন্ধ · সর্বশেষ সিঙ্ক ২৮/৯/২০২৬ ১০:২২ PM" */
    val dbSubtitle: StateFlow<String> = combine(SyncStatus.realtimeActive, SyncStatus.lastSyncMs) { realtime, lastMs ->
        formatDbSubtitle(realtime, lastMs)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    init {
        AppSingletons.appContext?.let { SyncStatus.load(it) }
        viewModelScope.launch {
            repository.observeBackup().collect { payload ->
                _summaries.value = trackedCategories.associateWith { category ->
                    val entries = payload.entriesFor(category)
                    CategorySummary(category, entries.size, entries.sumOf { it.baki })
                }
                _dbConnected.value = true
            }
        }
    }

    /** একটা নির্দিষ্ট ক্যাটাগরির সামারি টেক্সট, অথবা PARTY হলে দুই কাস্টমার ক্যাটাগরির সম্মিলিত সামারি */
    fun formatSummary(category: EntryCategory): String = formatSummary(category, _summaries.value)

    private fun formatDbSubtitle(realtime: Boolean, lastSyncMs: Long): String {
        val rt = if (realtime) "Real-time চালু" else "Real-time বন্ধ"
        if (lastSyncMs <= 0L) return "$rt · এখনো সিঙ্ক হয়নি"
        val c = Calendar.getInstance().apply { timeInMillis = lastSyncMs }
        val hour12 = c.get(Calendar.HOUR).let { if (it == 0) 12 else it }
        val minute = c.get(Calendar.MINUTE).toString().padStart(2, '0')
        val ampm = if (c.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
        val date = "${c.get(Calendar.DAY_OF_MONTH)}/${c.get(Calendar.MONTH) + 1}/${c.get(Calendar.YEAR)}"
        return "$rt · সর্বশেষ সিঙ্ক ${"$date $hour12:$minute".toBengaliDigits()} $ampm"
    }

    /** Compose থেকে State-এর মান পাস করে ডাকার জন্য — যাতে সামারি বদলালে কার্ড recompose হয় */
    fun formatSummary(category: EntryCategory, summaries: Map<EntryCategory, CategorySummary>): String {
        if (category == EntryCategory.PARTY) {
            val all = listOf(EntryCategory.REGULAR_CUSTOMER, EntryCategory.IRREGULAR_CUSTOMER)
                .mapNotNull { summaries[it] }
            val totalCount = all.sumOf { it.totalCount }
            val totalAmount = all.sumOf { it.totalAmount }
            return "মোট ${totalCount.toBengaliDigits()} জন · ${formatTaka(totalAmount)}"
        }
        val summary = summaries[category] ?: return "মোট ০ জন · ৳০"
        return "মোট ${summary.totalCount.toBengaliDigits()} জন · ${formatTaka(summary.totalAmount)}"
    }
}
