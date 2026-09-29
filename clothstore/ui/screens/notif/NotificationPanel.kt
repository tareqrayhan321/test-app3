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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
            .background(Color(0xFF241505))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔔 নোটিফিকেশন", color = Color(0xFFE0B84A), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "সব মুছো",
                    color = Color(0xFFA09070),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.clearAll() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
                Text(
                    text = "✕",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onClose)
                        .padding(8.dp)
                )
            }
        }

        if (notifications.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("কোনো নোটিফিকেশন নেই", color = Color(0xFF8A7A60), fontSize = 13.sp)
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

@Composable
private fun NotifRow(notif: Notification) {
    val icon = when (notif.type) {
        "success" -> "✅"
        "error" -> "⚠️"
        else -> "☁️"
    }
    val iconBg = when (notif.type) {
        "success" -> Color(0xFF1E4A2E)
        "error" -> Color(0xFF4A241E)
        else -> Color(0xFF1E3A4A)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF34220F))
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
            Text(icon, fontSize = 15.sp)
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(notif.msg, color = Color(0xFFF0EAD8), fontSize = 12.5.sp)
            Text(formatNotifTime(notif.ts), color = Color(0xFF8A7A60), fontSize = 10.sp, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/** ওয়েব অ্যাপের _notifTimeText() এর সমতুল্য — "এইমাত্র" / "X মিনিট আগে" / "X ঘণ্টা আগে" / তারিখ */
private fun formatNotifTime(ts: Long): String {
    val diff = System.currentTimeMillis() - ts
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    if (minutes < 1) return "এইমাত্র"
    if (minutes < 60) return "$minutes মিনিট আগে"
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    if (hours < 24) return "$hours ঘণ্টা আগে"
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ts))
}
