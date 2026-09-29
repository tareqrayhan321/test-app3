package com.imran.clothstore.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.R
import com.imran.clothstore.ui.theme.AppColors

/**
 * অ্যাপ টাইটেলের ফন্ট: Li Alinur Banglaborno (Unicode)। ফাইল: res/font/alinur_banglaborno.ttf
 * এই ফন্টের আলাদা Bold ফাইল নেই, তাই FontWeight.Normal ব্যবহার করা হয়েছে — নইলে Android
 * নিজে থেকে কৃত্রিম বোল্ড বসিয়ে অক্ষর বিকৃত করত।
 */
private val AlinurFontFamily = FontFamily(
    Font(R.font.alinur_banglaborno, FontWeight.Normal)
)

/**
 * KPI কার্ড হেডারের নিচের কিনারার উপর যতটা নামে/ওভারল্যাপ করে (dp)। হেডারের নিচের প্যাডিং
 * এর চেয়ে বেশি রাখা হয়েছে যাতে টাইটেল/সাবটাইটেল কখনো কার্ডের নিচে ঢাকা না পড়ে।
 * Home ও Dashboard দুই স্ক্রিনই এই একই মান ব্যবহার করে।
 */
val HeaderCardOverlap = 64.dp
val HeaderBottomPadding = HeaderCardOverlap + 22.dp

/**
 * স্টোরের লোগো (Fabric লোগো, SVG viewBox 256x195) — ImageVector আকারে।
 * এটি মাল্টি-কালার (হলুদ #FEC32D ও নীল #2C6DB0), তাই ব্যবহারের সময় Icon() এর tint দেওয়া যাবে না।
 * ব্যবহার করতে হবে Image() দিয়ে।
 */
private val StoreLogoIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "StoreLogo",
        defaultWidth = 34.dp,
        defaultHeight = 26.dp,
        viewportWidth = 256f,
        viewportHeight = 195f
    ).apply {
        path(fill = SolidColor(Color(0xFFFEC32D))) {
            moveTo(195.575f, 178.073f)
            curveTo(194.285f, 183.607f, 193.622f, 186.977f, 190.866f, 190.696f)
            curveTo(189.03f, 193.176f, 186.021f, 194.6f, 182.956f, 194.241f)
            curveTo(180.293f, 193.931f, 177.714f, 192.789f, 175.121f, 191.791f)
            curveTo(145.135f, 180.25f, 120.936f, 159.929f, 96.487f, 139.982f)
            curveTo(90.66f, 135.228f, 78.363f, 124.221f, 71.379f, 118.438f)
            curveTo(88.382f, 106.418f, 95.42f, 98.984f, 95.42f, 98.984f)
            curveTo(122.818f, 122.77f, 154.761f, 150.616f, 188.098f, 164.544f)
            curveTo(193.41f, 166.764f, 198.381f, 166.044f, 195.575f, 178.074f)
            moveTo(161.69f, 88.674f)
            curveTo(153.977f, 82.578f, 140.486f, 72.459f, 132.87f, 66.584f)
            curveTo(113.517f, 51.655f, 92.876f, 39.196f, 69.198f, 32.097f)
            curveTo(56.373f, 28.252f, 54.688f, 15.361f, 64.384f, 3.974f)
            curveTo(66.147f, 1.904f, 68.902f, 0.546f, 71.564f, 1.103f)
            curveTo(115.664f, 10.336f, 149.038f, 37.358f, 186.202f, 71.463f)
            curveTo(173.628f, 78.106f, 161.689f, 88.673f, 161.689f, 88.673f)
        }
        path(fill = SolidColor(Color(0xFF2C6DB0))) {
            moveTo(135.123f, 120.68f)
            curveTo(162.66f, 92.81f, 216.125f, 59.134f, 243.087f, 57.896f)
            arcTo(8.701f, 8.701f, 0f, false, true, 250.888f, 62.013f)
            curveTo(253.544f, 66.321f, 254.402f, 71.479f, 255.195f, 76.551f)
            curveTo(255.961f, 81.451f, 253.406f, 85.634f, 248.758f, 86.936f)
            curveTo(216.546f, 95.959f, 185.262f, 117.896f, 159.87f, 138.841f)
            curveTo(151.329f, 134.807f, 135.123f, 120.681f, 135.123f, 120.681f)
            moveTo(120.093f, 67.906f)
            curveTo(88.46f, 94.319f, 54.137f, 125.83f, 17.03f, 137.113f)
            curveTo(13.898f, 138.066f, 10.44f, 137.28f, 8.186f, 134.904f)
            curveTo(5.155f, 131.708f, 3.994f, 128.418f, 2.766f, 125.217f)
            curveTo(-1.528f, 114.032f, -0.311f, 110.76f, 10.804f, 106.483f)
            curveTo(36.894f, 96.44f, 58.602f, 79.75f, 79.605f, 61.91f)
            curveTo(82.785f, 59.209f, 89.094f, 54.022f, 92.573f, 51.161f)
            curveTo(104.079f, 54.245f, 120.093f, 67.906f, 120.093f, 67.906f)
            moveTo(63.104f, 165.362f)
            curveTo(72.175f, 160.713f, 86.141f, 155.227f, 95.244f, 150.112f)
            curveTo(102.599f, 156.596f, 119.627f, 169.514f, 119.627f, 169.514f)
            curveTo(107.28f, 177.768f, 87.587f, 190.057f, 73.878f, 193.602f)
            curveTo(70.564f, 194.459f, 67.045f, 193.169f, 64.951f, 190.461f)
            curveTo(60.943f, 185.274f, 58.625f, 179.768f, 58.426f, 173.215f)
            curveTo(58.326f, 169.928f, 60.178f, 166.861f, 63.104f, 165.362f)
            moveTo(136.655f, 20.665f)
            curveTo(152.867f, 10.86f, 171.108f, 3.496f, 183.607f, 0.822f)
            curveTo(187.044f, 0.087f, 190.637f, 1.588f, 192.523f, 4.554f)
            curveTo(196.11f, 10.194f, 197.601f, 15.86f, 197.235f, 22.059f)
            curveTo(197.055f, 25.117f, 195.359f, 27.892f, 192.671f, 29.359f)
            curveTo(184.83f, 33.642f, 170.864f, 36.981f, 162.837f, 40.484f)
            curveTo(162.837f, 40.484f, 148.682f, 28.544f, 136.655f, 20.665f)
        }
        path(fill = SolidColor(Color(0xFFFEC32D))) {
            moveTo(233.097f, 101.673f)
            curveTo(254.992f, 110.177f, 263.068f, 113.06f, 247.095f, 135.124f)
            curveTo(245.222f, 137.71f, 242.247f, 137.727f, 239.139f, 136.993f)
            curveTo(229.177f, 134.64f, 211.499f, 124.453f, 201.713f, 118.761f)
            curveTo(219.751f, 106.283f, 233.097f, 101.673f, 233.097f, 101.673f)
            moveTo(25.672f, 91.788f)
            curveTo(20.265f, 90.242f, 9.978f, 88.611f, 5.267f, 85.742f)
            curveTo(3.137f, 84.445f, 1.705f, 82.187f, 1.4f, 79.712f)
            curveTo(0.626f, 73.456f, 2.232f, 67.822f, 5.283f, 62.152f)
            arcTo(8.672f, 8.672f, 0f, false, true, 13.851f, 57.65f)
            curveTo(24.35f, 58.816f, 43.324f, 66.75f, 53.596f, 71.447f)
            curveTo(38.72f, 85.145f, 25.672f, 91.788f, 25.672f, 91.788f)
        }
    }.build()
}

/**
 * ওয়েব অ্যাপের <header class="hdr"> → <div class="hdr-topbar"> এর সমতুল্য।
 * অ্যাপ টাইটেল "ইমরান ক্লথ স্টোর" + সাবটাইটেল, ডানে নোটিফিকেশন ও মেনু বাটন।
 */
@Composable
fun AppTopBar(
    onNotifClick: () -> Unit,
    onMenuClick: () -> Unit,
    notifBadgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color(0xFF0B4A4E), AppColors.HeaderTeal)
                ),
                androidx.compose.foundation.shape.RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
            )
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = HeaderBottomPadding),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = StoreLogoIcon,
                contentDescription = null,
                modifier = Modifier.size(width = 40.dp, height = 30.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "ইমরান ক্লথ স্টোর",
                    color = Color.White,
                    fontSize = 19.sp,
                    fontFamily = AlinurFontFamily,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "পাওনা লিস্ট, ক্যাশফ্লো, প্রফিট-লস",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                IconButton(onClick = onNotifClick) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = "নোটিফিকেশন",
                        tint = Color.White
                    )
                }
                if (notifBadgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp, end = 10.dp)
                            .size(10.dp)
                            .background(Color(0xFFE53935), CircleShape)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "মেনু",
                    tint = Color.White
                )
            }
        }
    }
}
