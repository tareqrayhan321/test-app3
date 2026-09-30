package com.imran.clothstore.ui.screens.paikkari

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import com.imran.clothstore.ui.components.CompactTextField
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
    isDecimal: Boolean = false,
    last: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
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
                modifier = Modifier.width(186.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CompactTextField(
                    value = value,
                    onValueChange = { new -> if (new.all { it.isDigit() || it == '.' }) onValueChange(new) },
                    placeholder = "0",
                    fieldHeight = 38.dp,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    fontSize = 13.sp,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = if (isDecimal) KeyboardType.Decimal else KeyboardType.Number
                    ),
                    modifier = Modifier.width(126.dp)
                )
                Text(unit, fontSize = 10.5.sp, color = Color(0xFF9A96AD))
            }
        }
        if (!last) PkRowDivider()
    }
}

/** স্বয়ংক্রিয়ভাবে হিসাবকৃত (read-only) মান দেখানোর রো — .fauto এর সমতুল্য */
@Composable
fun PkAutoRow(label: String, value: String, last: Boolean = false) {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF1F7F3))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 12.5.sp, color = Color(0xFF6C6A64), modifier = Modifier.width(150.dp))
            Text(value, fontSize = 13.5.sp, color = Color(0xFF2BB673), fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        }
        if (!last) PkRowDivider()
    }
}

/** টেবিলের রো-এর মাঝের হালকা ডিভাইডার লাইন */
@Composable
internal fun PkRowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(1.dp)
            .background(Color(0xFFEAE5D6))
    )
}

/** প্রতিটা বিষয়ের জন্য আলাদা "টেবিল" — টাইটেল + বর্ডারযুক্ত গোলাকার কার্ড, ভেতরে রো ও ডিভাইডার */
@Composable
internal fun PkSectionCard(
    title: String,
    topPadding: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    SectionTitle(title, topPadding = topPadding)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFBFAF6))
            .border(1.dp, Color(0xFFE2D9C4), RoundedCornerShape(12.dp))
    ) { content() }
}

/** স্ট্রিং ইনপুটকে Double এ কনভার্ট করার হেল্পার — খালি বা অবৈধ হলে 0.0 */
fun String.toInputDouble(): Double = this.toDoubleOrNull() ?: 0.0
