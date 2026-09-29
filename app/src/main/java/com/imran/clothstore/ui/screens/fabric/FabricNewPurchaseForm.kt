package com.imran.clothstore.ui.screens.fabric

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.imran.clothstore.ui.screens.paikkari.toInputDouble
import com.imran.clothstore.ui.screens.reports.formatTaka
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের fabricFormWrap (নতুন কাপড় ক্রয় ফর্ম) এর সমতুল্য।
 * নাম + পরিমাণ + একক + দর, নিচে লাইভ "মোট: ৳X" প্রিভিউ (fabricCalcPreview() এর সমতুল্য)।
 */
@Composable
fun FabricNewPurchaseForm(
    onSave: (name: String, qty: Double, unit: String, rate: Double, onError: (String) -> Unit) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("গজ") }
    var rate by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val total = qty.toInputDouble() * rate.toInputDouble()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        errorMsg?.let {
            Text(it, color = Color(0xFFC0392B), fontSize = 11.5.sp, modifier = Modifier.padding(bottom = 8.dp))
        }

        OutlinedTextField(
            shape = androidx.compose.foundation.shape.CircleShape,
            value = name,
            onValueChange = { name = it },
            label = { Text("কাপড়ের নাম") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                shape = androidx.compose.foundation.shape.CircleShape,
                value = qty,
                onValueChange = { qty = it },
                label = { Text("পরিমাণ") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                shape = androidx.compose.foundation.shape.CircleShape,
                value = unit,
                onValueChange = { unit = it },
                label = { Text("একক") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        OutlinedTextField(
            shape = androidx.compose.foundation.shape.CircleShape,
            value = rate,
            onValueChange = { rate = it },
            label = { Text("একক দর (৳)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        if (total > 0) {
            Text(
                text = "মোট: ${formatTaka(total)}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2BB673),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                onSave(name, qty.toInputDouble(), unit.ifBlank { "গজ" }, rate.toInputDouble()) { err -> errorMsg = err }
                if (name.isNotBlank() && qty.toInputDouble() > 0 && rate.toInputDouble() > 0) {
                    name = ""; qty = ""; rate = ""; errorMsg = null
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.HeaderTeal)
        ) {
            Text("সেভ করুন")
        }
    }
}
