package com.imran.clothstore.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.data.model.WeeklyReport
import com.imran.clothstore.ui.theme.AppColors

private val ProfitGreen = Color(0xFF16A34A)
private val LossRed = Color(0xFFC0392B)

/**
 * ওয়েব অ্যাপের netBreakdownOverlay (showNetProfitBreakdown) এর সরাসরি Kotlin/Compose সংস্করণ।
 * সবচেয়ে সাম্প্রতিক সপ্তাহের রিপোর্ট নিয়ে: রেজাল্ট ব্যানার (লাভ/লোকসান) → লাইন-বাই-লাইন
 * ব্রেকডাউন (বিক্রয় → COGS → গ্রস → খরচগুলো → নিট) → সংক্ষিপ্ত ব্যাখ্যা নোট।
 */
@Composable
fun NetProfitBreakdownOverlay(
    onClose: () -> Unit,
    viewModel: WeeklyReportsViewModel
) {
    val reports by viewModel.reportsNewestFirst.collectAsState()
    val latest = reports.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.HeaderTeal)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Weekly Net Profit/Loss", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, contentDescription = "বন্ধ করুন", tint = Color.White)
            }
        }

        if (latest == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("📊 এখনো কোনো সাপ্তাহিক হিসাব যোগ করা হয়নি", fontSize = 13.sp, color = Color(0xFF9A96AD))
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = latest.label,
                fontSize = 13.sp,
                color = Color(0xFF6C6A64),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            ResultBanner(latest)

            Column(modifier = Modifier.padding(top = 16.dp)) {
                BreakdownRow("বিক্রয় (Sales)", formatTaka(latest.sales), isBold = false)
                BreakdownRow("পণ্য ব্যয় (COGS)", "−${formatTaka(latest.cogs)}", isBold = false, valueColor = LossRed)
                BreakdownRow("গ্রস প্রফিট", formatTaka(latest.gross), isBold = true, divider = true)
                BreakdownRow("পরিচালন ব্যয় (Opex)", if (latest.opex > 0) "−${formatTaka(latest.opex)}" else "—", isBold = false, valueColor = if (latest.opex > 0) LossRed else Color.Gray)
                BreakdownRow("স্থায়ী খরচ (Fixed)", if (latest.fixed > 0) "−${formatTaka(latest.fixed)}" else "—", isBold = false, valueColor = if (latest.fixed > 0) LossRed else Color.Gray)
                BreakdownRow("যাকাত (Zakat)", if (latest.zakat > 0) "−${formatTaka(latest.zakat)}" else "—", isBold = false, valueColor = if (latest.zakat > 0) LossRed else Color.Gray)
                BreakdownRow(
                    "নিট মুনাফা/লোকসান", formatTaka(latest.net), isBold = true, divider = true,
                    valueColor = if (latest.net < 0) LossRed else ProfitGreen
                )
            }

            SummaryNote(latest)
        }
    }
}

@Composable
private fun ResultBanner(report: WeeklyReport) {
    val isLoss = report.net < 0
    val bg = if (isLoss) Color(0xFFFBEAE7) else Color(0xFFE8F6EC)
    val labelColor = if (isLoss) LossRed else ProfitGreen

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(14.dp))
            .padding(18.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isLoss) "Weekly Net Loss" else "Weekly Net Profit",
                color = labelColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formatTaka(report.net),
                color = labelColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    isBold: Boolean,
    divider: Boolean = false,
    valueColor: Color = Color(0xFF141413)
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                color = valueColor
            )
        }
        if (divider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3E0D8))
                    .padding(top = 1.dp)
            )
        }
    }
}

/** ওয়েব অ্যাপের nbSummaryNote এর সমতুল্য — সবচেয়ে বড় খরচ খাত চিহ্নিত করে সংক্ষিপ্ত ব্যাখ্যা দেয় */
@Composable
private fun SummaryNote(report: WeeklyReport) {
    val isLoss = report.net < 0
    val bg = if (isLoss) Color(0xFFFBF3F0) else Color(0xFFF0F8F1)
    val textColor = if (isLoss) Color(0xFF8A3A2F) else Color(0xFF2F6B3A)

    val biggestExpense = listOf(
        "পণ্য ব্যয় (COGS)" to report.cogs,
        "পরিচালন ব্যয়" to report.opex,
        "স্থায়ী খরচ" to report.fixed,
        "যাকাত" to report.zakat
    ).maxByOrNull { it.second }

    val message = if (isLoss) {
        val expensePart = if (biggestExpense != null && biggestExpense.second > 0) {
            "সবচেয়ে বড় খরচ ছিল ${biggestExpense.first} (${formatTaka(biggestExpense.second)}) — এই খাতে ব্যয় কমানো বা বিক্রয়/গজে-লাভ বাড়ানোর দিকে নজর দিন।"
        } else {
            "বিক্রয়ের তুলনায় মোট খরচ বেশি হয়ে গেছে।"
        }
        "⚠️ এই সপ্তাহে ${formatTaka(kotlin.math.abs(report.net))} টাকা লোকসান হয়েছে। $expensePart"
    } else {
        val totalExpense = report.opex + report.fixed + report.zakat
        "✓ এই সপ্তাহে ${formatTaka(report.net)} টাকা লাভ হয়েছে। গ্রস প্রফিট ${formatTaka(report.gross)} থেকে সব খরচ (${formatTaka(totalExpense)}) বাদ দেওয়ার পরও নিট মুনাফা রয়ে গেছে।"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .background(bg, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(text = message, fontSize = 12.sp, color = textColor, lineHeight = 18.sp)
    }
}
