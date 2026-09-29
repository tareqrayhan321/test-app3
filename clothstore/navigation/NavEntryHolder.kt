package com.imran.clothstore.navigation

import com.imran.clothstore.data.model.Entry

/**
 * Navigation Compose রুট আর্গুমেন্ট শুধু primitive টাইপ (String/Int) সাপোর্ট করে,
 * তাই ক্লিক করা Entry অবজেক্ট সরাসরি রুটে পাঠানো যায় না। এই সিঙ্গেলটন হোল্ডার
 * সেই এন্ট্রিটা সাময়িকভাবে ধরে রাখে যাতে DetailScreen-এ initial value হিসেবে ব্যবহার করা যায়
 * (Firestore থেকে রিয়েল-টাইম আপডেট শুরু হওয়ার আগে flicker এড়াতে)।
 */
object NavEntryHolder {
    private var current: Entry? = null

    fun set(entry: Entry) {
        current = entry
    }

    /** একবার পড়ে সাথে সাথে খালি করে দেয় — স্টেল ডেটা ধরে রাখা এড়াতে */
    fun consume(): Entry? {
        val e = current
        current = null
        return e
    }
}
