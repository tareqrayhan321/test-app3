package com.imran.clothstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ওয়েব অ্যাপের নিচের "ড্যাশবোর্ড" / "হোম" ট্যাব বারের সমতুল্য।
 * নেটিভ অ্যাপে এই বার সম্পূর্ণ অনুপস্থিত ছিল — এখানে যোগ করা হলো।
 */
enum class BottomNavTab { DASHBOARD, HOME }

@Composable
fun BottomNavBar(
    selected: BottomNavTab,
    onSelect: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    // ভাসমান (floating) পিল-আকৃতির বার — স্ক্রিনের কিনারা থেকে ফাঁক রেখে, গোলাকার কোণসহ।
    // ব্যাকগ্রাউন্ড আধা-স্বচ্ছ (alpha) রাখা হয়েছে যাতে নিচের কার্ড ঝাপসা হলেও দেখা যায়।
    // এই বার কনটেন্টের উপরে overlay হয় (দেখুন MainActivity.kt), তাই স্বচ্ছতা কার্যকর।
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 96.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFFF4F1EA).copy(alpha = 0.96f))
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomNavItem(
            label = "Dashboard",
            selectedIcon = Icons.Filled.GridView,
            unselectedIcon = Icons.Outlined.GridView,
            isSelected = selected == BottomNavTab.DASHBOARD,
            onClick = { onSelect(BottomNavTab.DASHBOARD) }
        )
        BottomNavItem(
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            isSelected = selected == BottomNavTab.HOME,
            onClick = { onSelect(BottomNavTab.HOME) }
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFFD4520F) // ওয়েব রেফারেন্সে সক্রিয় ট্যাবের কমলা রং (আরো গাঢ়)
    val inactiveColor = Color(0xFF4A4A45)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = selectedIcon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
