package com.imran.clothstore.ui.screens.debtors

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের dlScreen (Debtors List / Party Ledger) এর সরাসরি Kotlin/Compose সংস্করণ।
 * হেডার + ক্যাটাগরি ট্যাব + সার্চ → Excel-স্টাইল টেবিল → ফুটারে মোট পাওনা/বকেয়া।
 * হেডারে ব্যাক আইকন নেই — ফোনের সিস্টেম ব্যাক জেসচার/বাটন দিয়ে ফেরা যায় (BackHandler)।
 */
@Composable
fun DebtorsListScreen(
    onBack: () -> Unit,
    viewModel: DebtorsListViewModel = viewModel()
) {
    BackHandler(onBack = onBack)
    val rows by viewModel.filteredRows.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val totalPawona by viewModel.totalPawona.collectAsState()
    val totalBokea by viewModel.totalBokea.collectAsState()

    var jomaDialogRow by remember { mutableStateOf<DebtorRow?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── হেডার ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.HeaderTeal)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Party Ledger",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "সকল দেনাদার/পাওনাদারের সম্মিলিত তালিকা",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            }

            // ── ক্যাটাগরি ফিল্টার ট্যাব ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                viewModel.tabs.forEach { tab ->
                    val isActive = tab == activeTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isActive) Color(0xFFE0B84A) else Color.White.copy(alpha = 0.15f))
                            .clickable { viewModel.onTabChange(tab) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tab.labelBn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) Color(0xFF1A0F06) else Color.White
                        )
                    }
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("নাম বা ঠিকানা খুঁজুন…") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
        }

        // ── বডি: টেবিল ──
        if (rows.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("কোনো এন্ট্রি পাওয়া যায়নি", fontSize = 13.sp, color = Color(0xFF9A96AD))
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f, fill = true).fillMaxWidth()) {
                item {
                    // টেবিল হেডার
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF0EEE5))
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text("পার্টির নাম", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                        Text("পাওনা/বকেয়া", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(64.dp))
                        Text("ঠিকানা", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(90.dp))
                    }
                }
                items(rows, key = { "${it.category.name}_${it.entry.id}" }) { row ->
                    DlTableRow(row = row, onJomaClick = { jomaDialogRow = row })
                }
            }

            // ── ফুটার — মোট পাওনা / মোট বকেয়া ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0EEE5))
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("মোট পাওনা", fontSize = 10.sp, color = Color(0xFF6C6A64))
                    Text("৳${totalPawona.toLong()}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("মোট বকেয়া", fontSize = 10.sp, color = Color(0xFF6C6A64))
                    Text("৳${totalBokea.toLong()}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // ── দ্রুত জমা ডায়ালগ ──
    jomaDialogRow?.let { row ->
        DlQuickJomaDialog(
            entryName = row.entry.name,
            onDismiss = { jomaDialogRow = null },
            onSave = { amount, date, note ->
                viewModel.addQuickJoma(row, amount, date, note)
                jomaDialogRow = null
            }
        )
    }
}
