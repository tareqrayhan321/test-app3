package com.imran.clothstore.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের .home-card / .card-body এর সমতুল্য কম্পোনেন্ট।
 * প্রতিটা হোম কার্ডে একটা SVG-জাতীয় আর্ট (Canvas দিয়ে আঁকা), টাইটেল, স্ট্যাট লাইন,
 * (ডান কোণার ">" অ্যারো সরিয়ে ফেলা হয়েছে)।
 *
 * @param art একটা @Composable lambda — কার্ডের উপরের আইকন/ইলাস্ট্রেশন আঁকার জন্য
 */
@Composable
fun HomeCard(
    title: String,
    statText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    art: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.95f)
            .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, AppColors.CardBorder), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                art()
            }
            Text(
                text = title,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = statText,
                fontSize = 10.5.sp,
                lineHeight = 15.sp,
                color = Color(0xFF3D3B36),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

/**
 * ছোট আর্ট + শিরোনাম কার্ড — কাপড় ক্রয়/P&L Trends/সাপ্তাহিক রিপোর্ট শর্টকাটের জন্য।
 * ওয়েব অ্যাপের .home-card.kpi-mini-card এর সমতুল্য। Home ও Dashboard দুই স্ক্রিনেই
 * অভিন্ন থাকার জন্য এখানে shared component হিসেবে রাখা হলো (আগে HomeScreen.kt-এ
 * private ছিল)।
 */
@Composable
fun MiniArtCard(
    title: String,
    onClick: () -> Unit,
    art: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    labelColor: Color = AppColors.TextPrimary,
    subtitle: String? = null,
    cardHeight: androidx.compose.ui.unit.Dp = 140.dp
) {
    Box(
        modifier = modifier
            .height(cardHeight)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(BorderStroke(1.dp, AppColors.CardBorder), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) { art() }
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = labelColor,
                modifier = Modifier.padding(top = 8.dp)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    maxLines = 3,
                    lineHeight = 12.sp,
                    color = Color(0xFF3D3B36),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}
