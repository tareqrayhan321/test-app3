package com.imran.clothstore.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    androidx.compose.material3.ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFF0E9DA)
    ) {
        Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 24.dp)) {
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
private fun BokeyoaMiniForm(
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
            .padding(top = 10.dp)
            .background(Color(0xFFF7F5EE), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(memo, { memo = it }, label = { Text("ম্যামো") }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(date, { date = it }, label = { Text("তারিখ") }, modifier = Modifier.weight(1f), singleLine = true)
        }
        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                bill, { bill = it }, label = { Text("বিল/বকেয়া") },
                modifier = Modifier.weight(1f), singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(goj, { goj = it }, label = { Text("গজ") }, modifier = Modifier.weight(1f), singleLine = true)
        }
        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                joma, { joma = it }, label = { Text("সাথে জমা (ঐচ্ছিক)") },
                modifier = Modifier.weight(1f), singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(note, { note = it }, label = { Text("মন্তব্য") }, modifier = Modifier.weight(1f), singleLine = true)
        }
        Button(
            onClick = { onSave(memo, date, goj, bill.toDoubleOrNull() ?: 0.0, note, joma.toDoubleOrNull() ?: 0.0) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
        ) {
            Text("সেভ করুন")
        }
    }
}

@Composable
private fun JomaMiniForm(
    onSave: (memo: String, date: String, joma: Double, note: String) -> Unit
) {
    var memo by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var joma by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)
            .background(Color(0xFFF7F5EE), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(memo, { memo = it }, label = { Text("ম্যামো") }, modifier = Modifier.weight(1f), singleLine = true)
            OutlinedTextField(date, { date = it }, label = { Text("তারিখ") }, modifier = Modifier.weight(1f), singleLine = true)
        }
        Row(modifier = Modifier.padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                joma, { joma = it }, label = { Text("জমা") },
                modifier = Modifier.weight(1f), singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(note, { note = it }, label = { Text("মন্তব্য") }, modifier = Modifier.weight(1f), singleLine = true)
        }
        Button(
            onClick = { onSave(memo, date, joma.toDoubleOrNull() ?: 0.0, note) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
        ) {
            Text("সেভ করুন")
        }
    }
}
