package com.imran.clothstore.util

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.util.Calendar
import java.util.TimeZone

/**
 * ওয়েব অ্যাপের getHijriDate() (index.html লাইন ৩৬১৪+) এর Android সমতুল্য।
 *
 * গুরুত্বপূর্ণ পার্থক্য: ওয়েব অ্যাপ ব্রাউজারের Intl.DateTimeFormat('en-u-ca-islamic-umalqura')
 * ব্যবহার করে — যেটা প্রকৃত জ্যোতির্বৈজ্ঞানিক/উম্মুল-কুরা পঞ্জিকা ডেটা ব্যবহার করে (ICU লাইব্রেরি
 * থেকে)। java.time.chrono.HijrahChronology (JDK-এ বিল্ট-ইন, desugar করে minSdk 24-এও পাওয়া যায়)
 * শুধু সরল সারণী-ভিত্তিক (tabular) হিজরি গণনা করে — এটাই ওয়েব অ্যাপের 'islamic' ফলব্যাক
 * ক্যালেন্ডারের কাছাকাছি, কিন্তু umalqura-র সাথে হুবহু মিলবে না (১ দিন এদিক-ওদিক হতে পারে,
 * বিশেষত মাসের শুরুতে)। সঠিক umalqura ডেটার জন্য ভবিষ্যতে একটা লাইব্রেরি (যেমন
 * com.github.msarhan:ummalqura-calendar) যোগ করা যেতে পারে — এখন বিল্ট-ইন সমাধান রাখা হলো।
 */

val AR_MONTHS = listOf(
    "مُحَرَّم", "صَفَر", "رَبِيعُ ٱلْأَوَّل", "رَبِيعُ ٱلثَّانِي",
    "جُمَادَىٰ ٱلْأُولَىٰ", "جُمَادَىٰ ٱلْآخِرَة", "رَجَب", "شَعْبَان",
    "رَمَضَان", "شَوَّال", "ذُو ٱلْقَعْدَة", "ذُو ٱلْحِجَّة"
)

val AR_MONTHS_SHORT = listOf(
    "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
    "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
    "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
)

data class HijriDate(val day: Int, val monthIdx: Int, val year: Int)

private val AR_DIGITS = mapOf(
    '0' to '٠', '1' to '١', '2' to '٢', '3' to '٣', '4' to '٤',
    '5' to '٥', '6' to '٦', '7' to '٧', '8' to '٨', '9' to '٩'
)

fun Int.toArabicDigits(): String = toString().map { AR_DIGITS[it] ?: it }.joinToString("")

/** আজকের বাংলাদেশ-সময় (Asia/Dhaka) অনুযায়ী হিজরি তারিখ — সমস্যা হলে null (দেখুন ফাইলের কমেন্ট) */
fun getHijriDateToday(): HijriDate? {
    return try {
        val tz = TimeZone.getTimeZone("Asia/Dhaka")
        val cal = Calendar.getInstance(tz)
        val gregorian = LocalDate.of(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
        val hijrah = HijrahDate.from(gregorian)
        val year = hijrah.get(ChronoField.YEAR_OF_ERA)
        val month = hijrah.get(ChronoField.MONTH_OF_YEAR) // 1-12
        val day = hijrah.get(ChronoField.DAY_OF_MONTH)
        HijriDate(day = day, monthIdx = month - 1, year = year)
    } catch (e: Exception) {
        null
    }
}
