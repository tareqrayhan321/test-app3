package com.imran.clothstore.ui.screens.paikkari

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের "সাপ্তাহিক তথ্য হালনাগাদ" (Dashboard-এর প্রোমোশনাল ব্যানার থেকে খোলা) বটম-শিট।
 * রেফারেন্স স্ক্রিনশট অনুযায়ী: উপরে drag handle, বাম দিকে টাইটেল "সাপ্তাহিক তথ্য হালনাগাদ",
 * ডান দিকে ✕ বন্ধ বাটন, নিচে ফর্ম (FIXED COSTS → MAINTENANCE COSTS → ...) স্ক্রলযোগ্য।
 * পুরনো ধাপ-ভিত্তিক ফর্ম (PkStep1Fixed/PkStep2Stock/PkStep3Sale) এবং calculate() লজিক অপরিবর্তিত —
 * শুধু প্রেজেন্টেশন ফুল-স্ক্রিন টিল-হেডার থেকে বটম-শিটে বদলানো হলো।
 * ফলাফল (RESULT) পেইজ ফুল-স্ক্রিনেই থাকে, যেহেতু রেফারেন্সে শুধু ইনপুট ফর্মের বটম-শিট দেখা গেছে।
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaikkariCalculatorScreen(
    onBack: () -> Unit,
    viewModel: PaikkariViewModel = viewModel()
) {
    val input by viewModel.input.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val result by viewModel.result.collectAsState()
    val validationError by viewModel.validationError.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val savedMessage by viewModel.savedMessage.collectAsState()

    BackHandler {
        if (currentPage == PkPage.RESULT) viewModel.goToCalcPage() else onBack()
    }

    if (currentPage == PkPage.CALC) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = onBack,
            sheetState = sheetState,
            containerColor = Color.White,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 4.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFD9D5C7))
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // ── শিট হেডার: টাইটেল + ✕ ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "সাপ্তাহিক তথ্য হালনাগাদ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "বন্ধ করুন", tint = Color(0xFF6C6A64))
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    validationError?.let { err ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                                .background(Color(0xFFFBEAE7), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Text(err.message, color = Color(0xFFC0392B), fontSize = 12.sp)
                        }
                    }

                    PkStep1Fixed(input = input, onUpdate = viewModel::updateInput)
                    PkStep2Stock(input = input, onUpdate = viewModel::updateInput)
                    PkStep3Sale(input = input, onUpdate = viewModel::updateInput)

                    Button(
                        onClick = { viewModel.calculate() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
                    ) {
                        Text("হিসাব করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        // ── ফলাফল পেইজ — ফুল-স্ক্রিন (টিল হেডারসহ) ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.HeaderTeal)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("সাপ্তাহিক তথ্য হালনাগাদ", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("হিসাবের ফলাফল", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                }
            }

            result?.let { r ->
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    PaikkariResultPage(
                        result = r,
                        weekLabel = currentWeekLabel(),
                        onNewCalculation = { viewModel.resetAll() },
                        onDone = onBack
                    )
                    if (isSaving) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                            Text("সংরক্ষণ করা হচ্ছে…", fontSize = 12.sp, color = Color(0xFF6C6A64))
                        }
                    }
                    savedMessage?.let { msg ->
                        Text(
                            text = msg,
                            fontSize = 12.sp,
                            color = Color(0xFF2BB673),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
