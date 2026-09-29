package com.imran.clothstore.ui.screens.paikkari

import com.imran.clothstore.util.toBengaliDigits
import java.util.Calendar
import java.util.Locale

private val BN_MONTHS = listOf(
    "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
    "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর"
)

/** ISO-8601 সপ্তাহ সংখ্যা — getWeekNumber() এর সমতুল্য */
fun isoWeekNumber(calendar: Calendar): Int {
    val cal = calendar.clone() as Calendar
    cal.firstDayOfWeek = Calendar.MONDAY
    cal.minimalDaysInFirstWeek = 4
    return cal.get(Calendar.WEEK_OF_YEAR)
}

/** getWeekLabel() এর সমতুল্য — "সেপ্টেম্বর ২০২৬ সপ্তা-৩৯" জাতীয় লেবেল তৈরি করে */
fun currentWeekLabel(): String {
    val cal = Calendar.getInstance(Locale.getDefault())
    val month = BN_MONTHS[cal.get(Calendar.MONTH)]
    val year = cal.get(Calendar.YEAR).toBengaliDigits()
    val week = isoWeekNumber(cal).toBengaliDigits()
    return "$month $year সপ্তা-$week"
}

/** বর্তমান বছর ও ISO সপ্তাহ সংখ্যা — একই সপ্তাহের রিপোর্ট থাকলে overwrite করার জন্য */
fun currentYearAndWeek(): Pair<Int, Int> {
    val cal = Calendar.getInstance(Locale.getDefault())
    return cal.get(Calendar.YEAR) to isoWeekNumber(cal)
}
