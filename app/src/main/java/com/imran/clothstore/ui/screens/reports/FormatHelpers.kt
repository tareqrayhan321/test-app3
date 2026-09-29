package com.imran.clothstore.ui.screens.reports

import com.imran.clothstore.util.formatCompact as bnFormatCompact
import com.imran.clothstore.util.formatCompactTaka as bnFormatCompactTaka
import com.imran.clothstore.util.formatGaj as bnFormatGaj
import com.imran.clothstore.util.formatTaka as bnFormatTaka

/**
 * পুরনো ইমপ্লিমেন্টেশন এখান থেকে সরিয়ে com.imran.clothstore.util.BengaliNumberFormat.kt-এ
 * একীভূত করা হয়েছে (বাংলা অঙ্ক কনভার্সন + HomeViewModel.kt-এর ডুপ্লিকেট formatTaka() বাদ)।
 * এই ফাইলে শুধু thin wrapper রাখা হলো, যাতে বাকি সব স্ক্রিনের
 * `import com.imran.clothstore.ui.screens.reports.formatTaka` স্টেটমেন্ট অপরিবর্তিত কাজ করে।
 */
fun formatTaka(v: Double): String = bnFormatTaka(v)
fun formatGaj(v: Double): String = bnFormatGaj(v)
fun formatCompact(v: Double): String = bnFormatCompact(v)
fun formatCompactTaka(v: Double): String = bnFormatCompactTaka(v)
