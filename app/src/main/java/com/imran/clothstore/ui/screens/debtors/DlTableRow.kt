package com.imran.clothstore.ui.screens.debtors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ওয়েব অ্যাপের dlRender() এর টেবিল রো — নাম, বাকি, ঠিকানা, কল বাটন, জমা বাটন
 * ঠিক পাঁচ কলামের কাঠামো (fv-excel টেবিলের সমতুল্য)।
 */
@Composable
fun DlTableRow(row: DebtorRow, nameWidth: Dp, moneyWidth: Dp, addrWidth: Dp, onJomaClick: () -> Unit) {
    val entry = row.entry
    val baki = entry.baki
    val isZero = baki == 0.0

    Row(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = entry.name.ifBlank { "—" },
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(nameWidth)
        )
        Text(
            text = "৳${baki.toLong()}",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isZero) Color(0xFF2BB673) else Color(0xFFD9452B),
            modifier = Modifier.width(moneyWidth)
        )
        Text(
            text = entry.addr.ifBlank { "—" },
            fontSize = 10.5.sp,
            color = Color(0xFF5A4A30),
            modifier = Modifier.width(addrWidth)
        )

        // কল বাটন
        val context = LocalContext.current
        val hasMob = entry.mob.isNotBlank()
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (hasMob) Color(0xFF2E7D32) else Color(0xFF3A3020))
                .clickable(enabled = hasMob) {
                    val intent = android.content.Intent(
                        android.content.Intent.ACTION_DIAL,
                        android.net.Uri.parse("tel:${entry.mob}")
                    )
                    context.startActivity(intent)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Call,
                contentDescription = "কল করুন",
                tint = if (hasMob) Color.White else Color(0xFF6A5A40),
                modifier = Modifier.size(14.dp)
            )
        }

        // জমা বাটন
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A5C35))
                .clickable(onClick = onJomaClick)
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(11.dp))
            Text("জমা", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}
