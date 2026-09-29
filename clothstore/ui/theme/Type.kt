package com.imran.clothstore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * ওয়েব অ্যাপ ব্যবহার করেছে Noto Sans Bengali / Noto Serif Bengali / Playfair Display।
 * এখানে ডিফল্ট system FontFamily রাখা হলো — চাইলে res/font ফোল্ডারে
 * NotoSansBengali-Regular.ttf ইত্যাদি রেখে FontFamily(Font(R.font.xxx)) দিয়ে replace করা যাবে।
 */
val AppFontFamily = FontFamily.Default // TODO: Noto Sans Bengali .ttf যোগ করলে এখানে বসবে

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)
