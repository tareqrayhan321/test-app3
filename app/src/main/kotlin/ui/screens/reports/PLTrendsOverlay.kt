package com.imran.clothstore.ui.screens.reports

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import com.imran.clothstore.util.toBengaliDigits
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.data.model.WeeklyReport
import com.imran.clothstore.ui.theme.AppColors
import kotlin.math.abs
import kotlin.math.max

/**
 * ওয়েব অ্যাপের plTrendsOverlay (plRenderRevenueBarChart) — রেফারেন্স স্ক্রিনশট অনুযায়ী:
 * ক্রিম হেডারে "Profit & Loss Trends", নিচে ক্রিম কার্ডের ভেতরে চার্ট। সপ্তাহের লেবেল উপরে
 * উল্লম্বভাবে (৯০° ঘোরানো), বার গ্র্যাডিয়েন্ট + গাঢ় আউটলাইন, মান-লেবেল "১৬.৭হা" ধাঁচে।
 * (হেডারে ব্যাক আইকন নেই — সিস্টেম ব্যাক ব্যবহার করুন, আপনার আগের নির্দেশ অনুযায়ী)
 */
@Composable
fun PLTrendsOverlay(
    onClose: () -> Unit,
    viewModel: WeeklyReportsViewModel
) {
    val reports by viewModel.reportsOldestFirst.collectAsState()
    androidx.activity.compose.BackHandler(onBack = onClose)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFAF9F5))
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 22.dp)
        ) {
            Text("Profit & Loss Trends", color = AppColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE6DFD8)))

        if (reports.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("📈 এখনো কোনো সাপ্তাহিক হিসাব যোগ করা হয়নি", fontSize = 13.sp, color = Color(0xFF9A96AD))
            }
            return@Column
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEFE8DC))
                .border(BorderStroke(1.dp, Color(0xFFE2D9C4)), RoundedCornerShape(24.dp))
                .padding(vertical = 14.dp, horizontal = 12.dp)
                .horizontalScroll(rememberScrollState())
        ) {
            RevenueBarChart(reports)
        }
    }
}

@Composable
private fun RevenueBarChart(reports: List<WeeklyReport>) {
    val barWidthDp = 38.dp
    val gapDp = 18.dp
    val chartHeightDp = 420.dp
    val leftPad = 6.dp
    val chartWidthDp = leftPad + barWidthDp * reports.size + gapDp * (reports.size - 1).coerceAtLeast(0) + 10.dp

    val absMax = max(1.0, reports.maxOf { abs(it.net) })
    val posTop = Color(0xFF5DB5A4); val posBottom = Color(0xFF3F8C7D)
    val negTop = Color(0xFFC97659); val negBottom = Color(0xFFA9583E)
    val outline = Color(0xFF3A3A3A)

    Canvas(
        modifier = Modifier
            .width(chartWidthDp)
            .height(chartHeightDp)
    ) {
        val barWidthPx = barWidthDp.toPx()
        val gapPx = gapDp.toPx()
        val startX = leftPad.toPx()
        val labelBand = size.height * 0.30f          // উপরে উল্লম্ব লেবেলের জায়গা
        val zeroY = labelBand + (size.height - labelBand) * 0.36f
        val upHalf = zeroY - labelBand - 34.dp.toPx()
        val downHalf = size.height - zeroY - 34.dp.toPx()

        // শূন্য-রেখা
        drawLine(
            color = outline,
            start = Offset(startX, zeroY),
            end = Offset(size.width, zeroY),
            strokeWidth = 2.dp.toPx()
        )

        reports.forEachIndexed { index, report ->
            val x = startX + index * (barWidthPx + gapPx)
            val net = report.net
            val isUp = net >= 0
            val maxH = if (isUp) upHalf else downHalf
            val barHeight = max(6.dp.toPx(), (abs(net) / absMax * maxH).toFloat())
            val barY = if (isUp) zeroY - barHeight else zeroY

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = if (isUp) listOf(posTop, posBottom) else listOf(negTop, negBottom),
                    startY = barY, endY = barY + barHeight
                ),
                topLeft = Offset(x, barY),
                size = androidx.compose.ui.geometry.Size(barWidthPx, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
            )
            drawRoundRect(
                color = outline,
                topLeft = Offset(x, barY),
                size = androidx.compose.ui.geometry.Size(barWidthPx, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            drawContext.canvas.nativeCanvas.apply {
                // মান-লেবেল (বারের বাইরের প্রান্তে)
                if (net != 0.0) {
                    val paint = android.graphics.Paint().apply {
                        textSize = 13.sp.toPx()
                        isFakeBoldText = true
                        isAntiAlias = true
                        textAlign = android.graphics.Paint.Align.CENTER
                        this.color = if (isUp) android.graphics.Color.rgb(63, 140, 125) else android.graphics.Color.rgb(169, 88, 62)
                    }
                    val labelY = if (isUp) barY - 8.dp.toPx() else barY + barHeight + 20.dp.toPx()
                    drawText(formatCompactPlain(net), x + barWidthPx / 2, labelY, paint)
                }

                // সপ্তাহের লেবেল — উপরে উল্লম্বভাবে (৯০° ঘোরানো, নিচ থেকে উপরে পড়া যায়)
                val lp = android.graphics.Paint().apply {
                    textSize = 13.sp.toPx()
                    isAntiAlias = true
                    textAlign = android.graphics.Paint.Align.LEFT
                    this.color = android.graphics.Color.rgb(74, 71, 64)
                }
                save()
                translate(x + barWidthPx / 2 + 5.dp.toPx(), labelBand - 6.dp.toPx())
                rotate(-90f)
                drawText(report.label, 0f, 0f, lp)
                restore()
            }
        }
    }
}

/** "১৬.৭হা" / "-১১.৬হা" — লাভে চিহ্ন নেই, লোকসানে হাইফেন (রেফারেন্স স্ক্রিনশটের ফরম্যাট) */
private fun formatCompactPlain(v: Double): String {
    val a = abs(v)
    val body = when {
        a >= 100000 -> "%.1fলা".format(java.util.Locale.US, a / 100000)
        a >= 1000 -> "%.1fহা".format(java.util.Locale.US, a / 1000)
        else -> kotlin.math.round(a).toLong().toString()
    }
    return ((if (v < 0) "-" else "") + body).toBengaliDigits()
}
