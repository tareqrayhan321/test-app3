package com.imran.clothstore.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.components.AppDateField
import com.imran.clothstore.ui.components.AppTextField
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের dmf-bokeyoa-/dmf-joma- মিনি ফর্মের সমতুল্য — এখন Detail স্ক্রিনের নিচের
 * "💰 জমা এন্ট্রি" / "📝 পাওনা এন্ট্রি" বাটন থেকে বটম-শিটে খোলে (আগে ইনলাইন টগল ছিল)।
 * ফর্ম-শিটের নিজস্ব রেফারেন্স স্ক্রিনশট পাওয়া যায়নি; স্টাইল অন্য ফর্মের (ক্রিম প্যানেল) সাথে মেলানো।
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormSheet(
    kind: String, // "bokeyoa" | "joma"
    isCustomerType: Boolean,
    onDismiss: () -> Unit,
    onAddBokeyoa: (memo: String, date: String, goj: String, bill: Double, note: String, joma: Double) -> Unit,
    onAddJoma: (memo: String, date: String, joma: Double, note: String) -> Unit
) {
    // পুরো উচ্চতায় খোলে (আংশিক অবস্থায় আটকে থাকে না), আর কিবোর্ড উঠলে ফর্ম কিবোর্ডের উপরে থাকে।
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = if (kind == "joma") "💰 জমা এন্ট্রি" else if (isCustomerType) "📝 পাওনা এন্ট্রি" else "📝 বকেয়া এন্ট্রি",
                fontSize = 17.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
            if (kind == "joma") {
                JomaMiniForm(onSave = { memo, date, joma, note ->
                    onAddJoma(memo, date, joma, note)
                    onDismiss()
                })
            } else {
                BokeyoaMiniForm(onSave = { memo, date, goj, bill, note, joma ->
                    onAddBokeyoa(memo, date, goj, bill, note, joma)
                    onDismiss()
                })
            }
        }
    }
}

@Composable
private fun ColumnScope.BokeyoaMiniForm(
    onSave: (memo: String, date: String, goj: String, bill: Double, note: String, joma: Double) -> Unit
) {
    var memo by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var goj by remember { mutableStateOf("") }
    var bill by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var joma by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .padding(top = 10.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        // ফিল্ডগুলো স্ক্রলযোগ্য অংশে; সেভ বাটন এর বাইরে, নিচে আটকানো — কিবোর্ড উঠলেও দেখা যায়
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AppTextField(memo, { memo = it }, "ম্যামো", Modifier.weight(1f))
            AppDateField(date, { date = it }, "তারিখ", Modifier.weight(1f))
        }
        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AppTextField(bill, { bill = it }, "বিল/বকেয়া", Modifier.weight(1f), KeyboardType.Decimal)
            AppTextField(goj, { goj = it }, "গজ", Modifier.weight(1f))
        }
        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AppTextField(joma, { joma = it }, "সাথে জমা (ঐচ্ছিক)", Modifier.weight(1f), KeyboardType.Decimal)
            AppTextField(note, { note = it }, "মন্তব্য", Modifier.weight(1f))
        }
        }
        Button(
            onClick = { onSave(memo, date, goj, bill.toDoubleOrNull() ?: 0.0, note, joma.toDoubleOrNull() ?: 0.0) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
        ) {
            Text("সেভ করুন")
        }
    }
}

@Composable
private fun ColumnScope.JomaMiniForm(
    onSave: (memo: String, date: String, joma: Double, note: String) -> Unit
) {
    var memo by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var joma by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .padding(top = 10.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        // ফিল্ডগুলো স্ক্রলযোগ্য অংশে; সেভ বাটন এর বাইরে, নিচে আটকানো — কিবোর্ড উঠলেও দেখা যায়
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState())
        ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AppTextField(memo, { memo = it }, "ম্যামো", Modifier.weight(1f))
            AppDateField(date, { date = it }, "তারিখ", Modifier.weight(1f))
        }
        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            AppTextField(joma, { joma = it }, "জমা", Modifier.weight(1f), KeyboardType.Decimal)
            AppTextField(note, { note = it }, "মন্তব্য", Modifier.weight(1f))
        }
        }
        Button(
            onClick = { onSave(memo, date, joma.toDoubleOrNull() ?: 0.0, note) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
        ) {
            Text("সেভ করুন")
        }
    }
}
