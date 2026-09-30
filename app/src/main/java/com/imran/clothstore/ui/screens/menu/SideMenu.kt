package com.imran.clothstore.ui.screens.menu

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের side-menu (sideMenu/menuOverlay) এর Kotlin/Compose সংস্করণ।
 * মূল ওয়েব অ্যাপে side-menu-body সম্পূর্ণ খালি ছিল (ভবিষ্যতের সম্প্রসারণের জন্য রাখা),
 * তাই এখানে কাঠামো অভিন্ন রেখে ব্যবহারিক শর্টকাট (হোম, পার্টি লেজার, ডাটাবেইজ) রাখা হয়েছে।
 */
@Composable
fun SideMenu(
    visible: Boolean,
    onClose: () -> Unit,
    onNavigateHome: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // ── পেছনের আবছা পর্দা — চাপ দিলে ড্রয়ার বন্ধ ──
        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClose
                    )
            )
        }

        // ── ড্রয়ার — স্ক্রিনের অর্ধেক প্রস্থ, বাম দিক থেকে স্লাইড ──
        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            // ড্রয়ার খোলা থাকলে সিস্টেম ব্যাক দিয়ে আগে ড্রয়ারই বন্ধ হবে
            BackHandler(onBack = onClose)

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .background(AppColors.HeaderTeal)
                    // ড্রয়ারের ভেতরের ফাঁকা জায়গায় চাপ যেন নিচের পর্দায় না পৌঁছে বন্ধ করে না দেয়
                    .pointerInput(Unit) { detectTapGestures { } }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color(0xFF0B4A4E), AppColors.HeaderTeal)
                            )
                        )
                        .statusBarsPadding()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 22.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("মেনু", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(Color.White)
                        .padding(12.dp)
                ) {
                    MenuItem(Icons.Filled.Home, "হোম", onNavigateHome)
                }
            }
        }
    }
}

@Composable
private fun MenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF2A2418),
            modifier = Modifier.size(20.dp)
        )
        Text(label, fontSize = 14.sp, modifier = Modifier.padding(start = 12.dp), color = Color(0xFF2A2418))
    }
}
