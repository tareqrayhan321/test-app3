package com.imran.clothstore.ui.screens.paikkari

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.screens.reports.formatTaka
import com.imran.clothstore.ui.theme.AppColors

private val ProfitGreen = Color(0xFF16A34A)
private val LossRed = Color(0xFFC0392B)

/**
 * ওয়েব অ্যাপের page-result এর সমতুল্য — নিট প্রফিট ব্যানার, মার্জিন স্ট্যাট, ও লাইন-বাই-লাইন ব্রেকডাউন।
 */
@Composable
fun PaikkariResultPage(
    result: PaikkariResult,
    weekLabel: String,
    onNewCalculation: () -> Unit,
    onDone: () -> Unit
) {
    val isLoss = result.netProfit < 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(weekLabel, fontSize = 13.sp, color = Color(0xFF6C6A64))

        // ── ব্যানার ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(if (isLoss) Color(0xFFFBEAE7) else Color(0xFFE8F6EC), RoundedCornerShape(14.dp))
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLoss) "Weekly Net Loss" else "Weekly Net Profit",
                color = if (isLoss) LossRed else ProfitGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = formatTaka(result.netProfit),
                color = if (isLoss) LossRed else ProfitGreen,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ── মার্জিন স্ট্যাট (তিনটা কলাম) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatBox("গ্রস মার্জিন", "%.1f%%".format(result.grossMarginPct), Modifier.weight(1f))
            StatBox("নিট মার্জিন", "%.1f%%".format(result.netMarginPct), Modifier.weight(1f))
            StatBox("নগদ অনুপাত", "%.1f%%".format(result.cashRatioPct), Modifier.weight(1f))
        }

        // ── লাইন-বাই-লাইন ব্রেকডাউন ──
        Column(modifier = Modifier.padding(top = 18.dp)) {
            ResultRow("মোট বিক্রি", formatTaka(result.totalSale))
            ResultRow("পণ্য ব্যয় (COGS)", "−${formatTaka(result.cogs)}", LossRed)
            ResultRow("গ্রস প্রফিট", formatTaka(result.gross), isBold = true, divider = true)
            ResultRow("পরিচালন ব্যয় (Opex)", if (result.opex > 0) "−${formatTaka(result.opex)}" else "—", if (result.opex > 0) LossRed else Color.Gray)
            ResultRow("স্থায়ী খরচ (Fixed)", if (result.fixedExp > 0) "−${formatTaka(result.fixedExp)}" else "—", if (result.fixedExp > 0) LossRed else Color.Gray)
            ResultRow("যাকাত (Zakat)", if (result.zakat > 0) "−${formatTaka(result.zakat)}" else "—", if (result.zakat > 0) LossRed else Color.Gray)
            ResultRow("নিট মুনাফা/লোকসান", formatTaka(result.netProfit), isBold = true, divider = true, color = if (isLoss) LossRed else ProfitGreen)

            SectionTitle("ক্যাশফ্লো", topPadding = 16.dp)
            ResultRow("ক্যাশ ইন", formatTaka(result.cashIn), ProfitGreen)
            ResultRow("ক্যাশ আউট", "−${formatTaka(result.cashOut)}", LossRed)
            ResultRow(
                "নিট ক্যাশফ্লো", formatTaka(result.netCashflow), isBold = true, divider = true,
                color = if (result.netCashflow < 0) LossRed else ProfitGreen
            )

            SectionTitle("বিক্রয় বিশ্লেষণ", topPadding = 16.dp)
            ResultRow("নগদ বিক্রয়", "%.1f%%".format(result.cashPct))
            ResultRow("বাকি বিক্রয়", "%.1f%%".format(result.creditPct))
            ResultRow("গড় লাভ/গজ", formatTaka(result.avgPerGaj))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(onClick = onNewCalculation, modifier = Modifier.weight(1f)) {
                Text("নতুন হিসাব")
            }
            Button(
                onClick = onDone,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
            ) {
                Text("সম্পন্ন")
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFFF7F5EE), RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 10.sp, color = Color(0xFF6C6A64))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    color: Color = Color(0xFF141413),
    isBold: Boolean = false,
    divider: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.5.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
            Text(value, fontSize = 12.5.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold, color = color)
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
