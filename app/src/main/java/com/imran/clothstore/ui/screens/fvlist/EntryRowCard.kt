package com.imran.clothstore.ui.screens.fvlist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.imran.clothstore.data.model.Entry
import com.imran.clothstore.ui.theme.AppColors
import com.imran.clothstore.util.formatTaka

/**
 * ওয়েব অ্যাপের fv লিস্ট আইটেম (fvc-<id>) এর সমতুল্য কার্ড — রেফারেন্স স্ক্রিনশট অনুযায়ী:
 * বামে ছবি/আদ্যক্ষর (গোলাকার-বর্গ), মাঝে নাম + 📍ঠিকানা + 📞নম্বর, ডানে কল ও WhatsApp বাটন,
 * তার নিচে বাকির পরিমাণ। সাধারণ ট্যাপে ডিটেইল খোলে, লং-প্রেসে এডিট/ডিলিট অ্যাকশন বার দেখায়।
 * (কল = ডায়ালার, WhatsApp = wa.me লিংক — বাটনের আচরণ রেফারেন্সে দেখা যায়নি, যুক্তিসঙ্গত অনুমান)
 */
@Composable
fun EntryRowCard(
    entry: Entry,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val phoneDigits = entry.mob.filter { it.isDigit() || it == '+' }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, Color(0xFFE2D9C4)), RoundedCornerShape(20.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongPress)
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // অ্যাভাটার — ছবি থাকলে দেখায়, নাহলে নামের প্রথম অক্ষর (গোলাকার-বর্গ)
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFD9CCA6)),
                contentAlignment = Alignment.Center
            ) {
                if (entry.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = entry.photoUrl,
                        contentDescription = "${entry.name} এর ছবি",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(52.dp).clip(RoundedCornerShape(14.dp))
                    )
                } else {
                    Text(
                        text = entry.name.trim().take(2).ifEmpty { "?" },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(
                    text = entry.name.ifBlank { "নাম নেই" },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                if (entry.addr.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                        Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Color(0xFF6C6A64), modifier = Modifier.size(18.dp))
                        Text(entry.addr, fontSize = 14.sp, color = AppColors.TextPrimary, modifier = Modifier.padding(start = 6.dp))
                    }
                }
                if (entry.mob.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 6.dp)) {
                        Icon(Icons.Outlined.Call, contentDescription = null, tint = Color(0xFF6C6A64), modifier = Modifier.size(18.dp))
                        // ফোন নম্বর ইংরেজি অঙ্কেই থাকে (ওয়েব অ্যাপের PHONE_RE আচরণ)
                        Text(entry.mob, fontSize = 14.sp, color = AppColors.TextPrimary, modifier = Modifier.padding(start = 6.dp))
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    ContactButton(enabled = phoneDigits.isNotBlank(), onClick = {
                        context.startActivity(
                            android.content.Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:$phoneDigits"))
                        )
                    }) {
                        Icon(Icons.Outlined.Call, contentDescription = "কল করুন", tint = Color(0xFF3D6B4F), modifier = Modifier.size(22.dp))
                    }
                    ContactButton(enabled = phoneDigits.isNotBlank(), onClick = {
                        val wa = phoneDigits.removePrefix("+")
                        context.startActivity(
                            android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse("https://wa.me/$wa"))
                        )
                    }) {
                        Icon(WhatsAppIcon, contentDescription = "WhatsApp", tint = Color(0xFF25D366), modifier = Modifier.size(22.dp))
                    }
                }
                Text(
                    text = formatTaka(entry.baki),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }
        }

        // লং-প্রেস অ্যাকশন বার — নির্বাচিত থাকলে দেখা যায়
        if (isSelected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Filled.Edit, contentDescription = "এডিট", tint = AppColors.HeaderTeal)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "ডিলিট", tint = Color(0xFFD9452B))
                }
            }
        }
    }
}

@Composable
private fun ContactButton(enabled: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFD9E5D2))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}
