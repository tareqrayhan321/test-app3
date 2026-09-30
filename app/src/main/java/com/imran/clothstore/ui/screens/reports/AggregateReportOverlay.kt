package com.imran.clothstore.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextAlign
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

    // পার্টি লেজারের কাঠামো: সাদা সমান হেডার (স্ট্যাটাস বারের নিচে) → সমান বডি → এক্সেল গ্রিড টেবিল
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── হেডার — সাদা, সমান (গোলাকার নয়) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Aggregate Report", color = Color(0xFF1A1A1A), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = if (reports.isEmpty()) "কোনো ডাটা নেই" else "${reports.size} সপ্তাহের ডাটা",
                    color = Color(0xFF6C6A64),
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "বন্ধ করুন", tint = Color(0xFF1A1A1A))
            }
        }

        // ── বডি (সমান, গোলাকার নয়) ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
        ) {
        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("এখনো কোনো সাপ্তাহিক হিসাব যোগ করা হয়নি", fontSize = 13.sp, color = Color(0xFF9A96AD))
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
            val colWidths = listOf(96.dp, 84.dp, 88.dp, 88.dp, 88.dp, 88.dp, 92.dp)
            val headers = listOf("সপ্তাহ", "ইন-স্টক", "আউট-স্টক", "নেট", "গ্রস", "ক্যাশ-ইন", "ক্যাশ-আউট")

            // মেইন হেডার রো — সবুজ, সাদা বোল্ড লেখা (পার্টি লেজার/ডিটেইলের মতো)
            Row(modifier = Modifier.background(AggHeaderBg).height(34.dp)) {
                headers.forEachIndexed { i, h ->
                    Box(
                        modifier = Modifier.width(colWidths[i]).fillMaxHeight().border(0.5.dp, AggHeaderGrid),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = h,
                            modifier = Modifier.padding(horizontal = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            // চওড়া = কলামগুলোর যোগফল (অনুভূমিক স্ক্রলের ভেতরে অসীম প্রস্থ এড়াতে); সারি বেশি হলে ফুটার যেন নিচে থাকে
            LazyColumn(modifier = Modifier.width(colWidths.fold(0.dp) { acc, w -> acc + w }).weight(1f, fill = false)) {
                // ট্যাপে অ্যাপ বন্ধ হওয়ার কারণ: একাধিক রিপোর্টের id একই (বা ০) হলে LazyColumn key ক্র্যাশ করত
                itemsIndexed(reports, key = { index, it -> "${it.id}_${it.ts}_$index" }) { index, report ->
                    AggReportRow(
                        report = report,
                        rowIndex = index,
                        colWidths = colWidths,
                        deleteMode = deleteMode,
                        onLongPress = { deleteMode = true },
                        onDelete = { viewModel.deleteReport(report.id) }
                    )
                }
            }

            // ── ফুটার টোটাল — গ্রিডের শেষ রো ──
            Row(modifier = Modifier.background(Color(0xFFE9E4D4)).height(IntrinsicSize.Min)) {
                AggCell("সর্বমোট", colWidths[0], bold = true, align = TextAlign.Start)
                AggCell(formatGaj(sumInStock), colWidths[1], bold = true)
                AggCell(formatGaj(sumOutStock), colWidths[2], bold = true)
                AggCell(formatTaka(sumNet), colWidths[3], bold = true, color = if (sumNet >= 0) AggGreen else AggRed)
                AggCell(formatTaka(sumGross), colWidths[4], bold = true)
                AggCell(formatTaka(sumIn), colWidths[5], bold = true)
                AggCell(formatTaka(sumOut), colWidths[6], bold = true)
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
                    "ডিলিট মোড চালু — সারি মুছতে ডিলিট আইকনে ট্যাপ করুন",
                    fontSize = 11.sp,
                    color = Color(0xFFD9452B),
                    modifier = Modifier.combinedClickable(onClick = { deleteMode = false })
                )
            }
        }
        }
    }
}

private val AggHeaderBg = Color(0xFF1E3A32)
private val AggHeaderGrid = Color(0xFF3B5A50)
private val AggGrid = Color(0xFFCFCBC0)
private val AggGreen = Color(0xFF2BB673)
private val AggRed = Color(0xFFD9452B)

/** এক্সেল গ্রিডের একটা ঘর — বর্ডারসহ, রো-র সমান উচ্চতা */
@Composable
private fun AggCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    bold: Boolean = false,
    color: Color = Color(0xFF141413),
    align: TextAlign = TextAlign.Center
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .border(0.5.dp, AggGrid)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = if (align == TextAlign.Start) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = color,
            textAlign = align
        )
    }
}

@Composable
private fun AggReportRow(
    report: WeeklyReport,
    rowIndex: Int,
    colWidths: List<androidx.compose.ui.unit.Dp>,
    deleteMode: Boolean,
    onLongPress: () -> Unit,
    onDelete: () -> Unit
) {
    val rowBg = if (rowIndex % 2 == 0) Color.White else Color(0xFFF7F5EF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .height(IntrinsicSize.Min)
            .combinedClickable(onClick = {}, onLongClick = onLongPress),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (deleteMode) {
            Box(
                modifier = Modifier.width(colWidths[0]).fillMaxHeight().border(0.5.dp, AggGrid),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "মুছুন", tint = AggRed)
                }
            }
        } else {
            AggCell(report.label.ifBlank { "—" }, colWidths[0], align = TextAlign.Start)
        }
        AggCell(formatGaj(report.stockInYard), colWidths[1])
        AggCell(formatGaj(report.stockOutYard), colWidths[2])
        AggCell(formatTaka(report.net), colWidths[3], color = if (report.net >= 0) AggGreen else AggRed)
        AggCell(formatTaka(report.gross), colWidths[4])
        AggCell(formatTaka(report.cashIn), colWidths[5])
        AggCell(formatTaka(report.cashOut), colWidths[6])
    }
}
