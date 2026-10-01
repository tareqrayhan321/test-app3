package com.imran.clothstore.data.sync

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * হোমের "ডাটাবেইজ" কার্ডে দেখানোর জন্য সিঙ্ক-অবস্থা — সর্বশেষ সফল সিঙ্কের সময় (SharedPreferences-এ
 * রাখা, অ্যাপ বন্ধ করলেও থাকে) এবং Firestore real-time listener চালু আছে কি না।
 */
object SyncStatus {
    private const val PREFS = "sync_status"
    private const val KEY_LAST_SYNC = "last_sync_ms"

    private val _lastSyncMs = MutableStateFlow(0L)
    val lastSyncMs: StateFlow<Long> = _lastSyncMs.asStateFlow()

    private val _realtimeActive = MutableStateFlow(false)
    val realtimeActive: StateFlow<Boolean> = _realtimeActive.asStateFlow()

    /** সেভ করা সময়টা প্রথমবার পড়ে (অ্যাপ চালুর পর) — নতুন মান আগেই এসে থাকলে সেটা বদলায় না */
    fun load(context: Context) {
        if (_lastSyncMs.value == 0L) {
            _lastSyncMs.value = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(KEY_LAST_SYNC, 0L)
        }
    }

    /** SyncWorker সফলভাবে শেষ হলে ডাকা হয় */
    fun markSynced(context: Context) {
        val now = System.currentTimeMillis()
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putLong(KEY_LAST_SYNC, now).apply()
        _lastSyncMs.value = now
    }

    fun setRealtimeActive(active: Boolean) {
        _realtimeActive.value = active
    }
}
