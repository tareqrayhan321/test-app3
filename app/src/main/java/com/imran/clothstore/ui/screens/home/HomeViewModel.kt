package com.imran.clothstore.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.backup.entriesFor
import com.imran.clothstore.data.model.CategorySummary
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.util.formatTaka
import com.imran.clothstore.util.toBengaliDigits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * হোম ড্যাশবোর্ড স্ক্রিনের জন্য ViewModel — imran_store/backup ডকুমেন্ট থেকে
 * প্রতিটা ক্যাটাগরির সামারি (মোট জন · টাকার পরিমাণ) রিয়েল-টাইম বের করে।
 *
 * PARTY (পার্টি) কার্ডের নিজস্ব কোনো তালিকা নেই — ওয়েব অ্যাপের মতোই এটা বাকি চারটা
 * ক্যাটাগরির সম্মিলিত সামারি দেখায় (partyOpen() → Party Ledger)।
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

    init {
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

    /** একটা নির্দিষ্ট ক্যাটাগরির সামারি টেক্সট, অথবা PARTY হলে চারটার সম্মিলিত সামারি */
    fun formatSummary(category: EntryCategory): String = formatSummary(category, _summaries.value)

    /** Compose থেকে State-এর মান পাস করে ডাকার জন্য — যাতে সামারি বদলালে কার্ড recompose হয় */
    fun formatSummary(category: EntryCategory, summaries: Map<EntryCategory, CategorySummary>): String {
        if (category == EntryCategory.PARTY) {
            val all = summaries.values
            val totalCount = all.sumOf { it.totalCount }
            val totalAmount = all.sumOf { it.totalAmount }
            return "মোট ${totalCount.toBengaliDigits()} জন · ${formatTaka(totalAmount)}"
        }
        val summary = summaries[category] ?: return "মোট ০ জন · ৳০"
        return "মোট ${summary.totalCount.toBengaliDigits()} জন · ${formatTaka(summary.totalAmount)}"
    }
}
