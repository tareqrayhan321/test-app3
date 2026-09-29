package com.imran.clothstore.ui.screens.paikkari

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ওয়েব অ্যাপের .frow (label + input + unit) এর সমতুল্য একটা ইনপুট রো।
 * value/onValueChange স্ট্রিং-ভিত্তিক রাখা হয়েছে যাতে ব্যবহারকারী মাঝপথে টাইপ করার সময়
 * "0.0" জাতীয় জোরপূর্বক ফরম্যাটিং না ঘটে।
 */
@Composable
fun PkInputRow(
    label: String,
    unit: String,
    value: String,
    onValueChange: (String) -> Unit,
    isDecimal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.5.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(110.dp)
        )
        Row(
            modifier = Modifier.width(160.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedTextField(
                shape = androidx.compose.foundation.shape.CircleShape,
                value = value,
                onValueChange = { new -> if (new.all { it.isDigit() || it == '.' }) onValueChange(new) },
                placeholder = { Text("0") },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
                ),
                modifier = Modifier.width(100.dp)
            )
            Text(unit, fontSize = 10.5.sp, color = Color(0xFF9A96AD))
        }
    }
}

/** স্বয়ংক্রিয়ভাবে হিসাবকৃত (read-only) মান দেখানোর রো — .fauto এর সমতুল্য */
@Composable
fun PkAutoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.5.sp, color = Color(0xFF6C6A64), modifier = Modifier.width(150.dp))
        Text(value, fontSize = 13.5.sp, color = Color(0xFF2BB673), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
    }
}

/** স্ট্রিং ইনপুটকে Double এ কনভার্ট করার হেল্পার — খালি বা অবৈধ হলে 0.0 */
fun String.toInputDouble(): Double = this.toDoubleOrNull() ?: 0.0
