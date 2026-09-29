package com.imran.clothstore.ui.screens.reports

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imran.clothstore.data.model.WeeklyReport
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের aggOverlay (Aggregate Report) এর সরাসরি Kotlin/Compose সংস্করণ।
 * প্রতিটা সপ্তাহের রিপোর্ট এক রো — ইন-স্টক, আউট-স্টক, নেট, গ্রস, ক্যাশ-ইন, ক্যাশ-আউট।
 * লং-প্রেসে ডিলিট মোড চালু হয় (aggOvToggleDelMode এর সমতুল্য)।
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AggregateReportOverlay(
    onClose: () -> Unit,
    viewModel: WeeklyReportsViewModel
) {
    val reports by viewModel.reportsNewestFirst.collectAsState()
    var deleteMode by remember { mutableStateOf(false) }

    val sumInStock = reports.sumOf { it.stockInYard }
    val sumOutStock = reports.sumOf { it.stockOutYard }
    val sumNet = reports.sumOf { it.net }
    val sumGross = reports.sumOf { it.gross }
    val sumIn = reports.sumOf { it.cashIn }
    val sumOut = reports.sumOf { it.cashOut }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F5))
    ) {
        // ── হেডার ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.HeaderTeal)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Aggregate Report", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (reports.isEmpty()) "কোনো ডাটা নেই" else "${reports.size} সপ্তাহের ডাটা",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "বন্ধ করুন", tint = Color.White)
            }
        }

        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("📊 এখনো কোনো সাপ্তাহিক হিসাব যোগ করা হয়নি", fontSize = 13.sp, color = Color(0xFF9A96AD))
            }
            return@Column
        }

        // ── টেবিল (অনুভূমিক স্ক্রলযোগ্য) ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            val colWidths = listOf(90.dp, 70.dp, 70.dp, 76.dp, 76.dp, 70.dp, 70.dp)
            val headers = listOf("সপ্তাহ", "ইন-স্টক", "আউট-স্টক", "নেট", "গ্রস", "ক্যাশ-ইন", "ক্যাশ-আউট")

            Row(modifier = Modifier.background(Color(0xFFF0EEE5))) {
                headers.forEachIndexed { i, h ->
                    Text(
                        text = h,
                        modifier = Modifier.width(colWidths[i]).padding(6.dp),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(reports, key = { it.id }) { report ->
                    AggReportRow(
                        report = report,
                        colWidths = colWidths,
                        deleteMode = deleteMode,
                        onLongPress = { deleteMode = true },
                        onDelete = { viewModel.deleteReport(report.id) }
                    )
                }
            }

            // ── ফুটার টোটাল ──
            Row(modifier = Modifier.background(Color(0xFFF0EEE5))) {
                Text("সর্বমোট", modifier = Modifier.width(colWidths[0]).padding(6.dp), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text(formatGaj(sumInStock), modifier = Modifier.width(colWidths[1]).padding(6.dp), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text(formatGaj(sumOutStock), modifier = Modifier.width(colWidths[2]).padding(6.dp), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text(
                    formatTaka(sumNet), modifier = Modifier.width(colWidths[3]).padding(6.dp), fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold, color = if (sumNet >= 0) Color(0xFF2BB673) else Color(0xFFD9452B)
                )
                Text(formatTaka(sumGross), modifier = Modifier.width(colWidths[4]).padding(6.dp), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text(formatTaka(sumIn), modifier = Modifier.width(colWidths[5]).padding(6.dp), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                Text(formatTaka(sumOut), modifier = Modifier.width(colWidths[6]).padding(6.dp), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (deleteMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF7E8E4))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "ডিলিট মোড চালু — সারি মুছতে 🗑️ আইকনে ট্যাপ করুন",
                    fontSize = 11.sp,
                    color = Color(0xFFD9452B),
                    modifier = Modifier.combinedClickable(onClick = { deleteMode = false })
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AggReportRow(
    report: WeeklyReport,
    colWidths: List<androidx.compose.ui.unit.Dp>,
    deleteMode: Boolean,
    onLongPress: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongPress),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (deleteMode) {
            IconButton(onClick = onDelete, modifier = Modifier.width(colWidths[0])) {
                Icon(Icons.Filled.Delete, contentDescription = "মুছুন", tint = Color(0xFFD9452B))
            }
        } else {
            Text(report.label.ifBlank { "—" }, modifier = Modifier.width(colWidths[0]).padding(6.dp), fontSize = 11.sp)
        }
        Text(formatGaj(report.stockInYard), modifier = Modifier.width(colWidths[1]).padding(6.dp), fontSize = 11.sp)
        Text(formatGaj(report.stockOutYard), modifier = Modifier.width(colWidths[2]).padding(6.dp), fontSize = 11.sp)
        Text(
            formatTaka(report.net), modifier = Modifier.width(colWidths[3]).padding(6.dp), fontSize = 11.sp,
            color = if (report.net >= 0) Color(0xFF2BB673) else Color(0xFFD9452B)
        )
        Text(formatTaka(report.gross), modifier = Modifier.width(colWidths[4]).padding(6.dp), fontSize = 11.sp)
        Text(formatTaka(report.cashIn), modifier = Modifier.width(colWidths[5]).padding(6.dp), fontSize = 11.sp)
        Text(formatTaka(report.cashOut), modifier = Modifier.width(colWidths[6]).padding(6.dp), fontSize = 11.sp)
    }
}
