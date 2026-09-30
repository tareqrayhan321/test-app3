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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
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
import com.imran.clothstore.data.model.Entry
import com.imran.clothstore.data.model.EntryCategory
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের fvScreen (ফুল স্ক্রিন লিস্ট ভিউ) এর সরাসরি Kotlin/Compose সংস্করণ।
 * হেডার (আইকন + টাইটেল + সাবটাইটেল + নতুন এন্ট্রি বাটন) → সার্চ → লিস্ট বডি।
 * হেডারে ব্যাক আইকন নেই — ফোনের সিস্টেম ব্যাক জেসচার/বাটন দিয়ে ফেরা যায় (BackHandler)।
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
    var pendingDelete by remember { mutableStateOf<Entry?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.HeaderTeal)
    ) {
        // ── হেডার (মেইন হেডারের মতো টিল গ্রেডিয়েন্ট) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        listOf(Color(0xFF0B4A4E), AppColors.HeaderTeal)
                    )
                )
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 14.dp)
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
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(
                            text = category.titleBn,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (totalCount > 0)
                                "${totalCount.toBengaliDigits()} জন · মোট বাকি ${formatTaka(totalBaki)}"
                            else "কোনো এন্ট্রি নেই",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // সার্চ বক্স — কম হাইট (42dp), চারকোনা-রাউন্ডেড (12dp)
            val searchInteraction = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            androidx.compose.foundation.text.BasicTextField(
                value = query,
                onValueChange = viewModel::onSearchChange,
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = Color(0xFF141413)),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(AppColors.HeaderTeal),
                interactionSource = searchInteraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .height(42.dp),
                decorationBox = { innerTextField ->
                    androidx.compose.material3.OutlinedTextFieldDefaults.DecorationBox(
                        value = query,
                        innerTextField = innerTextField,
                        enabled = true,
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
                        interactionSource = searchInteraction,
                        placeholder = { Text("নাম বা ঠিকানা খুঁজুন...", color = Color(0xFF8E8B82), fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = Color(0xFF6C6A64), modifier = Modifier.size(20.dp)) },
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color(0xFFD9D2C0),
                            unfocusedBorderColor = Color(0xFFE2D9C4)
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        container = {
                            androidx.compose.material3.OutlinedTextFieldDefaults.ContainerBox(
                                enabled = true,
                                isError = false,
                                interactionSource = searchInteraction,
                                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = Color(0xFFD9D2C0),
                                    unfocusedBorderColor = Color(0xFFE2D9C4)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    )
                }
            )
        }

        // ── লিস্ট বডি (উপরে গোলাকার কোণ) ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color.White)
        ) {
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (query.isNotBlank()) "কোনো ফলাফল নেই" else "কোনো এন্ট্রি নেই",
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
                    itemsIndexed(entries, key = { index, it -> "${it.id}_$index" }) { _, entry ->
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
                            onDelete = { pendingDelete = entry }
                        )
                    }
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

    // ── ডিলিটের আগে সিক্রেট কী ──
    pendingDelete?.let { target ->
        com.imran.clothstore.ui.components.SecretKeyDialog(
            message = "\"${target.name.ifBlank { "এই এন্ট্রি" }}\" ডিলিট করতে সিক্রেট কী দিন। ডিলিট করলে এর সব লেনদেনও মুছে যাবে।",
            confirmLabel = "ডিলিট করুন",
            onConfirm = {
                viewModel.deleteEntry(target)
                pendingDelete = null
            },
            onDismiss = {
                pendingDelete = null
                viewModel.selectForAction(null)
            }
        )
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
