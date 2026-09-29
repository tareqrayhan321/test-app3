package com.imran.clothstore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppLightColorScheme = lightColorScheme(
    primary = AppColors.HeaderTeal,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = AppColors.Gold,
    onSecondary = AppColors.Ink,
    background = AppColors.BodyBg,
    onBackground = AppColors.TextPrimary,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = AppColors.TextPrimary
)

@Composable
fun ImranClothStoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // ওয়েব অ্যাপে ডার্ক-মোড টগল নেই — তাই আপাতত সবসময় লাইট স্কিম ব্যবহার করা হচ্ছে,
    // হুবহু একই লুক বজায় রাখতে।
    MaterialTheme(
        colorScheme = AppLightColorScheme,
        typography = AppTypography,
        content = content
    )
}
