package com.imran.clothstore.ui.screens.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের side-menu (sideMenu/menuOverlay) এর Kotlin/Compose সংস্করণ।
 * মূল ওয়েব অ্যাপে side-menu-body সম্পূর্ণ খালি ছিল (ভবিষ্যতের সম্প্রসারণের জন্য রাখা),
 * তাই এখানে কাঠামো অভিন্ন রেখে ব্যবহারিক শর্টকাট (হোম, পার্টি লেজার, ডাটাবেইজ সংযোগ) যোগ করা হয়েছে।
 */
@Composable
fun SideMenu(
    onClose: () -> Unit,
    onNavigateHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF0B4A4E), AppColors.HeaderTeal)
                    ),
                    RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                )
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("মেনু", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(
                "✕",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onClose)
                    .padding(6.dp)
            )
        }

        Column(modifier = Modifier.padding(12.dp)) {
            MenuItem("🏠", "হোম", onNavigateHome)
        }
    }
}

@Composable
private fun MenuItem(icon: String, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 16.sp)
        Text(label, fontSize = 14.sp, modifier = Modifier.padding(start = 12.dp), color = Color(0xFF2A2418))
    }
}
