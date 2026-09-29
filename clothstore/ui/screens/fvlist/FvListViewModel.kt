package com.imran.clothstore.ui.screens.fvlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.backup.entriesFor
import com.imran.clothstore.data.backup.toBackupEntry
import com.imran.clothstore.data.model.Entry
import com.imran.clothstore.data.model.EntryCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * fvScreen (ফুল স্ক্রিন লিস্ট ভিউ) এর ViewModel।
 * মূল ওয়েব অ্যাপের fvFilterRender() লজিক অনুসরণ করে — সার্চ, মোট বাকির যোগফল, CRUD।
 * পুরো imran_store/backup ডকুমেন্ট থেকে এই ক্যাটাগরির array বের করে ব্যবহার করে
 * (আলাদা কালেকশন থেকে নয়)।
 */
class FvListViewModel(
    private val category: EntryCategory,
    private val repository: BackupRepository = BackupRepository()
) : ViewModel() {

    private val _allEntries = MutableStateFlow<List<Entry>>(emptyList())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedForAction = MutableStateFlow<Entry?>(null)
    val selectedForAction: StateFlow<Entry?> = _selectedForAction.asStateFlow()

    private val _editingEntry = MutableStateFlow<Entry?>(null)
    val editingEntry: StateFlow<Entry?> = _editingEntry.asStateFlow()

    private val _isFormOpen = MutableStateFlow(false)
    val isFormOpen: StateFlow<Boolean> = _isFormOpen.asStateFlow()

    val filteredEntries: StateFlow<List<Entry>> = combine(_allEntries, _searchQuery) { all, q ->
        val query = q.trim().lowercase()
        if (query.isEmpty()) {
            all
        } else {
            all.filter {
                it.name.lowercase().contains(query) ||
                    it.addr.lowercase().contains(query) ||
                    it.mob.lowercase().contains(query) ||
                    it.memo.lowercase().contains(query)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCount: StateFlow<Int> = _allEntries
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalBaki: StateFlow<Double> = _allEntries
        .map { list -> list.sumOf { it.baki } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        viewModelScope.launch {
            repository.observeBackup().collect { payload ->
                _allEntries.value = payload.entriesFor(category)
            }
        }
    }

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun openNewEntryForm() {
        _editingEntry.value = null
        _isFormOpen.value = true
    }

    fun openEditForm(entry: Entry) {
        _editingEntry.value = entry
        _isFormOpen.value = true
        _selectedForAction.value = null
    }

    fun closeForm() {
        _isFormOpen.value = false
        _editingEntry.value = null
    }

    fun selectForAction(entry: Entry?) {
        _selectedForAction.value = entry
    }

    /** epSaveCustomer / epSaveSupplier এর সমতুল্য — bill/baki - joma থেকে নীট বাকি হিসাব করে সেভ করে */
    fun saveEntry(
        name: String,
        addr: String,
        mob: String,
        memo: String,
        billOrBaki: Double,
        joma: Double,
        goj: String,
        note: String,
        date: String,
        photoUrl: String
    ) {
        if (name.isBlank() && billOrBaki == 0.0) return
        val netBaki = maxOf(0.0, billOrBaki - joma)
        val mobFormatted = if (mob.isNotBlank() && !mob.startsWith("+88")) "+88$mob" else mob

        val existing = _editingEntry.value
        // initPawna: প্রথমবার তৈরির সময় (existing == null) একবারই baki+joma থেকে হিসেব করে
        // সেভ হয় এবং তারপর আর কখনো বদলায় না (এডিটে existing.initPawna অপরিবর্তিত থাকে) —
        // ওয়েব অ্যাপের item.initPawna প্যাটার্ন অনুসরণ করে।
        val initPawna = existing?.initPawna ?: (netBaki + joma)
        val entry = Entry(
            id = existing?.id ?: System.currentTimeMillis(),
            name = name.trim(),
            addr = addr.trim(),
            mob = mobFormatted,
            memo = memo.trim(),
            bill = billOrBaki,
            joma = joma,
            baki = netBaki,
            initPawna = initPawna,
            goj = goj.trim(),
            note = note.trim(),
            date = date.ifBlank { existing?.date ?: "" },
            photoUrl = photoUrl,
            order = existing?.order ?: 0L,
            history = existing?.history ?: emptyList()
        )

        viewModelScope.launch {
            repository.addOrUpdateEntry(category, entry.toBackupEntry())
        }
        closeForm()
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            repository.deleteEntry(category, entry.id)
        }
        _selectedForAction.value = null
    }
}
