package com.imran.clothstore.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.imran.clothstore.R

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

/**
 * Material Symbols Outlined "group" (opsz 24, wght 400, FILL 0, GRAD 0) — ImageVector আকারে।
 * ভিউবক্স 960x960 (SVG-র "0 -960 960 960" কে (0,0)-ভিত্তিক করতে y-এ +960 অফসেট করা হয়েছে)।
 */
private val MaterialGroupIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Group",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            // M40-160 → (40, 800)
            moveTo(40f, 800f)
            verticalLineToRelative(-112f)
            quadToRelative(0f, -34f, 17.5f, -62.5f)
            reflectiveQuadTo(104f, 582f)
            quadToRelative(62f, -31f, 126f, -46.5f)
            reflectiveQuadTo(360f, 520f)
            quadToRelative(66f, 0f, 130f, 15.5f)
            reflectiveQuadTo(616f, 582f)
            quadToRelative(29f, 15f, 46.5f, 43.5f)
            reflectiveQuadTo(680f, 688f)
            verticalLineToRelative(112f)
            horizontalLineTo(40f)
            close()
            moveToRelative(720f, 0f)
            verticalLineToRelative(-120f)
            quadToRelative(0f, -44f, -24.5f, -84.5f)
            reflectiveQuadTo(666f, 526f)
            quadToRelative(51f, 6f, 96f, 20.5f)
            reflectiveQuadToRelative(84f, 35.5f)
            quadToRelative(36f, 20f, 55f, 44.5f)
            reflectiveQuadToRelative(19f, 53.5f)
            verticalLineToRelative(120f)
            horizontalLineTo(760f)
            close()
            moveTo(360f, 480f)
            quadToRelative(-66f, 0f, -113f, -47f)
            reflectiveQuadToRelative(-47f, -113f)
            quadToRelative(0f, -66f, 47f, -113f)
            reflectiveQuadToRelative(113f, -47f)
            quadToRelative(66f, 0f, 113f, 47f)
            reflectiveQuadToRelative(47f, 113f)
            quadToRelative(0f, 66f, -47f, 113f)
            reflectiveQuadToRelative(-113f, 47f)
            close()
            moveToRelative(400f, -160f)
            quadToRelative(0f, 66f, -47f, 113f)
            reflectiveQuadToRelative(-113f, 47f)
            quadToRelative(-11f, 0f, -28f, -2.5f)
            reflectiveQuadToRelative(-28f, -5.5f)
            quadToRelative(27f, -32f, 41.5f, -71f)
            reflectiveQuadToRelative(14.5f, -81f)
            quadToRelative(0f, -42f, -14.5f, -81f)
            reflectiveQuadTo(544f, 168f)
            quadToRelative(14f, -5f, 28f, -6.5f)
            reflectiveQuadToRelative(28f, -1.5f)
            quadToRelative(66f, 0f, 113f, 47f)
            reflectiveQuadToRelative(47f, 113f)
            close()
            moveTo(120f, 720f)
            horizontalLineToRelative(480f)
            verticalLineToRelative(-32f)
            quadToRelative(0f, -11f, -5.5f, -20f)
            reflectiveQuadTo(580f, 654f)
            quadToRelative(-54f, -27f, -109f, -40.5f)
            reflectiveQuadTo(360f, 600f)
            quadToRelative(-56f, 0f, -111f, 13.5f)
            reflectiveQuadTo(140f, 654f)
            quadToRelative(-9f, 5f, -14.5f, 14f)
            reflectiveQuadToRelative(-5.5f, 20f)
            verticalLineToRelative(32f)
            close()
            moveToRelative(240f, -320f)
            quadToRelative(33f, 0f, 56.5f, -23.5f)
            reflectiveQuadTo(440f, 320f)
            quadToRelative(0f, -33f, -23.5f, -56.5f)
            reflectiveQuadTo(360f, 240f)
            quadToRelative(-33f, 0f, -56.5f, 23.5f)
            reflectiveQuadTo(280f, 320f)
            quadToRelative(0f, 33f, 23.5f, 56.5f)
            reflectiveQuadTo(360f, 400f)
            close()
            moveToRelative(0f, 320f)
            close()
            moveToRelative(0f, -320f)
            close()
        }
    }.build()
}

/* ============================================================================
 * Material Symbols Outlined (opsz 24, wght 400, FILL 0, GRAD 0, ROND 50) —
 * viewport 24x24। প্রতিটা আইকন lazy ImageVector, কালো fill; রং কার্ডের Image()-তে
 * ColorFilter.tint দিয়ে বসে।
 * ========================================================================== */

/** রেগুলার মহাজন — delivery_truck_bolt */
private val DeliveryTruckBoltIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "delivery_truck_bolt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(4.88f, 19.13f)
            quadTo(4f, 18.25f, 4f, 17f)
            horizontalLineTo(1.5f)
            lineTo(1.95f, 15f)
            horizontalLineTo(4.78f)
            quadToRelative(0.42f, -0.47f, 1f, -0.74f)
            reflectiveQuadTo(7f, 14f)
            reflectiveQuadToRelative(1.22f, 0.26f)
            quadToRelative(0.58f, 0.26f, 1f, 0.74f)
            horizontalLineTo(13.4f)
            lineTo(15.5f, 6f)
            horizontalLineTo(6.55f)
            lineTo(6.98f, 4f)
            horizontalLineTo(18f)
            lineTo(17.08f, 8f)
            horizontalLineTo(20f)
            lineToRelative(3f, 4f)
            lineToRelative(-1f, 5f)
            horizontalLineTo(20f)
            quadToRelative(0f, 1.25f, -0.88f, 2.13f)
            reflectiveQuadTo(17f, 20f)
            reflectiveQuadTo(14.88f, 19.13f)
            reflectiveQuadTo(14f, 17f)
            horizontalLineTo(10f)
            quadToRelative(0f, 1.25f, -0.88f, 2.13f)
            reflectiveQuadTo(7f, 20f)
            reflectiveQuadTo(4.88f, 19.13f)
            close()
            moveTo(15.93f, 13f)
            horizontalLineToRelative(4.82f)
            lineToRelative(0.1f, -0.53f)
            lineTo(19f, 10f)
            horizontalLineTo(16.63f)
            lineToRelative(-0.7f, 3f)
            close()
            moveTo(15.5f, 6f)
            lineToRelative(-2.1f, 9f)
            lineToRelative(0.05f, -0.18f)
            lineTo(15.5f, 6f)
            close()
            moveToRelative(-12f, 7f)
            verticalLineTo(10f)
            horizontalLineTo(1f)
            lineTo(4.5f, 5f)
            verticalLineTo(8f)
            horizontalLineTo(7f)
            lineTo(3.5f, 13f)
            close()
            moveTo(7f, 18f)
            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
            quadTo(8f, 17.43f, 8f, 17f)
            reflectiveQuadTo(7.71f, 16.29f)
            reflectiveQuadTo(7f, 16f)
            quadTo(6.58f, 16f, 6.29f, 16.29f)
            reflectiveQuadTo(6f, 17f)
            reflectiveQuadToRelative(0.29f, 0.71f)
            reflectiveQuadTo(7f, 18f)
            close()
            moveToRelative(10f, 0f)
            quadToRelative(0.43f, 0f, 0.71f, -0.29f)
            quadTo(18f, 17.43f, 18f, 17f)
            reflectiveQuadTo(17.71f, 16.29f)
            reflectiveQuadTo(17f, 16f)
            reflectiveQuadToRelative(-0.71f, 0.29f)
            reflectiveQuadTo(16f, 17f)
            reflectiveQuadToRelative(0.29f, 0.71f)
            reflectiveQuadTo(17f, 18f)
            close()
        }
    }.build()
}

/** ইর-রেগুলার কাস্টমার — nest_wake_on_approach */
private val NestWakeOnApproachIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "nest_wake_on_approach",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(19.44f, 11.56f)
            quadTo(19f, 11.13f, 19f, 10.5f)
            verticalLineToRelative(-6f)
            quadTo(19f, 3.88f, 19.44f, 3.44f)
            reflectiveQuadTo(20.5f, 3f)
            reflectiveQuadToRelative(1.06f, 0.44f)
            reflectiveQuadTo(22f, 4.5f)
            verticalLineToRelative(6f)
            quadToRelative(0f, 0.63f, -0.44f, 1.06f)
            reflectiveQuadTo(20.5f, 12f)
            reflectiveQuadTo(19.44f, 11.56f)
            close()
            moveTo(7.18f, 10.83f)
            quadTo(6f, 9.65f, 6f, 8f)
            reflectiveQuadTo(7.18f, 5.18f)
            reflectiveQuadTo(10f, 4f)
            reflectiveQuadToRelative(2.83f, 1.18f)
            reflectiveQuadTo(14f, 8f)
            reflectiveQuadToRelative(-1.17f, 2.82f)
            reflectiveQuadTo(10f, 12f)
            reflectiveQuadTo(7.18f, 10.83f)
            close()
            moveTo(2f, 20f)
            verticalLineTo(17.2f)
            quadTo(2f, 16.35f, 2.43f, 15.64f)
            quadTo(2.85f, 14.93f, 3.6f, 14.55f)
            quadTo(5.1f, 13.8f, 6.71f, 13.4f)
            quadTo(8.33f, 13f, 10f, 13f)
            reflectiveQuadToRelative(3.29f, 0.4f)
            reflectiveQuadToRelative(3.11f, 1.15f)
            quadToRelative(0.75f, 0.38f, 1.17f, 1.09f)
            reflectiveQuadTo(18f, 17.2f)
            verticalLineTo(20f)
            horizontalLineTo(2f)
            close()
            moveTo(4f, 18f)
            horizontalLineTo(16f)
            verticalLineTo(17.2f)
            quadToRelative(0f, -0.27f, -0.13f, -0.5f)
            quadTo(15.75f, 16.48f, 15.5f, 16.35f)
            quadToRelative(-1.27f, -0.65f, -2.66f, -1f)
            reflectiveQuadTo(10f, 15f)
            reflectiveQuadTo(7.16f, 15.35f)
            reflectiveQuadToRelative(-2.66f, 1f)
            quadTo(4.25f, 16.48f, 4.13f, 16.7f)
            quadTo(4f, 16.93f, 4f, 17.2f)
            verticalLineTo(18f)
            close()
            moveTo(11.41f, 9.41f)
            quadTo(12f, 8.82f, 12f, 8f)
            reflectiveQuadTo(11.41f, 6.59f)
            reflectiveQuadTo(10f, 6f)
            quadTo(9.18f, 6f, 8.59f, 6.59f)
            quadTo(8f, 7.18f, 8f, 8f)
            reflectiveQuadTo(8.59f, 9.41f)
            reflectiveQuadTo(10f, 10f)
            reflectiveQuadTo(11.41f, 9.41f)
            close()
            moveTo(10f, 8f)
            close()
            moveToRelative(0f, 10f)
            close()
        }
    }.build()
}

/** ইর-রেগুলার মহাজন — agriculture */
private val AgricultureIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "agriculture",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(4f, 9f)
            quadTo(3.58f, 9f, 3.29f, 8.71f)
            reflectiveQuadTo(3f, 8f)
            quadTo(3f, 7.57f, 3.29f, 7.29f)
            reflectiveQuadTo(4f, 7f)
            horizontalLineTo(7f)
            quadTo(7.83f, 7f, 8.41f, 7.59f)
            reflectiveQuadTo(9f, 9f)
            horizontalLineTo(4f)
            close()
            moveToRelative(2f, 9f)
            quadToRelative(1.25f, 0f, 2.13f, -0.88f)
            reflectiveQuadTo(9f, 15f)
            reflectiveQuadTo(8.13f, 12.88f)
            reflectiveQuadTo(6f, 12f)
            reflectiveQuadTo(3.88f, 12.88f)
            reflectiveQuadTo(3f, 15f)
            reflectiveQuadToRelative(0.88f, 2.13f)
            reflectiveQuadTo(6f, 18f)
            close()
            moveTo(20.56f, 17.56f)
            quadTo(21f, 17.13f, 21f, 16.5f)
            reflectiveQuadTo(20.56f, 15.44f)
            reflectiveQuadTo(19.5f, 15f)
            reflectiveQuadToRelative(-1.06f, 0.44f)
            reflectiveQuadTo(18f, 16.5f)
            reflectiveQuadToRelative(0.44f, 1.06f)
            reflectiveQuadTo(19.5f, 18f)
            reflectiveQuadToRelative(1.06f, -0.44f)
            close()
            moveTo(6f, 16.5f)
            quadToRelative(-0.63f, 0f, -1.06f, -0.44f)
            reflectiveQuadTo(4.5f, 15f)
            reflectiveQuadTo(4.94f, 13.94f)
            reflectiveQuadTo(6f, 13.5f)
            reflectiveQuadToRelative(1.06f, 0.44f)
            reflectiveQuadTo(7.5f, 15f)
            reflectiveQuadTo(7.06f, 16.06f)
            reflectiveQuadTo(6f, 16.5f)
            close()
            moveTo(20f, 13.02f)
            quadToRelative(0.65f, 0.13f, 1.08f, 0.34f)
            reflectiveQuadTo(22f, 14.05f)
            verticalLineTo(8f)
            quadTo(22f, 7.18f, 21.41f, 6.59f)
            reflectiveQuadTo(20f, 6f)
            horizontalLineTo(13.7f)
            lineTo(12.65f, 4.9f)
            lineToRelative(1.4f, -1.4f)
            lineTo(13.35f, 2.8f)
            lineTo(9.8f, 6.35f)
            lineToRelative(0.75f, 0.7f)
            lineToRelative(1.4f, -1.4f)
            lineTo(13f, 6.7f)
            verticalLineTo(9f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(11f, 11f)
            horizontalLineTo(8.98f)
            quadToRelative(0.58f, 0.42f, 0.93f, 0.88f)
            reflectiveQuadTo(10.6f, 13f)
            horizontalLineTo(11f)
            quadToRelative(1.65f, 0f, 2.83f, -1.18f)
            reflectiveQuadTo(15f, 9f)
            verticalLineTo(8f)
            horizontalLineToRelative(5f)
            verticalLineToRelative(5.02f)
            close()
            moveTo(16.03f, 16f)
            quadToRelative(0.15f, -0.68f, 0.36f, -1.09f)
            reflectiveQuadTo(17.05f, 14f)
            horizontalLineTo(10.9f)
            quadTo(11f, 14.58f, 11f, 15f)
            reflectiveQuadToRelative(-0.1f, 1f)
            horizontalLineToRelative(5.13f)
            close()
            moveToRelative(3.47f, 4f)
            quadToRelative(-1.45f, 0f, -2.47f, -1.02f)
            reflectiveQuadTo(16f, 16.5f)
            reflectiveQuadToRelative(1.03f, -2.48f)
            reflectiveQuadTo(19.5f, 13f)
            reflectiveQuadToRelative(2.48f, 1.02f)
            reflectiveQuadTo(23f, 16.5f)
            reflectiveQuadToRelative(-1.02f, 2.48f)
            reflectiveQuadTo(19.5f, 20f)
            close()
            moveTo(6f, 20f)
            quadTo(3.93f, 20f, 2.46f, 18.54f)
            reflectiveQuadTo(1f, 15f)
            reflectiveQuadTo(2.46f, 11.46f)
            reflectiveQuadTo(6f, 10f)
            quadToRelative(2.08f, 0f, 3.54f, 1.46f)
            quadTo(11f, 12.93f, 11f, 15f)
            reflectiveQuadTo(9.54f, 18.54f)
            quadTo(8.08f, 20f, 6f, 20f)
            close()
            moveToRelative(9.83f, -9f)
            close()
        }
    }.build()
}

/** সাপ্তাহিক রিপোর্ট — note_stack */
private val NoteStackIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "note_stack",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(7f, 20f)
            verticalLineTo(8.98f)
            quadTo(7f, 8.15f, 7.6f, 7.57f)
            reflectiveQuadTo(9.03f, 7f)
            horizontalLineTo(20f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(22f, 9f)
            verticalLineToRelative(8f)
            lineToRelative(-5f, 5f)
            horizontalLineTo(9f)
            quadTo(8.18f, 22f, 7.59f, 21.41f)
            reflectiveQuadTo(7f, 20f)
            close()
            moveTo(2.03f, 6.25f)
            quadTo(1.88f, 5.43f, 2.35f, 4.76f)
            reflectiveQuadTo(3.65f, 3.95f)
            lineTo(14.5f, 2.02f)
            quadToRelative(0.83f, -0.15f, 1.49f, 0.32f)
            reflectiveQuadToRelative(0.81f, 1.3f)
            lineTo(17.05f, 5f)
            horizontalLineTo(15f)
            lineTo(14.83f, 4f)
            lineTo(4f, 5.93f)
            lineToRelative(1f, 5.65f)
            verticalLineToRelative(6.98f)
            quadTo(4.6f, 18.33f, 4.31f, 17.95f)
            quadTo(4.03f, 17.58f, 3.95f, 17.1f)
            lineTo(2.03f, 6.25f)
            close()
            moveTo(9f, 9f)
            verticalLineTo(20f)
            horizontalLineToRelative(7f)
            verticalLineTo(16f)
            horizontalLineToRelative(4f)
            verticalLineTo(9f)
            horizontalLineTo(9f)
            close()
            moveToRelative(5.5f, 5.5f)
            close()
        }
    }.build()
}

/** P&L Trends — insert_chart */
private val InsertChartIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "insert_chart",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(7f, 17f)
            horizontalLineTo(9f)
            verticalLineTo(10f)
            horizontalLineTo(7f)
            verticalLineToRelative(7f)
            close()
            moveToRelative(4f, 0f)
            horizontalLineToRelative(2f)
            verticalLineTo(7f)
            horizontalLineTo(11f)
            verticalLineTo(17f)
            close()
            moveToRelative(4f, 0f)
            horizontalLineToRelative(2f)
            verticalLineTo(13f)
            horizontalLineTo(15f)
            verticalLineToRelative(4f)
            close()
            moveTo(5f, 21f)
            quadTo(4.18f, 21f, 3.59f, 20.41f)
            reflectiveQuadTo(3f, 19f)
            verticalLineTo(5f)
            quadTo(3f, 4.17f, 3.59f, 3.59f)
            reflectiveQuadTo(5f, 3f)
            horizontalLineTo(19f)
            quadToRelative(0.83f, 0f, 1.41f, 0.59f)
            reflectiveQuadTo(21f, 5f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(19f, 21f)
            horizontalLineTo(5f)
            close()
            moveTo(5f, 19f)
            horizontalLineTo(19f)
            verticalLineTo(5f)
            horizontalLineTo(5f)
            verticalLineTo(19f)
            close()
            moveTo(5f, 5f)
            verticalLineTo(19f)
            verticalLineTo(5f)
            close()
        }
    }.build()
}

/** কাপড় ক্রয় হিসাব — local_convenience_store */
private val LocalConvenienceStoreIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "local_convenience_store",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.NonZero) {
            moveTo(8.03f, 18f)
            horizontalLineToRelative(3f)
            verticalLineTo(17f)
            horizontalLineToRelative(-2f)
            verticalLineTo(16f)
            horizontalLineToRelative(2f)
            verticalLineTo(13f)
            horizontalLineToRelative(-3f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(3f)
            close()
            moveToRelative(7f, 0f)
            horizontalLineToRelative(1f)
            verticalLineTo(13f)
            horizontalLineToRelative(-1f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(-1f)
            verticalLineTo(13f)
            horizontalLineToRelative(-1f)
            verticalLineToRelative(3f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(2f)
            close()
            moveToRelative(6f, -6.95f)
            verticalLineTo(19f)
            quadToRelative(0f, 0.82f, -0.59f, 1.41f)
            reflectiveQuadTo(19.03f, 21f)
            horizontalLineToRelative(-14f)
            quadTo(4.2f, 21f, 3.61f, 20.41f)
            reflectiveQuadTo(3.03f, 19f)
            verticalLineTo(11.05f)
            quadTo(2.45f, 10.52f, 2.14f, 9.7f)
            reflectiveQuadTo(2.13f, 7.9f)
            lineTo(3.18f, 4.5f)
            quadTo(3.38f, 3.85f, 3.89f, 3.42f)
            reflectiveQuadTo(5.08f, 3f)
            horizontalLineToRelative(13.9f)
            quadToRelative(0.68f, 0f, 1.18f, 0.41f)
            reflectiveQuadTo(20.88f, 4.5f)
            lineToRelative(1.05f, 3.4f)
            quadToRelative(0.3f, 0.98f, -0.01f, 1.78f)
            quadToRelative(-0.31f, 0.8f, -0.89f, 1.38f)
            close()
            moveTo(14.23f, 10f)
            quadToRelative(0.67f, 0f, 1.02f, -0.46f)
            reflectiveQuadTo(15.53f, 8.5f)
            lineTo(14.98f, 5f)
            horizontalLineTo(13.03f)
            verticalLineTo(8.7f)
            quadToRelative(0f, 0.53f, 0.35f, 0.91f)
            reflectiveQuadTo(14.23f, 10f)
            close()
            moveToRelative(-4.5f, 0f)
            quadToRelative(0.57f, 0f, 0.94f, -0.39f)
            quadTo(11.03f, 9.23f, 11.03f, 8.7f)
            verticalLineTo(5f)
            horizontalLineTo(9.08f)
            lineTo(8.53f, 8.5f)
            quadTo(8.43f, 9.1f, 8.79f, 9.55f)
            reflectiveQuadTo(9.73f, 10f)
            close()
            moveTo(5.28f, 10f)
            quadTo(5.73f, 10f, 6.06f, 9.67f)
            reflectiveQuadTo(6.48f, 8.85f)
            lineTo(7.03f, 5f)
            horizontalLineTo(5.08f)
            lineToRelative(-1f, 3.35f)
            quadTo(3.93f, 8.85f, 4.24f, 9.42f)
            reflectiveQuadTo(5.28f, 10f)
            close()
            moveToRelative(13.5f, 0f)
            quadToRelative(0.72f, 0f, 1.05f, -0.58f)
            reflectiveQuadTo(19.98f, 8.35f)
            lineTo(18.93f, 5f)
            horizontalLineToRelative(-1.9f)
            lineToRelative(0.55f, 3.85f)
            quadToRelative(0.08f, 0.5f, 0.41f, 0.82f)
            reflectiveQuadTo(18.78f, 10f)
            close()
            moveTo(5.03f, 19f)
            horizontalLineToRelative(14f)
            verticalLineTo(11.95f)
            quadTo(18.9f, 12f, 18.86f, 12f)
            reflectiveQuadToRelative(-0.09f, 0f)
            quadTo(18.1f, 12f, 17.59f, 11.77f)
            reflectiveQuadTo(16.58f, 11.05f)
            quadToRelative(-0.45f, 0.45f, -1.02f, 0.7f)
            reflectiveQuadTo(14.33f, 12f)
            quadToRelative(-0.68f, 0f, -1.26f, -0.25f)
            reflectiveQuadToRelative(-1.04f, -0.7f)
            quadToRelative(-0.42f, 0.45f, -0.99f, 0.7f)
            reflectiveQuadTo(9.83f, 12f)
            quadTo(9.1f, 12f, 8.51f, 11.75f)
            reflectiveQuadTo(7.48f, 11.05f)
            quadTo(6.95f, 11.58f, 6.44f, 11.79f)
            reflectiveQuadTo(5.28f, 12f)
            quadTo(5.23f, 12f, 5.16f, 12f)
            reflectiveQuadTo(5.03f, 11.95f)
            verticalLineTo(19f)
            close()
            moveToRelative(14f, 0f)
            horizontalLineToRelative(-14f)
            quadToRelative(0.08f, 0f, 0.14f, 0f)
            reflectiveQuadToRelative(0.11f, 0f)
            quadToRelative(0.65f, 0f, 1.16f, 0f)
            reflectiveQuadToRelative(1.04f, 0f)
            quadToRelative(0.22f, 0f, 0.49f, 0f)
            reflectiveQuadToRelative(0.56f, 0f)
            reflectiveQuadToRelative(0.63f, 0f)
            reflectiveQuadToRelative(0.68f, 0f)
            quadToRelative(0.32f, 0f, 0.63f, 0f)
            reflectiveQuadToRelative(0.59f, 0f)
            reflectiveQuadToRelative(0.54f, 0f)
            reflectiveQuadToRelative(0.45f, 0f)
            quadToRelative(0.45f, 0f, 1.04f, 0f)
            reflectiveQuadToRelative(1.26f, 0f)
            quadToRelative(0.32f, 0f, 0.63f, 0f)
            reflectiveQuadToRelative(0.59f, 0f)
            reflectiveQuadToRelative(0.55f, 0f)
            reflectiveQuadToRelative(0.49f, 0f)
            quadToRelative(0.5f, 0f, 1.01f, 0f)
            reflectiveQuadToRelative(1.19f, 0f)
            quadToRelative(0.05f, 0f, 0.09f, 0f)
            reflectiveQuadToRelative(0.16f, 0f)
            close()
        }
    }.build()
}

/** কার্ড ১: রেগুলার কাস্টমার — Material Symbols "group" আইকন */
@Composable
fun CustomerArt(modifier: Modifier = Modifier) {
    Image(
        imageVector = MaterialGroupIcon,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF3B82F6))
    )
}

/** কার্ড ২: ইর-রেগুলার কাস্টমার — Material Symbols "nest_wake_on_approach" */
@Composable
fun CustomerIrregularArt(modifier: Modifier = Modifier) {
    Image(
        imageVector = NestWakeOnApproachIcon,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF8B5CF6))
    )
}

/** কার্ড ৩: রেগুলার মহাজন — Material Symbols "delivery_truck_bolt" */
@Composable
fun SupplierTruckArt(modifier: Modifier = Modifier) {
    Image(
        imageVector = DeliveryTruckBoltIcon,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFF59E0B))
    )
}

/** কার্ড ৪: ইর-রেগুলার মহাজন — Material Symbols "agriculture" */
@Composable
fun SupplierIrregularArt(modifier: Modifier = Modifier) {
    Image(
        imageVector = AgricultureIcon,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFEF6C3D))
    )
}

/** কার্ড ৭: কাপড় ক্রয় হিসাব — Material Symbols "local_convenience_store" */
@Composable
fun FabricArt(modifier: Modifier = Modifier) {
    Image(
        imageVector = LocalConvenienceStoreIcon,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF2E7D6B))
    )
}

/** কার্ড ৬: P&L Trends — Material Symbols "insert_chart" */
@Composable
fun PLTrendsArt(modifier: Modifier = Modifier) {
    Image(
        imageVector = InsertChartIcon,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFB45309))
    )
}

/** কার্ড ৮: সাপ্তাহিক রিপোর্ট — Material Symbols "note_stack" */
@Composable
fun ReportArt(modifier: Modifier = Modifier) {
    Image(
        imageVector = NoteStackIcon,
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFC9573C))
    )
}


/** কার্ড ৯: ডাটাবেইজ — Material Symbols "database_upload" */
@Composable
fun DatabaseArt(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_database_upload),
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF3B82F6))
    )
}

/** কার্ড ১০: পার্টি — Material Symbols "groups" */
@Composable
fun PartyArt(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_groups),
        contentDescription = null,
        modifier = modifier.size(56.dp),
        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFFF08A3C))
    )
}
