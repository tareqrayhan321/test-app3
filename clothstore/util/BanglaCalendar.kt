package com.imran.clothstore.util

import java.util.Calendar
import java.util.TimeZone

/**
 * ওয়েব অ্যাপের getBanglaDate() (index.html লাইন ৩৫০৯-৩৫৫২) এর হুবহু Kotlin পোর্ট —
 * একই তারিখে একই বাংলা সন/মাস/দিন দেবে। Asia/Dhaka টাইমজোন অনুযায়ী "আজকের" গ্রেগরিয়ান
 * তারিখ ব্যবহার করা হয় (ওয়েব অ্যাপ Intl.DateTimeFormat({timeZone:'Asia/Dhaka'}) দিয়ে যা করে)।
 */

val BN_MONTHS = listOf(
    "বৈশাখ", "জ্যৈষ্ঠ", "আষাঢ়", "শ্রাবণ", "ভাদ্র", "আশ্বিন",
    "কার্তিক", "অগ্রহায়ণ", "পৌষ", "মাঘ", "ফাল্গুন", "চৈত্র"
)

data class BanglaDate(val day: Int, val monthIdx: Int, val year: Int)

/** [গ্রেগ মাস (1-12), বাংলা মাস শুরুর গ্রেগ দিন, বাংলা মাস idx, বাংলা বছরে +১?] — ওয়েব অ্যাপের `tr` টেবিল হুবহু */
private val TR = listOf(
    intArrayOf(4, 14, 0, 1), intArrayOf(5, 15, 1, 1), intArrayOf(6, 15, 2, 1), intArrayOf(7, 16, 3, 1),
    intArrayOf(8, 16, 4, 1), intArrayOf(9, 16, 5, 1), intArrayOf(10, 17, 6, 1), intArrayOf(11, 16, 7, 1),
    intArrayOf(12, 15, 8, 1), intArrayOf(1, 14, 9, 0), intArrayOf(2, 13, 10, 0), intArrayOf(3, 14, 11, 0)
)

private fun daysInGregorianMonth(year: Int, month1to12: Int): Int {
    // java.util.Calendar: getActualMaximum(DAY_OF_MONTH) — ওয়েব অ্যাপের `new Date(y, m, 0).getDate()` এর সমতুল্য
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(year, month1to12 - 1, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun getBanglaDate(gY: Int, gM: Int, gD: Int): BanglaDate {
    var bnMonIdx = 8
    var bnYearAdd = 0
    var bnDay = gD
    var matched = false

    for (i in TR.indices) {
        val (trGM, trStartDay, trMonIdx, trYearAdd) = TR[i]
        if (gM == trGM) {
            if (gD >= trStartDay) {
                bnMonIdx = trMonIdx
                bnYearAdd = trYearAdd
                bnDay = gD - trStartDay + 1
            } else {
                // মাসের শুরু হয়নি — আগের বাংলা মাস
                val prev = (i - 1 + 12) % 12
                bnMonIdx = TR[prev][2]
                bnYearAdd = TR[prev][3]
                val prevStart = TR[prev][1]
                val prevGM = TR[prev][0]
                val prevGY = if (prevGM > gM) gY - 1 else gY
                val daysInPrevGM = daysInGregorianMonth(prevGY, prevGM)
                bnDay = (daysInPrevGM - prevStart + 1) + gD
            }
            matched = true
            break
        }
    }
    if (!matched) {
        // TR-এর প্রতিটা gM 1..12 কভার করে বলে এখানে পৌঁছানো উচিত না; নিরাপত্তা ফলব্যাক
        bnMonIdx = 8; bnYearAdd = 0; bnDay = gD
    }

    var bnYear = gY - 593 + bnYearAdd
    if (bnYearAdd == 0 && gM in 1..3) bnYear = gY - 594

    return BanglaDate(day = bnDay, monthIdx = bnMonIdx, year = bnYear)
}

/** সুবিধাজনক ওভারলোড — আজকের বাংলাদেশ-সময় (Asia/Dhaka) তারিখ থেকে */
fun getBanglaDateToday(): BanglaDate {
    val tz = TimeZone.getTimeZone("Asia/Dhaka")
    val cal = Calendar.getInstance(tz)
    return getBanglaDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
}

private operator fun IntArray.component1() = this[0]
private operator fun IntArray.component2() = this[1]
private operator fun IntArray.component3() = this[2]
private operator fun IntArray.component4() = this[3]
