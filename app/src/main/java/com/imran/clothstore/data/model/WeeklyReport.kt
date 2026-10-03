package com.imran.clothstore.data.model

/**
 * ওয়েব অ্যাপের localStorage `chart_data` array-এর একটা এন্ট্রি — সাপ্তাহিক হিসাবের
 * সম্পূর্ণ ব্রেকডাউন। পাইকারি ক্যালকুলেটর থেকে তৈরি হয় এবং Aggregate Report,
 * Net Profit Breakdown, P&L Trends — এই তিনটা ওভারলে এর উপর নির্ভর করে।
 *
 * এই array Firestore imran_store/backup ডকুমেন্টের chart_data ফিল্ডে থাকে — আলাদা
 * কালেকশনে নয়। id সংখ্যা (Date.now()); merge হয় ts দিয়ে, id দিয়ে নয় (ওয়েব অ্যাপের
 * _mergeArrByTs লজিক)। মোট এন্ট্রি ২৬টার বেশি হলে সবচেয়ে পুরাতনটা বাদ পড়ে।
 */
data class WeeklyReport(
    val id: Long = 0L,
    /** "সেপ্টেম্বর ৩ সপ্তা-৪" জাতীয় লেবেল */
    val label: String = "",
    /** এপক টাইমস্ট্যাম্প (মিলিসেকেন্ড) — merge/সাজানোর মূল চাবি */
    val ts: Long = System.currentTimeMillis(),
    /** বছর, ISO সপ্তাহ সংখ্যা, ও মাস (0-11) — একই সপ্তাহে দ্বিতীয়বার হিসাব করলে replace করার জন্য */
    val year: Int = 0,
    val week: Int = 0,
    val month: Int = 0,

    // ── প্রফিট/লস ব্রেকডাউন ──
    val sales: Double = 0.0,
    val cogs: Double = 0.0,
    val gross: Double = 0.0,
    val opex: Double = 0.0,
    val fixed: Double = 0.0,
    val zakat: Double = 0.0,
    val net: Double = 0.0,

    // ── ক্যাশ ফ্লো ──
    val cashIn: Double = 0.0,
    val cashOut: Double = 0.0,
    /** নগদ ক্রয় (Fund Flow পাই চার্টে) — না দিলে cashOut এর সমান ধরা হয় */
    val cashPurchase: Double = 0.0,
    /** পুরনো দেনা পরিশোধ */
    val oldDebt: Double = 0.0,
    /** নগদ বিক্রয়ের গ্রস প্রফিট (গজ×লাভ পদ্ধতি) — Weekly Cash Profit/Loss KPI-তে ব্যবহৃত */
    val cashProfit: Double = 0.0,

    // ── স্টক (গজ/ইয়ার্ড) ──
    val stockInYard: Double = 0.0,
    val stockOutYard: Double = 0.0,

    // ── Aggregate Report-এর নতুন কলামের জন্য (পুরনো এন্ট্রিতে ০ থাকবে) ──
    /** ক্রয় মূল্য = নগদ ক্রয় + বাকি ক্রয় */
    val purchaseTotal: Double = 0.0,
    /** নগদ বিক্রি */
    val cashSale: Double = 0.0,
    /** বাকি বিক্রি */
    val creditSale: Double = 0.0,
    /** পার্টি আমদানি = পুরনো বাকি আদায় */
    val oldCollection: Double = 0.0
)
