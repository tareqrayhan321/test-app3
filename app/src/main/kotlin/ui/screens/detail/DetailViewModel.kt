package com.imran.clothstore.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.backup.entriesFor
import com.imran.clothstore.data.backup.toBackupEntry
import com.imran.clothstore.data.model.Entry
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.data.model.HistoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * fvDetailScreen এর ViewModel — fvDetailSaveBokeyoa()/fvDetailSaveJoma() এর সমতুল্য।
 * নতুন বকেয়া/জমা যোগ করার পর সম্পূর্ণ bill/joma/baki recalculate করে।
 *
 * entryId (Long) দিয়ে পুরো imran_store/backup payload observe করে সেখান থেকে এই
 * নির্দিষ্ট এন্ট্রিটা filter করে বের করে — যাতে অন্য ডিভাইসে হওয়া পরিবর্তনও সাথে সাথে প্রতিফলিত হয়।
 */
class DetailViewModel(
    private val category: EntryCategory,
    private val entryId: Long,
    initialEntry: Entry,
    private val repository: BackupRepository = BackupRepository()
) : ViewModel() {

    private val _entry = MutableStateFlow(initialEntry)
    val entry: StateFlow<Entry> = _entry.asStateFlow()

    val transactionRows: StateFlow<List<TxnRow>> = MutableStateFlow(
        buildTransactionRows(initialEntry, category.isCustomerType)
    ).also { state ->
        viewModelScope.launch {
            _entry.collect { e -> state.value = buildTransactionRows(e, category.isCustomerType) }
        }
    }.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeBackup().collect { payload ->
                val updated = payload.entriesFor(category).firstOrNull { it.id == entryId }
                if (updated != null) _entry.value = updated
            }
        }
    }

    /** নতুন বকেয়া/বিল (ও ঐচ্ছিক সাথে জমা) যোগ করে — কাস্টমার ও মহাজন উভয়ের জন্য */
    fun addBokeyoa(memo: String, date: String, goj: String, billAmount: Double, note: String, jomaAmount: Double) {
        val current = _entry.value
        val newHistory = current.history.toMutableList()
        // ওয়েব অ্যাপের মতোই: কাস্টমার হলে bill ফিল্ডে, মহাজন হলে baki ফিল্ডে বকেয়ার পরিমাণ রাখা হয়
        val historyItem = if (category.isCustomerType) {
            HistoryItem(type = "bokeyoa", date = date, memo = memo, bill = billAmount, goj = goj, note = note)
        } else {
            HistoryItem(type = "bokeyoa", date = date, memo = memo, baki = billAmount, goj = goj, note = note)
        }
        newHistory.add(historyItem)
        if (jomaAmount > 0) {
            newHistory.add(HistoryItem(type = "joma", date = date, memo = memo, joma = jomaAmount))
        }
        recalculateAndSave(current.copy(history = newHistory))
    }

    /** শুধু জমা যোগ করে */
    fun addJoma(memo: String, date: String, jomaAmount: Double, note: String) {
        val current = _entry.value
        val newHistory = current.history.toMutableList()
        newHistory.add(HistoryItem(type = "joma", date = date, memo = memo, joma = jomaAmount, note = note))
        recalculateAndSave(current.copy(history = newHistory))
    }

    /** history-সহ পুরো এন্ট্রি থেকে মোট bill/joma/baki recalculate করে সেভ করে */
    private fun recalculateAndSave(updated: Entry) {
        val isCustomer = category.isCustomerType
        val totalBill = updated.bill + updated.history
            .filter { it.type == "bokeyoa" }
            .sumOf { it.bokeyoaAmount(isCustomer) }
        val totalJoma = updated.joma + updated.history.filter { it.type == "joma" }.sumOf { it.joma }
        val netBaki = maxOf(0.0, totalBill - totalJoma)

        val finalEntry = updated.copy(baki = netBaki)
        _entry.value = finalEntry

        viewModelScope.launch {
            repository.addOrUpdateEntry(category, finalEntry.toBackupEntry())
        }
    }
}
