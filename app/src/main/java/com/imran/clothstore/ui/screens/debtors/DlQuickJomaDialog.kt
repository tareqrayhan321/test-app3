package com.imran.clothstore.ui.screens.debtors

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
import com.imran.clothstore.ui.components.AppDateField
import com.imran.clothstore.ui.components.AppTextField
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
import androidx.compose.ui.window.Dialog

/**
 * ওয়েব অ্যাপের dlJomaOverlay এর সমতুল্য — Debtors List থেকে এক ট্যাপে দ্রুত জমা যোগ করার ডায়ালগ।
 * সবুজ গ্রেডিয়েন্ট হেডার (মূল অ্যাপে #1a3828 → #224a34) রঙের ইঙ্গিত হিসেবে রাখা হলো।
 */
@Composable
fun DlQuickJomaDialog(
    entryName: String,
    onDismiss: () -> Unit,
    onSave: (amount: Double, date: String, note: String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp))
        ) {
            // হেডার — সবুজ গ্রেডিয়েন্টের বদলে সলিড টোন (Compose-এ সহজ gradient ব্যবহার করা যায়, তবে এখানে flat রাখা হলো)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A3828))
                    .padding(16.dp)
            ) {
                Text(
                    text = "জমা এন্ট্রি",
                    color = Color(0xFFE0B84A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = entryName,
                    color = Color(0xFFA09070),
                    fontSize = 10.sp
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                AppDateField(date, { date = it }, "তারিখ", Modifier.fillMaxWidth())
                AppTextField(
                    amount, { amount = it }, "জমার পরিমাণ",
                    Modifier.fillMaxWidth().padding(top = 10.dp), KeyboardType.Decimal
                )
                AppTextField(
                    note, { note = it }, "মন্তব্য",
                    Modifier.fillMaxWidth().padding(top = 10.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("বাতিল")
                    }
                    Button(
                        onClick = { onSave(amount.toDoubleOrNull() ?: 0.0, date, note) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0B84A))
                    ) {
                        Text("সেভ করুন", color = Color(0xFF1A0F06))
                    }
                }
            }
        }
    }
}
