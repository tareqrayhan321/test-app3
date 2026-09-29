package com.imran.clothstore.ui.screens.fvlist

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import com.imran.clothstore.util.formatTaka
import com.imran.clothstore.util.toBengaliDigits
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import com.imran.clothstore.data.model.Entry
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের fvScreen (ফুল স্ক্রিন লিস্ট ভিউ) এর সরাসরি Kotlin/Compose সংস্করণ।
 * হেডার (আইকন + টাইটেল + সাবটাইটেল + নতুন এন্ট্রি বাটন) → সার্চ → লিস্ট বডি।
 * হেডারে ব্যাক আইকন নেই — ফোনের সিস্টেম ব্যাক জেসচার/বাটন দিয়ে ফেরা যায় (BackHandler)।
 */
@Composable
fun FvListScreen(
    category: EntryCategory,
    onBack: () -> Unit,
    onEntryClick: (Entry) -> Unit,
    viewModel: FvListViewModel
) {
    BackHandler(onBack = onBack)
    val entries by viewModel.filteredEntries.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val totalBaki by viewModel.totalBaki.collectAsState()
    val selectedForAction by viewModel.selectedForAction.collectAsState()
    val isFormOpen by viewModel.isFormOpen.collectAsState()
    val editingEntry by viewModel.editingEntry.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF9F5))
    ) {
        // ── হেডার (ক্রিম, রেফারেন্স অনুযায়ী) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F1E8))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = Color(0xFF4A4740),
                        modifier = Modifier.size(30.dp)
                    )
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = category.titleBn,
                            color = AppColors.TextPrimary,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (totalCount > 0)
                                "${totalCount.toBengaliDigits()} জন · মোট বাকি ${formatTaka(totalBaki)}"
                            else "কোনো এন্ট্রি নেই",
                            color = Color(0xFF4A4740),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onSearchChange,
                placeholder = { Text("নাম বা ঠিকানা খুঁজুন...", color = Color(0xFF8E8B82)) },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF6C6A64)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFAF9F5),
                    unfocusedContainerColor = Color(0xFFFAF9F5),
                    focusedBorderColor = Color(0xFFD9D2C0),
                    unfocusedBorderColor = Color(0xFFE2D9C4)
                )
            )
        }

        // ── লিস্ট বডি ──
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (query.isNotBlank()) "🔍 কোনো ফলাফল নেই" else "🎉 কোনো এন্ট্রি নেই",
                    fontSize = 14.sp,
                    color = Color(0xFF9A96AD)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    EntryRowCard(
                        entry = entry,
                        isSelected = selectedForAction?.id == entry.id,
                        onClick = {
                            if (selectedForAction != null) {
                                viewModel.selectForAction(null)
                            } else {
                                onEntryClick(entry)
                            }
                        },
                        onLongPress = { viewModel.selectForAction(entry) },
                        onEdit = { viewModel.openEditForm(entry) },
                        onDelete = { viewModel.deleteEntry(entry) }
                    )
                }
            }
        }
    }

    // ── নতুন এন্ট্রি (নিচে ডান কোণে ছোট FAB) ──
    FloatingActionButton(
        onClick = { viewModel.openNewEntryForm() },
        shape = CircleShape,
        containerColor = AppColors.HeaderTeal,
        contentColor = Color.White,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .navigationBarsPadding()
            .padding(end = 28.dp, bottom = 36.dp)
            .size(56.dp)
    ) {
        Icon(person_add, contentDescription = "নতুন এন্ট্রি", modifier = Modifier.size(24.dp))
    }
    }

    // ── এন্ট্রি ফর্ম ডায়ালগ ──
    if (isFormOpen) {
        EntryFormDialog(
            isCustomerType = category.isCustomerType,
            existing = editingEntry,
            onDismiss = { viewModel.closeForm() },
            onSave = { name, addr, mob, memo, billOrBaki, joma, goj, note, date, photoUrl ->
                viewModel.saveEntry(name, addr, mob, memo, billOrBaki, joma, goj, note, date, photoUrl)
            }
        )
    }
}
