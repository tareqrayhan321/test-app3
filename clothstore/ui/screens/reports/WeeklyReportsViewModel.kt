package com.imran.clothstore.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.backup.weeklyReports
import com.imran.clothstore.data.model.WeeklyReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Aggregate Report, Net Profit Breakdown, P&L Trends — তিনটা ওভারলেই একই ডেটা সোর্স
 * (imran_store/backup ডকুমেন্টের chart_data ফিল্ড) ব্যবহার করে, তাই একটা শেয়ার্ড ViewModel
 * দিয়ে সবগুলো চালানো হচ্ছে।
 */
class WeeklyReportsViewModel(
    private val repository: BackupRepository = BackupRepository()
) : ViewModel() {

    private val _reports = MutableStateFlow<List<WeeklyReport>>(emptyList())

    /** নতুন থেকে পুরাতন সাজানো (ts descending) */
    val reportsNewestFirst: StateFlow<List<WeeklyReport>> = _reports
        .map { list -> list.sortedByDescending { it.ts } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** পুরাতন থেকে নতুন সাজানো (ts ascending) — বার চার্টের জন্য */
    val reportsOldestFirst: StateFlow<List<WeeklyReport>> = _reports
        .map { list -> list.sortedBy { it.ts } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.observeBackup().collect { payload -> _reports.value = payload.weeklyReports() }
        }
    }

    fun deleteReport(reportId: Long) {
        viewModelScope.launch {
            repository.deleteWeeklyReport(reportId)
        }
    }
}
