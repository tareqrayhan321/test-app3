package com.imran.clothstore.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp

/**
 * মূল ওয়েব অ্যাপে প্রতিটা হোম কার্ডের একটা হাতে-আঁকা inline SVG আইকন ছিল
 * (viewBox 0 0 64 64)। এখানে সেগুলোকে Compose Canvas দিয়ে পুনর্গঠন করা হয়েছে —
 * একই রং, একই আনুপাতিক আকার। viewBox 64x64 কে Canvas এর size অনুযায়ী স্কেল করা হয়।
 */

private fun scale(canvasSize: Float) = canvasSize / 64f

/** মাঝের/মূল ফিগারের বুকে একটা ছোট ৳ ব্যাজ আঁকে — ওয়েব অ্যাপের কাস্টমার আইকনে
 *  থাকা টাকার চিহ্নের সমতুল্য (রেফারেন্স স্ক্রিনশট অনুযায়ী)। */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawTakaBadge(
    center: Offset,
    radius: Float,
    badgeColor: Color = Color(0xFFFFFFFF),
    symbolColor: Color = Color(0xFF141413)
) {
    drawCircle(badgeColor, radius = radius, center = center)
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = symbolColor.toArgb()
            textSize = radius * 1.3f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            isFakeBoldText = true
        }
        val fm = paint.fontMetrics
        val textY = center.y - (fm.ascent + fm.descent) / 2f
        drawText("৳", center.x, textY, paint)
    }
}

/** কার্ড ১: রেগুলার কাস্টমার — তিনটা মানুষের সিলুয়েট (কমলা/নীল জ্যাকেট) */
@Composable
fun CustomerArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        // ছায়া
        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(10f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(44f), dp(6f))
        )
        // বাম মানুষ (কমলা)
        drawCircle(Color(0xFFF2B56B), radius = dp(6.5f), center = Offset(dp(14f), dp(28f)))
        drawCircle(Color(0xFFF08A3C), radius = dp(11f), center = Offset(dp(14f), dp(46f)))
        // ডান মানুষ (সবুজ)
        drawCircle(Color(0xFFF2B56B), radius = dp(6.5f), center = Offset(dp(50f), dp(28f)))
        drawCircle(Color(0xFF2EC4A0), radius = dp(11f), center = Offset(dp(50f), dp(46f)))
        // মাঝের মানুষ (নীল, বড়)
        drawCircle(Color(0xFFFFCF9C), radius = dp(8.5f), center = Offset(dp(32f), dp(22f)))
        drawCircle(Color(0xFF3B82F6), radius = dp(16f), center = Offset(dp(32f), dp(50f)))
        // মাঝের মানুষের বুকে ৳ ব্যাজ (ওয়েব রেফারেন্সের সাথে মিলিয়ে)
        drawTakaBadge(center = Offset(dp(32f), dp(42f)), radius = dp(6f))
    }
}

/** কার্ড ২: ইর-রেগুলার কাস্টমার — একজন মানুষ (বেগুনি) + লাল ব্যাজ */
@Composable
fun CustomerIrregularArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(12f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(36f), dp(6f))
        )
        drawCircle(Color(0xFFFFCF9C), radius = dp(10f), center = Offset(dp(28f), dp(20f)))
        drawCircle(Color(0xFF8B5CF6), radius = dp(20f), center = Offset(dp(28f), dp(55f)))
        // লাল সতর্কতা ব্যাজ
        drawCircle(Color(0xFFEF5350), radius = dp(10.5f), center = Offset(dp(49f), dp(17f)))
        drawCircle(
            Color.Transparent,
            radius = dp(10.5f),
            center = Offset(dp(49f), dp(17f)),
            style = Stroke(width = dp(2.2f))
        )
        drawLine(
            Color.White,
            start = Offset(dp(43.5f), dp(17f)),
            end = Offset(dp(54.5f), dp(17f)),
            strokeWidth = dp(3.4f),
            cap = StrokeCap.Round
        )
    }
}

/** কার্ড ৩: রেগুলার মহাজন — মালবাহী ট্রাক (কমলা কেবিন) */
@Composable
fun SupplierTruckArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(8f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(48f), dp(6f))
        )
        // কার্গো বক্স
        drawRoundRect(
            color = Color(0xFFF59E0B),
            topLeft = Offset(dp(4f), dp(16f)),
            size = androidx.compose.ui.geometry.Size(dp(32f), dp(28f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(3.5f))
        )
        // কেবিন
        drawRoundRect(
            color = Color(0xFF2E7D6B),
            topLeft = Offset(dp(36f), dp(25f)),
            size = androidx.compose.ui.geometry.Size(dp(20f), dp(19f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
        // বাম্পার
        drawRoundRect(
            color = Color(0xFF374151),
            topLeft = Offset(dp(4f), dp(43f)),
            size = androidx.compose.ui.geometry.Size(dp(52f), dp(4f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
        // চাকা
        drawCircle(Color(0xFF1F2937), radius = dp(6.5f), center = Offset(dp(16f), dp(48f)))
        drawCircle(Color(0xFFD1D5DB), radius = dp(3f), center = Offset(dp(16f), dp(48f)))
        drawCircle(Color(0xFF1F2937), radius = dp(6.5f), center = Offset(dp(46f), dp(48f)))
        drawCircle(Color(0xFFD1D5DB), radius = dp(3f), center = Offset(dp(46f), dp(48f)))
    }
}

/** কার্ড ৪: ইর-রেগুলার মহাজন — ট্রাক (লাল কেবিন) + সবুজ তীর (সতর্কতা) */
@Composable
fun SupplierIrregularArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(8f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(48f), dp(6f))
        )
        drawRoundRect(
            color = Color(0xFFEF6C3D),
            topLeft = Offset(dp(24f), dp(22f)),
            size = androidx.compose.ui.geometry.Size(dp(34f), dp(22f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(3.5f))
        )
        drawRoundRect(
            color = Color(0xFF7C6BC4),
            topLeft = Offset(dp(8f), dp(32f)),
            size = androidx.compose.ui.geometry.Size(dp(17f), dp(12f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
        drawRoundRect(
            color = Color(0xFF374151),
            topLeft = Offset(dp(8f), dp(43f)),
            size = androidx.compose.ui.geometry.Size(dp(50f), dp(4f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
        drawCircle(Color(0xFF1F2937), radius = dp(6.2f), center = Offset(dp(19f), dp(48f)))
        drawCircle(Color(0xFF1F2937), radius = dp(6.2f), center = Offset(dp(47f), dp(48f)))
        // বাক্স (হলুদ)
        drawRoundRect(
            color = Color(0xFFFBBF24),
            topLeft = Offset(dp(29f), dp(5f)),
            size = androidx.compose.ui.geometry.Size(dp(16f), dp(14f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
    }
}

/** কার্ড ৯: ডাটাবেইজ সংযোগ — সিলিন্ডার ডাটাবেইজ + সবুজ চেকমার্ক */
@Composable
fun DatabaseArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(12f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(40f), dp(6f))
        )
        // সিলিন্ডার বডি
        drawRoundRect(
            color = Color(0xFF3B82F6),
            topLeft = Offset(dp(12f), dp(14f)),
            size = androidx.compose.ui.geometry.Size(dp(40f), dp(32f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(6f))
        )
        // উপরের ঢাকনা
        drawOval(
            color = Color(0xFF7FB2FF),
            topLeft = Offset(dp(12f), dp(5.5f)),
            size = androidx.compose.ui.geometry.Size(dp(40f), dp(17f))
        )
        // সবুজ চেক ব্যাজ
        drawCircle(Color(0xFF22C55E), radius = dp(9.5f), center = Offset(dp(50f), dp(50f)))
    }
}

/** কার্ড ১০: পার্টি — দুইটা পতাকা + গিফট বক্স স্টাইল মুখ */
@Composable
fun PartyArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(10f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(44f), dp(6f))
        )
        // দুই দিকের রঙিন ব্যানার
        drawCircle(Color(0xFF3B82F6), radius = dp(11f), center = Offset(dp(12f), dp(30f)))
        drawCircle(Color(0xFFF08A3C), radius = dp(11f), center = Offset(dp(52f), dp(30f)))
        // কেন্দ্রীয় মুখ
        drawCircle(Color(0xFFFFD6A8), radius = dp(14f), center = Offset(dp(32f), dp(32f)))
    }
}

/** কার্ড ৭: কাপড় ক্রয় (Fabric) — কাপড়ের গোল্লা/থান কাপড় (বেগুনি/অ্যামেথিস্ট) */
@Composable
fun FabricArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(10f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(44f), dp(6f))
        )
        // কাপড়ের রোল (সিলিন্ডার)
        drawRoundRect(
            color = Color(0xFF8B5CF6),
            topLeft = Offset(dp(10f), dp(18f)),
            size = androidx.compose.ui.geometry.Size(dp(44f), dp(28f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(6f))
        )
        // রোলের মুখ (উপবৃত্ত)
        drawOval(
            color = Color(0xFFC4B5FD),
            topLeft = Offset(dp(6f), dp(16f)),
            size = androidx.compose.ui.geometry.Size(dp(14f), dp(32f))
        )
        // কাপড়ের ভাঁজ রেখা
        drawLine(
            Color(0xFF6D28D9),
            start = Offset(dp(24f), dp(22f)),
            end = Offset(dp(24f), dp(42f)),
            strokeWidth = dp(1.5f)
        )
        drawLine(
            Color(0xFF6D28D9),
            start = Offset(dp(36f), dp(20f)),
            end = Offset(dp(36f), dp(44f)),
            strokeWidth = dp(1.5f)
        )
    }
}

/**
 * কার্ড ৬: P&L Trends — ওয়েব অ্যাপের hc-art-trends SVG-র সমতুল্য (index.html লাইন ৩৩৪১-৩৩৪৯):
 * ক্রিম প্যানেল, ৩টা বাড়তে-থাকা বার (হলুদ/কমলা/সবুজ), লাল ট্রেন্ড-লাইন + তীর।
 */
@Composable
fun PLTrendsArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(10f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(44f), dp(6f))
        )
        // ক্রিম প্যানেল
        drawRoundRect(
            color = Color(0xFFFFF3E6),
            topLeft = Offset(dp(8f), dp(10f)),
            size = androidx.compose.ui.geometry.Size(dp(48f), dp(44f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(7f)),
            style = Stroke(width = dp(1.6f))
        )
        drawRoundRect(
            color = Color(0xFFFFF3E6),
            topLeft = Offset(dp(8f), dp(10f)),
            size = androidx.compose.ui.geometry.Size(dp(48f), dp(44f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(7f))
        )
        drawRoundRect(
            color = Color(0xFFF2C9A0),
            topLeft = Offset(dp(8f), dp(10f)),
            size = androidx.compose.ui.geometry.Size(dp(48f), dp(44f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(7f)),
            style = Stroke(width = dp(1.6f))
        )
        // তিনটা বাড়তে থাকা বার
        drawRoundRect(
            color = Color(0xFFF5A623),
            topLeft = Offset(dp(15f), dp(36f)),
            size = androidx.compose.ui.geometry.Size(dp(8f), dp(14f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
        drawRoundRect(
            color = Color(0xFFEF7B45),
            topLeft = Offset(dp(28f), dp(28f)),
            size = androidx.compose.ui.geometry.Size(dp(8f), dp(22f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
        drawRoundRect(
            color = Color(0xFF2BB673),
            topLeft = Offset(dp(41f), dp(18f)),
            size = androidx.compose.ui.geometry.Size(dp(8f), dp(32f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2f))
        )
        // ট্রেন্ড-লাইন (লাল)
        val trendPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(dp(14f), dp(30f))
            lineTo(dp(25f), dp(21f))
            lineTo(dp(34f), dp(26f))
            lineTo(dp(49f), dp(13f))
        }
        drawPath(
            trendPath,
            color = Color(0xFFD9452B),
            style = Stroke(width = dp(3f), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
        // তীরের মাথা
        val arrowPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(dp(43f), dp(12f))
            lineTo(dp(51f), dp(12f))
            lineTo(dp(51f), dp(20f))
        }
        drawPath(
            arrowPath,
            color = Color(0xFFD9452B),
            style = Stroke(width = dp(3f), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
    }
}

/**
 * কার্ড ৮: সাপ্তাহিক হালনাগাদ রিপোর্ট — ওয়েব অ্যাপের hc-art-report SVG-র সমতুল্য
 * (index.html লাইন ৩৩৫২-৩৩৬২): কোণ-ভাঁজ করা সাদা ডকুমেন্ট, লাল বৃত্তে ডকুমেন্ট-আইকন,
 * উপরে হেডার-বার, নিচে সবুজ চেক ব্যাজ।
 */
@Composable
fun ReportArt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(56.dp)) {
        val s = scale(size.minDimension)
        fun dp(v: Float) = v * s

        drawOval(
            color = Color.Black.copy(alpha = 0.10f),
            topLeft = Offset(dp(14f), dp(56f)),
            size = androidx.compose.ui.geometry.Size(dp(36f), dp(6f))
        )
        // ডকুমেন্ট বডি (কোণ-ভাঁজ করা)
        val docPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(dp(14f), dp(8f))
            lineTo(dp(40f), dp(8f))
            lineTo(dp(52f), dp(20f))
            lineTo(dp(52f), dp(52f))
            quadraticBezierTo(dp(52f), dp(56f), dp(48f), dp(56f))
            lineTo(dp(14f), dp(56f))
            quadraticBezierTo(dp(10f), dp(56f), dp(10f), dp(52f))
            lineTo(dp(10f), dp(12f))
            quadraticBezierTo(dp(10f), dp(8f), dp(14f), dp(8f))
            close()
        }
        drawPath(docPath, color = Color.White)
        drawPath(docPath, color = Color(0xFFC9573C), style = Stroke(width = dp(2.2f)))
        // ভাঁজ করা কোণ
        val foldPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(dp(40f), dp(8f))
            lineTo(dp(40f), dp(17f))
            quadraticBezierTo(dp(40f), dp(20f), dp(43f), dp(20f))
            lineTo(dp(52f), dp(20f))
        }
        drawPath(foldPath, color = Color(0xFFF3C9BC))
        drawPath(foldPath, color = Color(0xFFC9573C), style = Stroke(width = dp(2.2f)))
        // উপরের হেডার-বার
        drawRoundRect(
            color = Color(0xFFF3B8A6),
            topLeft = Offset(dp(18f), dp(12f)),
            size = androidx.compose.ui.geometry.Size(dp(14f), dp(3.4f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(1.7f))
        )
        // কেন্দ্রীয় লাল বৃত্ত + ডকুমেন্ট-আইকন
        drawCircle(Color(0xFFE2573D), radius = dp(11f), center = Offset(dp(31f), dp(36f)))
        val innerDoc = androidx.compose.ui.graphics.Path().apply {
            moveTo(dp(27f), dp(33f))
            lineTo(dp(35f), dp(33f))
            moveTo(dp(27f), dp(36f))
            lineTo(dp(35f), dp(36f))
        }
        // (ছোট দুই-লাইন আইকন সরলীকৃত সাদা রেখা দিয়ে)
        drawLine(Color.White, start = Offset(dp(27f), dp(33f)), end = Offset(dp(35f), dp(33f)), strokeWidth = dp(1.6f), cap = StrokeCap.Round)
        drawLine(Color.White, start = Offset(dp(27f), dp(36f)), end = Offset(dp(35f), dp(36f)), strokeWidth = dp(1.6f), cap = StrokeCap.Round)
        // নিচের সবুজ চেক ব্যাজ
        drawRoundRect(
            color = Color(0xFF2BB673),
            topLeft = Offset(dp(34f), dp(46f)),
            size = androidx.compose.ui.geometry.Size(dp(15f), dp(9f)),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(dp(2.4f))
        )
        val checkPath = androidx.compose.ui.graphics.Path().apply {
            moveTo(dp(38f), dp(50.5f))
            lineTo(dp(41f), dp(53f))
            lineTo(dp(46f), dp(48f))
        }
        drawPath(
            checkPath,
            color = Color.White,
            style = Stroke(width = dp(2f), cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
        )
    }
}
