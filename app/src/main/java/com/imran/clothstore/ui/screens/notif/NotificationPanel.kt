package com.imran.clothstore.ui.screens.notif

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.data.model.Notification
import com.imran.clothstore.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * ওয়েব অ্যাপের notifOverlay (notif-panel) এর সরাসরি Kotlin/Compose সংস্করণ।
 * ফুল-স্ক্রিন ওভারলে হিসেবে খোলে — আইকনসহ (success ✅ / error ⚠️ / info ☁️) নোটিফিকেশন লিস্ট।
 */
@Composable
fun NotificationPanel(
    onClose: () -> Unit,
    viewModel: NotificationViewModel
) {
    val notifications by viewModel.notifications.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onPanelOpened()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.HeaderTeal)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF0B4A4E), AppColors.HeaderTeal)
                    )
                )
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("নোটিফিকেশন", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "সব মুছো",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.clearAll() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
        ) {
            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("কোনো নোটিফিকেশন নেই", color = Color(0xFF8A8578), fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications, key = { it.id }) { notif ->
                        NotifRow(notif)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotifRow(notif: Notification) {
    val icon = when (notif.type) {
        "success" -> Icons.Filled.CheckCircle
        "error" -> Icons.Filled.Warning
        else -> Icons.Filled.Cloud
    }
    val iconTint = when (notif.type) {
        "success" -> Color(0xFF1F9D55)
        "error" -> Color(0xFFD9452B)
        else -> Color(0xFF2F6DB5)
    }
    val iconBg = when (notif.type) {
        "success" -> Color(0xFFDDF0E3)
        "error" -> Color(0xFFFBE0DC)
        else -> Color(0xFFDCEBF5)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.CardCream)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(notif.msg, color = Color(0xFF141413), fontSize = 12.5.sp)
            Text(formatNotifTime(notif.ts), color = Color(0xFF6C6A64), fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** ওয়েব অ্যাপের _notifTimeText() এর সমতুল্য — "এইমাত্র" / "X মিনিট আগে" / "X ঘণ্টা আগে" / তারিখ। */
private fun formatNotifTime(ts: Long): String {
    val diff = System.currentTimeMillis() - ts
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    if (minutes < 1) return "এইমাত্র"
    if (minutes < 60) return "$minutes মিনিট আগে"
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    if (hours < 24) return "$hours ঘণ্টা আগে"
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
}
