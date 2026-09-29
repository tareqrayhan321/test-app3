package com.imran.clothstore.ui.screens.fabric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.data.backup.BackupFabricPurchase
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.backup.fabricGroups
import com.imran.clothstore.data.model.FabricGroup
import com.imran.clothstore.data.model.FabricPurchase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ওয়েব অ্যাপের fabric মডিউলের (fabricSave/fabricDcaSave/fabricDeletePurchase) ViewModel।
 * imran_store/backup ডকুমেন্টের fabricPurchaseData_v2 ফিল্ড থেকে ডেটা আসে।
 */
class FabricViewModel(
    private val repository: BackupRepository = BackupRepository()
) : ViewModel() {

    private val _allGroups = MutableStateFlow<List<FabricGroup>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredGroups: StateFlow<List<FabricGroup>> = combine(_allGroups, _searchQuery) { groups, q ->
        val query = q.trim().lowercase()
        if (query.isEmpty()) groups else groups.filter { it.name.lowercase().contains(query) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** fabricUpdateCard() এর সমতুল্য — হোম কার্ডে দেখানো "X টি | ৳Y" সামারি */
    val cardSummaryText: StateFlow<String> = _allGroups
        .map { groups ->
            if (groups.isEmpty()) {
                "ক্রয় তালিকা"
            } else {
                val total = groups.sumOf { it.totalAmount }
                val count = groups.sumOf { it.purchases.size }
                "${count}টি | ৳${total.toLong()}"
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ক্রয় তালিকা")

    init {
        viewModelScope.launch {
            repository.observeBackup().collect { payload -> _allGroups.value = payload.fabricGroups() }
        }
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    /** fabricSave() এর সমতুল্য — নতুন কাপড়ের নাম/গ্রুপ বা বিদ্যমান গ্রুপে প্রথম purchase */
    fun saveNewPurchase(name: String, qty: Double, unit: String, rate: Double, onError: (String) -> Unit) {
        if (name.isBlank()) { onError("কাপড়ের নাম লিখুন"); return }
        if (qty <= 0) { onError("পরিমাণ দিন"); return }
        if (rate <= 0) { onError("একক দর দিন"); return }

        val purchase = BackupFabricPurchase(
            id = System.currentTimeMillis(),
            date = isoNow(),
            qty = qty,
            rate = rate,
            total = qty * rate
        )
        viewModelScope.launch {
            repository.addFabricPurchase(name.trim(), unit, purchase)
        }
    }

    /** fabricDcaSave() এর সমতুল্য — বিদ্যমান গ্রুপে আরও একটা purchase যোগ করে */
    fun addPurchaseToGroup(group: FabricGroup, qty: Double, rate: Double, onError: (String) -> Unit) {
        if (qty <= 0) { onError("পরিমাণ দিন"); return }
        if (rate <= 0) { onError("দর দিন"); return }

        val purchase = BackupFabricPurchase(
            id = System.currentTimeMillis(),
            date = isoNow(),
            qty = qty,
            rate = rate,
            total = qty * rate
        )
        viewModelScope.launch {
            repository.addPurchaseToGroup(group.id, purchase)
        }
    }

    fun deletePurchase(group: FabricGroup, purchase: FabricPurchase) {
        viewModelScope.launch {
            repository.deleteFabricPurchase(group.id, purchase.id)
        }
    }

    private fun isoNow(): String =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
}
