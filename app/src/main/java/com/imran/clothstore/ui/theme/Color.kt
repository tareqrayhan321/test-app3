package com.imran.clothstore.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * ওয়েব অ্যাপের CSS :root ভেরিয়েবল থেকে হুবহু তোলা কালার প্যালেট।
 * যেন Kotlin UI ঠিক একই রং ব্যবহার করে — কোনো "কাছাকাছি" রং অনুমান করা হয়নি।
 */
object AppColors {
    // ── Dark Wood / Gold থিম (fv স্ক্রিন, ইত্যাদি) ──
    val Ink = Color(0xFF1A0F06)
    val Gold = Color(0xFFC8961E)
    val Gold2 = Color(0xFFE0B84A)
    val Gold3 = Color(0xFFF5D06E)
    val Muted = Color(0xFF8A7A60)
    val Border = Color(0xFFC8A860)

    // ── হেডার / টপবার — Deep Teal/Cyan ──
    val HeaderTeal = Color(0xFF00363A)
    val ThemeColor = Color(0xFF00363A) // manifest.json theme-color এর সাথে মিল রেখে

    // ── বেস ব্যাকগ্রাউন্ড ──
    val BodyBg = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF141413)

    // ── ওয়েব অ্যাপের হোম কার্ড থিম — ক্রিম/বেইজ ব্যাকগ্রাউন্ড + সোনালি বর্ডার ──
    // (রেফারেন্স স্ক্রিনশট অনুযায়ী: home-card, kpi card, calendar strip card সবকটার
    // ব্যাকগ্রাউন্ড এই ক্রিম টোন এবং প্রতিটার একটা পাতলা সোনালি বর্ডার আছে)
    val CardCream = Color(0xFFF3ECD9)
    val CardBorder = Color(0xFFD9C48A)

    // ── উড প্যানেল বেজ গ্রেডিয়েন্টের স্টপ কালার (--wood-base) ──
    val WoodTop = Color(0xFF6B3E18)
    val WoodMid1 = Color(0xFF5A3210)
    val WoodMid2 = Color(0xFF4E2C0E)
    val WoodMid3 = Color(0xFF56300F)
    val WoodBottom = Color(0xFF4A2A0C)
}
