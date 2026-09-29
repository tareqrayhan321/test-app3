package com.imran.clothstore.data.model

/**
 * ওয়েব অ্যাপের notifPush()/localStorage `app_notifications` এর সমতুল্য একটা নোটিফিকেশন।
 * Firestore কালেকশন: notifications/{notifId} — যাতে একাধিক ডিভাইসের মধ্যে notification history sync থাকে।
 */
data class Notification(
    val id: String = "",
    /** "success" | "error" | "info" — ওয়েব অ্যাপের icons ম্যাপের সাথে মিল রেখে */
    val type: String = "info",
    val msg: String = "",
    val ts: Long = System.currentTimeMillis()
)
