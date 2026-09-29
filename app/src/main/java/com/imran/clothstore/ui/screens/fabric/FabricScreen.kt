package com.imran.clothstore.ui.screens.fabric

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের fabricOverlay (কাপড় ক্রয় তালিকা) এর সরাসরি Kotlin/Compose সংস্করণ।
 * হেডার + "নতুন এন্ট্রি" টগল ফর্ম + সার্চ → গ্রুপ কার্ড লিস্ট, প্রতিটাতে DCA (আরো কিনেছি) ফর্ম।
 * হেডারে ব্যাক আইকন নেই — ফোনের সিস্টেম ব্যাক জেসচার/বাটন দিয়ে ফেরা যায় (BackHandler)।
 */
@Composable
fun FabricScreen(
    onBack: () -> Unit,
    viewModel: FabricViewModel = viewModel()
) {
    BackHandler(onBack = onBack)
    val groups by viewModel.filteredGroups.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val summaryText by viewModel.cardSummaryText.collectAsState()
    var showNewForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F5))
    ) {
        // ── হেডার ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AppColors.HeaderTeal)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("কাপড় ক্রয়", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text(summaryText, color = Color.White.copy(alpha = 0.75f), fontSize = 11.5.sp)
                }
                IconButton(onClick = { showNewForm = !showNewForm }) {
                    Icon(Icons.Filled.Add, contentDescription = "নতুন এন্ট্রি", tint = Color.White)
                }
            }

            if (groups.isNotEmpty()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = viewModel::onSearchChange,
                    placeholder = { Text("কাপড়ের নাম খুঁজুন...") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }
        }

        if (showNewForm) {
            Box(modifier = Modifier.padding(14.dp)) {
                FabricNewPurchaseForm(
                    onSave = { name, qty, unit, rate, onError ->
                        viewModel.saveNewPurchase(name, qty, unit, rate, onError)
                        if (name.isNotBlank() && qty > 0 && rate > 0) showNewForm = false
                    }
                )
            }
        }

        // ── বডি ──
        if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "এখনো কোনো কাপড় ক্রয় যোগ করা হয়নি",
                        fontSize = 13.sp,
                        color = Color(0xFF9A96AD),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)
            ) {
                items(groups, key = { it.id }) { group ->
                    FabricGroupCard(
                        group = group,
                        onAddMore = { qty, rate, onError ->
                            viewModel.addPurchaseToGroup(group, qty, rate, onError)
                        },
                        onDeletePurchase = { purchase ->
                            viewModel.deletePurchase(group, purchase)
                        }
                    )
                }
            }
        }
    }
}
