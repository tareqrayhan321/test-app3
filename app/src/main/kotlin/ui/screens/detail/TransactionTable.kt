package com.imran.clothstore.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.util.formatTaka
import com.imran.clothstore.util.toBengaliDigits

/**
 * ওয়েব অ্যাপের .fv-excel টেবিলের সমতুল্য — রেফারেন্স স্ক্রিনশট অনুযায়ী: গাঢ় সবুজ হেডার সারি (সাদা
 * বোল্ড টেক্সট), পাওনা-সারি গোলাপি-হালকা, জমা-সারি ধূসর-সবুজ, টাকা "৳১,১৪,৮৫৩" ফরম্যাটে।
 * কলাম: তারিখ | ম্যামো | পাওনা/প্রাপ্য | জমা | মোট বাকি | গজ | মন্তব্য — অনুভূমিক স্ক্রলযোগ্য।
 */
@Composable
fun TransactionTable(rows: List<TxnRow>) {
    val colWidths = listOf(104.dp, 60.dp, 124.dp, 104.dp, 122.dp, 76.dp, 110.dp)
    val headers = listOf("তারিখ", "ম্যামো", "পাওনা/প্রাপ্য", "জমা", "মোট বাকি", "গজ", "মন্তব্য")
    val headerBg = Color(0xFF1E3A32)
    val pawnaBg = Color(0xFFF2E6E4)
    val jomaBg = Color(0xFFECEEEA)
    val grid = Color(0xFFCFCBC0)

    Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Row(modifier = Modifier.background(headerBg)) {
            headers.forEachIndexed { i, h ->
                Text(
                    text = h,
                    modifier = Modifier
                        .width(colWidths[i])
                        .border(0.5.dp, Color(0xFF3B5A50))
                        .padding(vertical = 14.dp, horizontal = 6.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (rows.isEmpty()) {
            Text(
                text = "কোনো লেনদেন নেই",
                modifier = Modifier.padding(16.dp),
                fontSize = 13.sp,
                color = Color(0xFF9A96AD)
            )
        }

        rows.forEach { row ->
            val bg = if (row.type == TxnType.BILL_OR_PAWNA) pawnaBg else jomaBg
            Row(modifier = Modifier.background(bg)) {
                Cell(formatDateShort(row.date), colWidths[0], grid, TextAlign.Center)
                Cell(row.memo.ifBlank { "—" }.toBengaliDigits(), colWidths[1], grid, TextAlign.Center, bold = row.memo.isNotBlank())
                Cell(
                    if (row.type == TxnType.BILL_OR_PAWNA) formatTaka(row.amount) else "—",
                    colWidths[2], grid, TextAlign.Center
                )
                Cell(
                    when {
                        row.mergedJomaAmount != null -> formatTaka(row.mergedJomaAmount)
                        row.type == TxnType.JOMA -> formatTaka(row.amount)
                        else -> "—"
                    },
                    colWidths[3], grid, TextAlign.Center, bold = true
                )
                Cell(formatTaka(row.runningBaki), colWidths[4], grid, TextAlign.Center, bold = true)
                Cell(row.goj.ifBlank { "—" }.toBengaliDigits(), colWidths[5], grid, TextAlign.Center)
                Cell(row.note.ifBlank { "" }, colWidths[6], grid, TextAlign.Center)
            }
        }
    }
}

@Composable
private fun Cell(text: String, width: androidx.compose.ui.unit.Dp, grid: Color, align: TextAlign, bold: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier
            .width(width)
            .border(0.5.dp, grid)
            .padding(vertical = 18.dp, horizontal = 6.dp),
        fontSize = 15.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        color = Color(0xFF141413),
        textAlign = align
    )
}

/** "2026-09-23" জাতীয় ISO তারিখকে "২৩/০৯/২০২৬" (দিন/মাস দুই অঙ্কে, বাংলা অঙ্কে) ফরম্যাটে দেখায় —
 *  রেফারেন্স স্ক্রিনশটের টেবিল অনুযায়ী। পার্স ব্যর্থ হলে মূল স্ট্রিং বাংলা অঙ্কে ফেরত দেয়। */
private fun formatDateShort(date: String): String {
    if (date.isBlank()) return "—"
    return try {
        val parsed = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(date.take(10))
            ?: return date.toBengaliDigits()
        java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.US).format(parsed).toBengaliDigits()
    } catch (e: Exception) {
        date.toBengaliDigits()
    }
}
