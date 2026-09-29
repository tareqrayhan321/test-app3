package com.imran.clothstore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.theme.AppColors
import com.imran.clothstore.util.toBengaliDigits
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val FieldBg = Color(0xFFFAF9F5)
private val FieldBorder = Color(0xFFCFC5AC)

/**
 * পপআপ/ফর্মের সাধারণ ইনপুট বক্স। লেবেল খালি অবস্থায় বক্সের ভেতরে থাকে, টা
 * (বা ফোকাস পেলে) বর্ডারের উপরে উঠে যায়। উচ্চতা ফিক্স করা নেই, তাই লেখা কাটে
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    readOnly: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        singleLine = true,
        label = { Text(label, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        textStyle = TextStyle(fontSize = 15.sp, color = AppColors.TextPrimary),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = FieldBg,
            unfocusedContainerColor = FieldBg,
            focusedBorderColor = AppColors.HeaderTeal,
            unfocusedBorderColor = FieldBorder,
            focusedLabelColor = AppColors.HeaderTeal,
            unfocusedLabelColor = Color(0xFF6C6A64),
            cursorColor = AppColors.HeaderTeal
        )
    )
}

/**
 * তারিখের ইনপুট — লেখার সুযোগ নেই; ট্যাপ করলে ক্যালেন্ডার খোলে। মান ISO ("yyyy-MM-dd") 
 * সংরক্ষিত হয় (অ্যাপের বাকি অংশ এই ফরম্যাটই পড়ে), স্ক্রিনে "দিন/মাস/বছর" বা
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var open by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AppTextField(
            value = displayDate(value),
            onValueChange = {},
            label = label,
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = AppColors.HeaderTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
        // টেক্সট ফিল্ডের উপরের স্বচ্ছ স্তর — পুরো বক্সে ট্যাপ ধরে ক্যালেন্ডার 
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { open = true }
        )
    }

    if (open) {
        val state = rememberDatePickerState(initialSelectedDateMillis = isoToMillis(value))
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onValueChange(millisToIso(it)) }
                    open = false
                }) { Text("ঠিক আছে") }
            },
            dismissButton = {
                TextButton(onClick = { open = false }) { Text("বাতিল") }
            }
        ) {
            DatePicker(state = state)
        }
    }
}

private fun utcFormat(pattern: String) =
    SimpleDateFormat(pattern, Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

private fun isoToMillis(iso: String): Long? =
    runCatching { utcFormat("yyyy-MM-dd").parse(iso.take(10))?.time }.getOrNull()

private fun millisToIso(millis: Long): String = utcFormat("yyyy-MM-dd").format(millis)

private fun displayDate(iso: String): String {
    if (iso.isBlank()) return ""
    val millis = isoToMillis(iso) ?: return iso.toBengaliDigits()
    return utcFormat("dd/MM/yyyy").format(millis).toBengaliDigits()
}
