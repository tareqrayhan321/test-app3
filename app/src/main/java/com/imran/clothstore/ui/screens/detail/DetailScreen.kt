package com.imran.clothstore.ui.screens.detail

import androidx.compose.foundation.background
import com.imran.clothstore.util.formatTaka
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের fvDetailScreen এর সরাসরি Kotlin/Compose সংস্করণ।
 * হেডার (বড় অ্যাভাটার + নাম + ঠিকানা/মোবাইল) → Excel টেবিল → নতুন বকেয়া/জমা ফর্ম।
 * হেডারে ব্যাক আইকন নেই — ফোনের সিস্টেম ব্যাক জেসচার/বাটন দিয়ে ফেরা যায় (BackHandler)।
 */
@Composable
fun DetailScreen(
    category: EntryCategory,
    onBack: () -> Unit,
    viewModel: DetailViewModel
) {
    BackHandler(onBack = onBack)
    val entry by viewModel.entry.collectAsState()
    val rows by viewModel.transactionRows.collectAsState()

    var formKind by remember { mutableStateOf<String?>(null) } // "bokeyoa" | "joma" | null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── হেডার (ক্রিম, রেফারেন্স অনুযায়ী) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD9CCA6)),
                contentAlignment = Alignment.Center
            ) {
                if (entry.photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = entry.photoUrl,
                        contentDescription = "${entry.name} এর ছবি",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.size(64.dp).clip(CircleShape)
                    )
                } else {
                    Text(
                        text = entry.name.trim().take(1).ifEmpty { "?" },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = entry.name.ifBlank { "—" },
                    color = AppColors.TextPrimary,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                val metaParts = listOf(entry.addr, entry.mob).filter { it.isNotBlank() }
                if (metaParts.isNotEmpty()) {
                    Text(
                        text = metaParts.joinToString(" · "),
                        color = Color(0xFF4A4740),
                        fontSize = 15.sp
                    )
                }
            }
        }
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE2D9C4))
        )

        // ── টেবিল (স্ক্রলযোগ্য) ──
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            TransactionTable(rows = rows)
        }

        // ── স্টিকি ফুটার: বর্তমান পাওনা/প্রাপ্য ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (category.isCustomerType) "বর্তমান পাওনা/প্রাপ্য" else "বর্তমান বকেয়া",
                fontSize = 16.sp,
                color = AppColors.TextPrimary
            )
            Text(
                text = formatTaka(entry.baki),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        // ── দুই বড় বাটন: জমা এন্ট্রি (সবুজ) | পাওনা এন্ট্রি (লাল) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(horizontal = 12.dp)
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { formKind = "joma" },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F9D55))
            ) {
                Text("💰 জমা এন্ট্রি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, softWrap = false)
            }
            Button(
                onClick = { formKind = "bokeyoa" },
                modifier = Modifier.weight(1f).height(42.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text(
                    if (category.isCustomerType) "📝 পাওনা এন্ট্রি" else "📝 বকেয়া এন্ট্রি",
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, softWrap = false
                )
            }
        }
    }

    formKind?.let { kind ->
        TransactionFormSheet(
            kind = kind,
            isCustomerType = category.isCustomerType,
            onDismiss = { formKind = null },
            onAddBokeyoa = { memo, date, goj, bill, note, joma ->
                viewModel.addBokeyoa(memo, date, goj, bill, note, joma)
            },
            onAddJoma = { memo, date, joma, note ->
                viewModel.addJoma(memo, date, joma, note)
            }
        )
    }
}
