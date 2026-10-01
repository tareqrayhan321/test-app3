package com.imran.clothstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
    // ভাসমান পিল বার — পার্টি ডিটেইলের জোড়া "জমা | পাওনা" বাটনের মতো: আধা-স্বচ্ছ সাদা ব্যাকগ্রাউন্ড,
    // হালকা বর্ডার, হালকা শ্যাডো, মাঝে হালকা ডিভাইডার। কনটেন্টের উপরে overlay হয় (MainActivity.kt)।
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 96.dp, vertical = 12.dp)
            .fillMaxWidth()
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.82f))
            .border(1.dp, Color(0xFFD9D2C0), CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Dashboard",
            selectedIcon = Icons.Filled.GridView,
            unselectedIcon = Icons.Outlined.GridView,
            isSelected = selected == BottomNavTab.DASHBOARD,
            onClick = { onSelect(BottomNavTab.DASHBOARD) },
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(30.dp)
                .background(Color(0xFFD9D2C0))
        )
        BottomNavItem(
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            isSelected = selected == BottomNavTab.HOME,
            onClick = { onSelect(BottomNavTab.HOME) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = Color(0xFFD4520F) // ওয়েব রেফারেন্সে সক্রিয় ট্যাবের কমলা রং (আরো গাঢ়)
    val inactiveColor = Color(0xFF4A4A45)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
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
