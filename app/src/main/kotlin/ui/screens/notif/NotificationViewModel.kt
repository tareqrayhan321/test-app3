package com.imran.clothstore.ui.screens.notif

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import com.imran.clothstore.data.model.Notification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * ওয়েব অ্যাপের নোটিফিকেশন সিস্টেম (notifOpen/notifClose/notifClear) এর ViewModel।
 *
 * গুরুত্বপূর্ণ: ওয়েব অ্যাপে app_notifications localStorage-এ থাকে এবং Firestore-এ কখনো
 * sync হয় না (_FB_KEYS/_FB_ARR_KEYS/_FB_OBJ_KEYS কোনোটাতেই নেই) — প্রতিটা ডিভাইসে আলাদা।
 * তাই এখানে Firestore-এ না রেখে NotificationCenter ব্যবহার করা হয়েছে — এটাই ওয়েব অ্যাপের
 * "শুধু এই ডিভাইসে" আচরণের সবচেয়ে কাছের সমতুল্য। কিন্তু NotificationCenter এখন Jetpack
 * DataStore দিয়ে backed — অ্যাপ বন্ধ/মিনিমাইজ করলে বা Android প্রসেস কিল করলেও তালিকা
 * টিকে থাকে (localStorage যেমন ব্রাউজার/ট্যাব বন্ধ করলে খালি হয় না, শুধু ব্যবহারকারী
 * স্পষ্টভাবে ডেটা মুছলে হয় — তার সাথে সামঞ্জস্যপূর্ণ)।
 */
class NotificationViewModel(
    private val center: NotificationCenter = NotificationCenter
) : ViewModel() {

    val notifications: StateFlow<List<Notification>> = center.notifications
    val hasUnread: StateFlow<Boolean> = center.hasUnread

    fun onPanelOpened() {
        center.markAllSeen()
    }

    fun clearAll() {
        center.clearAll()
    }

    fun push(type: String, msg: String) {
        center.push(type, msg)
    }
}

private val Context.notifDataStore by preferencesDataStore(name = "notifications")

/**
 * DataStore-backed নোটিফিকেশন সেন্টার — অ্যাপের যেকোনো জায়গা থেকে NotificationCenter.push(...)
 * কল করে নোটিফিকেশন যোগ করা যায়। init(context) প্রথমে ClothStoreApplication.onCreate()-এ
 * একবার কল হয়ে ডিস্কে থাকা তালিকা লোড করে; তারপর প্রতিটা push/markAllSeen/clearAll ডিস্কে
 * async ভাবে লিখে রাখে। init() কল হওয়ার আগে push() করা হলে (তাত্ত্বিকভাবে সম্ভব না, যেহেতু
 * Application.onCreate() UI-এর আগে চলে) সেই এন্ট্রি শুধু in-memory থাকবে যতক্ষণ না init হয়।
 */
object NotificationCenter {
    private const val KEY_NAME = "notifications_json"
    private val NOTIFS_KEY = stringPreferencesKey(KEY_NAME)
    // markAllSeen কল হওয়ার পর নতুন push না হওয়া পর্যন্ত hasUnread false থাকা উচিত —
    // ডিস্ক-রিস্টার্টের পরেও hasUnread এর সর্বশেষ মান মনে রাখতে আলাদা ফ্ল্যাগ persist করা হয়
    private val SEEN_KEY = androidx.datastore.preferences.core.booleanPreferencesKey("all_seen_flag")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _hasUnread = MutableStateFlow(false)
    val hasUnread: StateFlow<Boolean> = _hasUnread.asStateFlow()

    private var appContext: Context? = null
    private var allSeenFlag = false

    /** ClothStoreApplication.onCreate() থেকে একবার কল হয় — ডিস্ক থেকে আগের নোটিফিকেশন লোড করে */
    fun init(context: Context) {
        if (appContext != null) return // দ্বিতীয়বার init হলে এড়িয়ে যাওয়া (আইডেম্পোটেন্ট)
        appContext = context.applicationContext
        scope.launch {
            val prefs = appContext!!.notifDataStore.data.first()
            val json = prefs[NOTIFS_KEY]
            allSeenFlag = prefs[SEEN_KEY] ?: false
            if (!json.isNullOrBlank()) {
                _notifications.value = decodeNotifications(json)
                _hasUnread.value = _notifications.value.isNotEmpty() && !allSeenFlag
            }
        }
    }

    fun push(type: String, msg: String) {
        val notif = Notification(
            id = System.currentTimeMillis().toString(),
            type = type,
            msg = msg,
            ts = System.currentTimeMillis()
        )
        _notifications.value = (listOf(notif) + _notifications.value).take(50)
        _hasUnread.value = true
        allSeenFlag = false
        persist()
    }

    fun markAllSeen() {
        _hasUnread.value = false
        allSeenFlag = true
        persist()
    }

    fun clearAll() {
        _notifications.value = emptyList()
        _hasUnread.value = false
        allSeenFlag = false
        persist()
    }

    private fun persist() {
        val ctx = appContext ?: return // init() না হলে শুধু in-memory থাকবে
        val json = encodeNotifications(_notifications.value)
        val seen = allSeenFlag
        scope.launch {
            ctx.notifDataStore.edit { prefs ->
                prefs[NOTIFS_KEY] = json
                prefs[SEEN_KEY] = seen
            }
        }
    }

    private fun encodeNotifications(list: List<Notification>): String {
        val arr = JSONArray()
        list.forEach { n ->
            val obj = JSONObject()
            obj.put("id", n.id)
            obj.put("type", n.type)
            obj.put("msg", n.msg)
            obj.put("ts", n.ts)
            arr.put(obj)
        }
        return arr.toString()
    }

    private fun decodeNotifications(json: String): List<Notification> {
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                Notification(
                    id = obj.optString("id", ""),
                    type = obj.optString("type", "info"),
                    msg = obj.optString("msg", ""),
                    ts = obj.optLong("ts", System.currentTimeMillis())
                )
            }
        } catch (e: Exception) {
            // ডিস্কে থাকা ডেটা করাপ্টেড হলে খালি তালিকা দিয়ে শুরু করা, ক্র্যাশ না করে
            emptyList()
        }
    }
}
