package com.imran.clothstore.ui.screens.fvlist

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Material Symbols Outlined — person_add (24dp, wght 400) */
internal val person_add: ImageVector by lazy {
    ImageVector.Builder(
        name = "person_add",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(18f, 14f)
            verticalLineTo(11f)
            horizontalLineTo(15f)
            verticalLineTo(9f)
            horizontalLineToRelative(3f)
            verticalLineTo(6f)
            horizontalLineToRelative(2f)
            verticalLineTo(9f)
            horizontalLineToRelative(3f)
            verticalLineToRelative(2f)
            horizontalLineTo(20f)
            verticalLineToRelative(3f)
            horizontalLineTo(18f)
            close()
            moveTo(6.18f, 10.83f)
            quadTo(5f, 9.65f, 5f, 8f)
            reflectiveQuadTo(6.18f, 5.18f)
            reflectiveQuadTo(9f, 4f)
            reflectiveQuadToRelative(2.83f, 1.18f)
            reflectiveQuadTo(13f, 8f)
            reflectiveQuadToRelative(-1.17f, 2.82f)
            reflectiveQuadTo(9f, 12f)
            reflectiveQuadTo(6.18f, 10.83f)
            close()
            moveTo(1f, 20f)
            verticalLineTo(17.2f)
            quadTo(1f, 16.35f, 1.44f, 15.64f)
            quadTo(1.88f, 14.93f, 2.6f, 14.55f)
            quadTo(4.15f, 13.77f, 5.75f, 13.39f)
            reflectiveQuadTo(9f, 13f)
            reflectiveQuadToRelative(3.25f, 0.39f)
            reflectiveQuadToRelative(3.15f, 1.16f)
            quadToRelative(0.72f, 0.38f, 1.16f, 1.09f)
            reflectiveQuadTo(17f, 17.2f)
            verticalLineTo(20f)
            horizontalLineTo(1f)
            close()
            moveTo(3f, 18f)
            horizontalLineTo(15f)
            verticalLineTo(17.2f)
            quadToRelative(0f, -0.27f, -0.14f, -0.5f)
            quadTo(14.73f, 16.48f, 14.5f, 16.35f)
            quadTo(13.15f, 15.68f, 11.78f, 15.34f)
            reflectiveQuadTo(9f, 15f)
            reflectiveQuadTo(6.23f, 15.34f)
            reflectiveQuadTo(3.5f, 16.35f)
            quadTo(3.28f, 16.48f, 3.14f, 16.7f)
            quadTo(3f, 16.93f, 3f, 17.2f)
            verticalLineTo(18f)
            close()
            moveTo(10.41f, 9.41f)
            quadTo(11f, 8.82f, 11f, 8f)
            reflectiveQuadTo(10.41f, 6.59f)
            reflectiveQuadTo(9f, 6f)
            quadTo(8.18f, 6f, 7.59f, 6.59f)
            quadTo(7f, 7.18f, 7f, 8f)
            reflectiveQuadTo(7.59f, 9.41f)
            reflectiveQuadTo(9f, 10f)
            quadToRelative(0.83f, 0f, 1.41f, -0.59f)
            close()
            moveTo(9f, 8f)
            close()
            moveTo(9f, 18f)
            close()
        }
    }.build()
}

/**
 * WhatsApp লোগো (whatsapp.svg থেকে — শুধু বাবল + ফোন অংশ, ডানের "WhatsApp" লেখা বাদ)।
 * ট্রান্সপারেন্ট পটভূমির উপর একরঙা (tint-যোগ্য) — Icon(tint = ...) দিয়ে রং বদলানো যায়।
 * ভিউপোর্ট 146.02 × 147.154 (SVG-র লোগো অংশের বাউন্ডিং)।
 */
internal val WhatsAppIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "whatsapp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 146.02f,
        viewportHeight = 147.154f,
    ).apply {
        // বাইরের বাবল আউটলাইন (SVG-র সাদা পাথ: বাইরের আকৃতি − ভিতরের বৃত্ত = রিং)
        path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
            moveTo(74.34f, 0f)
            curveTo(34.76f, 0f, 2.659f, 32.104f, 2.659f, 71.682f)
            curveToRelative(0f, 13.535f, 3.756f, 26.22f, 10.275f, 37.027f)
            lineTo(0.001f, 147.154f)
            lineToRelative(39.686f, -12.72f)
            arcToRelative(71.502f, 71.502f, 0f, false, false, 34.653f, 8.929f)
            curveToRelative(39.579f, 0f, 71.681f, -32.102f, 71.681f, -71.681f)
            curveTo(146.02f, 32.104f, 113.919f, 0f, 74.34f, 0f)
            close()
            moveTo(74.34f, 131.953f)
            curveToRelative(-12.225f, 0f, -23.634f, -3.685f, -33.165f, -9.956f)
            lineToRelative(-23.174f, 7.404f)
            lineToRelative(7.512f, -22.393f)
            curveToRelative(-7.228f, -9.922f, -11.479f, -22.146f, -11.479f, -35.327f)
            curveToRelative(0f, -33.236f, 27.035f, -60.271f, 60.271f, -60.271f)
            reflectiveCurveToRelative(60.271f, 27.035f, 60.271f, 60.271f)
            curveToRelative(0f, 33.235f, -27.035f, 60.271f, -60.271f, 60.271f)
            horizontalLineToRelative(0.035f)
            close()
        }
        // ভিতরের হ্যান্ডসেট
        path(fill = SolidColor(Color.Black)) {
            moveTo(57.509f, 42.025f)
            curveToRelative(-1.169f, -2.799f, -2.055f, -2.905f, -3.826f, -2.977f)
            arcToRelative(34.047f, 34.047f, 0f, false, false, -2.02f, -0.07f)
            curveToRelative(-2.304f, 0f, -4.713f, 0.673f, -6.166f, 2.161f)
            curveToRelative(-1.771f, 1.807f, -6.165f, 6.023f, -6.165f, 14.669f)
            reflectiveCurveToRelative(6.307f, 17.008f, 7.157f, 18.178f)
            curveToRelative(0.886f, 1.168f, 12.296f, 19.168f, 30.012f, 26.504f)
            curveToRelative(13.854f, 5.74f, 17.965f, 5.208f, 21.118f, 4.535f)
            curveToRelative(4.606f, -0.992f, 10.382f, -4.395f, 11.835f, -8.504f)
            curveToRelative(1.453f, -4.111f, 1.453f, -7.619f, 1.027f, -8.363f)
            curveToRelative(-0.425f, -0.744f, -1.595f, -1.168f, -3.366f, -2.055f)
            curveToRelative(-1.771f, -0.886f, -10.382f, -5.138f, -12.012f, -5.705f)
            curveToRelative(-1.595f, -0.602f, -3.118f, -0.389f, -4.322f, 1.312f)
            curveToRelative(-1.701f, 2.374f, -3.366f, 4.784f, -4.713f, 6.236f)
            curveToRelative(-1.063f, 1.134f, -2.8f, 1.276f, -4.252f, 0.673f)
            curveToRelative(-1.949f, -0.814f, -7.405f, -2.729f, -14.138f, -8.717f)
            curveToRelative(-5.209f, -4.641f, -8.752f, -10.416f, -9.779f, -12.152f)
            curveToRelative(-1.028f, -1.771f, -0.106f, -2.8f, 0.708f, -3.756f)
            curveToRelative(0.886f, -1.099f, 1.736f, -1.878f, 2.622f, -2.906f)
            curveToRelative(0.886f, -1.027f, 1.382f, -1.559f, 1.949f, -2.764f)
            curveToRelative(0.603f, -1.169f, 0.177f, -2.374f, -0.248f, -3.26f)
            curveToRelative(-0.425f, -0.886f, -3.968f, -9.532f, -5.421f, -13.039f)
            close()
        }
    }.build()
}
