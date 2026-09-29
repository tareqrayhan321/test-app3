package com.imran.clothstore.ui.screens.fabric

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.data.model.FabricGroup
import com.imran.clothstore.data.model.FabricPurchase
import com.imran.clothstore.ui.screens.paikkari.toInputDouble
import com.imran.clothstore.ui.screens.reports.formatTaka
import com.imran.clothstore.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ওয়েব অ্যাপের .fab-group-card এর সমতুল্য — একটা কাপড়ের নাম, একাধিক purchase রো,
 * একাধিক purchase থাকলে "গড় দর" ব্যাজ, এবং "+ আরো কিনেছি" DCA ইনলাইন ফর্ম।
 */
@Composable
fun FabricGroupCard(
    group: FabricGroup,
    onAddMore: (qty: Double, rate: Double, onError: (String) -> Unit) -> Unit,
    onDeletePurchase: (FabricPurchase) -> Unit
) {
    var showDcaForm by remember { mutableStateOf(false) }
    var dcaQty by remember { mutableStateOf("") }
    var dcaRate by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
    ) {
        // ── হেড ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(group.name, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = group.unit,
                    fontSize = 9.sp,
                    color = Color(0xFF8E8B82),
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF0EEE5))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (group.purchases.size > 1) {
                    Text(
                        text = "গড় দর ৳%.1f".format(group.avgRate),
                        fontSize = 9.5.sp,
                        color = Color(0xFF6C5A2A),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF7EFD8))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = "+ আরো কিনেছি",
                    fontSize = 10.5.sp,
                    color = AppColors.HeaderTeal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showDcaForm = !showDcaForm }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        // ── DCA ইনলাইন ফর্ম ──
        if (showDcaForm) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDF6F0))
                    .padding(12.dp)
            ) {
                errorMsg?.let {
                    Text(it, color = Color(0xFFC0392B), fontSize = 11.sp, modifier = Modifier.padding(bottom = 6.dp))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dcaQty,
                        onValueChange = { dcaQty = it },
                        label = { Text("পরিমাণ (${group.unit})") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = dcaRate,
                        onValueChange = { dcaRate = it },
                        label = { Text("দর/${group.unit}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = { showDcaForm = false }, modifier = Modifier.weight(1f)) {
                        Text("✕", fontSize = 12.sp)
                    }
                    Button(
                        onClick = {
                            onAddMore(dcaQty.toInputDouble(), dcaRate.toInputDouble()) { err -> errorMsg = err }
                            if (dcaQty.toInputDouble() > 0 && dcaRate.toInputDouble() > 0) {
                                dcaQty = ""; dcaRate = ""; showDcaForm = false; errorMsg = null
                            }
                        },
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5DB8A6))
                    ) {
                        Text("✓ যোগ করুন", fontSize = 12.sp)
                    }
                }
            }
        }

        // ── purchase রো তালিকা ──
        Column {
            group.purchases.forEachIndexed { idx, p ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (idx % 2 == 1) Color(0xFFFAFAF8) else Color.Transparent)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatFabricDate(p.date),
                        fontSize = 10.sp,
                        color = Color(0xFF8E8B82),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "${p.qty} ${group.unit} × ৳${p.rate}",
                        fontSize = 12.sp,
                        color = Color(0xFF3D3D3A),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatTaka(p.total),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "✕",
                        fontSize = 12.sp,
                        color = Color(0xFFB8B4A8),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .clickable { onDeletePurchase(p) }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}

private val BN_SHORT_MONTHS = listOf(
    "জানু", "ফেব্রু", "মার্চ", "এপ্রিল", "মে", "জুন",
    "জুলাই", "আগস্ট", "সেপ্ট", "অক্টো", "নভে", "ডিসে"
)

/** fabricFmtDate() এর সমতুল্য — "২৩ সেপ্ট ২০২৬" জাতীয় সংক্ষিপ্ত তারিখ */
private fun formatFabricDate(iso: String): String {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val d = sdf.parse(iso) ?: Date()
        val cal = java.util.Calendar.getInstance().apply { time = d }
        val dd = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val mon = BN_SHORT_MONTHS[cal.get(java.util.Calendar.MONTH)]
        val yy = cal.get(java.util.Calendar.YEAR)
        "$dd $mon $yy"
    } catch (e: Exception) {
        "—"
    }
}
