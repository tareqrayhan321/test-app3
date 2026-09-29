package com.imran.clothstore.ui.screens.dbconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imran.clothstore.AppSingletons
import com.imran.clothstore.data.backup.BackupRepository
import com.imran.clothstore.data.sync.SyncWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** dbDot এর তিনটা অবস্থার সমতুল্য — connected / connecting / error */
enum class DbConnectionState { CONNECTING, CONNECTED, ERROR }

/**
 * ওয়েব অ্যাপের dbOverlay এর ViewModel। সংযোগ-স্ট্যাটাস যাচাই ও ঝুঁকিপূর্ণ
 * "ক্লাউড ডেটা মুছে ফেলা" (dbConfirmWipe) — এখন imran_store/backup ডকুমেন্ট লক্ষ্য করে,
 * আগের ভুল per-category কালেকশন কাঠামোর বদলে।
 */
class DbConnectViewModel(
    private val repository: BackupRepository = BackupRepository()
) : ViewModel() {

    private val _connectionState = MutableStateFlow(DbConnectionState.CONNECTING)
    val connectionState: StateFlow<DbConnectionState> = _connectionState.asStateFlow()

    private val _lastSyncText = MutableStateFlow("")
    val lastSyncText: StateFlow<String> = _lastSyncText.asStateFlow()

    private val _isWiping = MutableStateFlow(false)
    val isWiping: StateFlow<Boolean> = _isWiping.asStateFlow()

    init {
        checkConnection()
    }

    fun checkConnection() {
        _connectionState.value = DbConnectionState.CONNECTING
        viewModelScope.launch {
            try {
                // Room cache খালি থাকলেও যেন "সংযুক্ত" বলে ভুল না দেখায়—সরাসরি cloud read করি।
                val remote = repository.fetchRemoteOnce()
                AppSingletons.appContext?.let { context -> SyncWorker.triggerImmediate(context) }
                val partyCount = remote.is_c1_list.size + remote.is_c2_list.size +
                    remote.is_c3_list.size + remote.is_c4_list.size
                _connectionState.value = DbConnectionState.CONNECTED
                _lastSyncText.value = "ক্লাউডে পার্টি: $partyCount · সাপ্তাহিক রিপোর্ট: ${remote.chart_data.size}; সিঙ্ক অনুরোধ করা হয়েছে"
            } catch (e: Exception) {
                _connectionState.value = DbConnectionState.ERROR
                _lastSyncText.value = "সংযোগ ব্যর্থ: ${e.message}"
            }
        }
    }

    companion object {
        /** ওয়েব অ্যাপের dbConfirmWipe()-এ ব্যবহৃত _FB_SECRET ('1212') এর সমতুল্য —
         *  ভুল কী দিলে wipe হবে না। */
        const val WIPE_SECRET = "1212"
    }

    /**
     * dbConfirmWipe() এর সমতুল্য — imran_store/backup ডকুমেন্ট সম্পূর্ণ খালি করে দেয়,
     * কিন্তু শুধুমাত্র enteredSecret সঠিক হলে। ভুল কী দিলে wipe না করেই false রিটার্ন করে।
     */
    fun wipeAllCloudData(enteredSecret: String, onComplete: (success: Boolean, wrongSecret: Boolean) -> Unit) {
        if (enteredSecret != WIPE_SECRET) {
            onComplete(false, true)
            return
        }
        _isWiping.value = true
        viewModelScope.launch {
            try {
                repository.wipeAll()
                com.imran.clothstore.ui.screens.notif.NotificationCenter.push(
                    "error", "ক্লাউডের সব ডেটা মুছে ফেলা হয়েছে"
                )
                _isWiping.value = false
                onComplete(true, false)
            } catch (e: Exception) {
                _isWiping.value = false
                onComplete(false, false)
            }
        }
    }
}
