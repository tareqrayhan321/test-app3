package com.imran.clothstore.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.components.AppDateField
import com.imran.clothstore.ui.components.AppTextField
import com.imran.clothstore.ui.theme.AppColors

/** 120.0 → "120", 120.5 → "120.5" — এডিট ফর্মে দেখানোর জন্য */
private fun numText(d: Double): String =
    if (d == 0.0) "" else if (d % 1.0 == 0.0) d.toLong().toString() else d.toString()

/**
 * লেনদেনের একটা রো এডিট করার বটম-শিট। BILL রো-তে: মেমো, তারিখ, পরিমাণ, গজ, সাথের জমা, মন্তব্য।
 * JOMA রো-তে: মেমো, তারিখ, জমা, মন্তব্য। সেভ চাপলে onSave চলে (সিক্রেট কী যাচাই ডিটেইল স্ক্রিনে হয়)।
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionSheet(
    row: TxnRow,
    isCustomerType: Boolean,
    onDismiss: () -> Unit,
    onSave: (memo: String, date: String, goj: String, amount: Double, note: String, joma: Double) -> Unit
) {
    val isJoma = row.type == TxnType.JOMA
    var memo by remember(row) { mutableStateOf(row.memo) }
    var date by remember(row) { mutableStateOf(row.date) }
    var goj by remember(row) { mutableStateOf(row.goj) }
    var amount by remember(row) { mutableStateOf(numText(row.amount)) }
    var joma by remember(row) { mutableStateOf(numText(row.mergedJomaAmount ?: 0.0)) }
    var note by remember(row) { mutableStateOf(row.note) }

    val amountValue = amount.toDoubleOrNull() ?: 0.0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = if (isJoma) "জমা এডিট" else if (isCustomerType) "পাওনা এডিট" else "বকেয়া এডিট",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(top = 10.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppDateField(date, { date = it }, "তারিখ", Modifier.weight(1f))
                        AppTextField(memo, { memo = it }, "ম্যামো", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AppTextField(
                            amount, { amount = it },
                            if (isJoma) "জমা" else if (isCustomerType) "পাওনা/বিল" else "বকেয়া",
                            Modifier.weight(1f), KeyboardType.Decimal
                        )
                        if (!isJoma) {
                            AppTextField(goj, { goj = it }, "গজ", Modifier.weight(1f), KeyboardType.Decimal)
                        } else {
                            AppTextField(note, { note = it }, "মন্তব্য", Modifier.weight(1f))
                        }
                    }
                    if (!isJoma) {
                        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AppTextField(joma, { joma = it }, "সাথে জমা (ঐচ্ছিক)", Modifier.weight(1f), KeyboardType.Decimal)
                            AppTextField(note, { note = it }, "মন্তব্য", Modifier.weight(1f))
                        }
                    }
                }
                Button(
                    onClick = {
                        onSave(
                            memo.trim(), date, goj.trim(), amountValue, note.trim(),
                            if (isJoma) 0.0 else (joma.toDoubleOrNull() ?: 0.0)
                        )
                    },
                    enabled = amountValue > 0,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
                ) {
                    Text("সেভ করুন")
                }
            }
        }
    }
}
