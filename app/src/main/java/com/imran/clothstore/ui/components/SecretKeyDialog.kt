package com.imran.clothstore.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.theme.AppColors

/** ডিলিট/এডিট নিশ্চিত করার সিক্রেট কী */
const val SECRET_KEY = "1212"

/** বাংলা অঙ্কে টাইপ করলেও যেন মিলে যায় — বাংলা অঙ্ককে ইংরেজিতে রূপান্তর করে */
private fun normalizeDigits(s: String): String =
    s.map { c -> if (c in '০'..'৯') ('0' + (c - '০')) else c }.joinToString("")

/**
 * সিক্রেট কী চাওয়ার ডায়ালগ। কী মিললে onConfirm() চলে; ভুল হলে ত্রুটি দেখিয়ে ইনপুট খালি করে দেয়।
 */
@Composable
fun SecretKeyDialog(
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = "🔒 সিক্রেট কী",
    confirmLabel: String = "নিশ্চিত করুন"
) {
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(message, fontSize = 13.sp, color = Color(0xFF4A4740))
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.take(12); error = false },
                    singleLine = true,
                    isError = error,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    placeholder = { Text("সিক্রেট কী লিখুন", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
                if (error) {
                    Text(
                        "সিক্রেট কী সঠিক নয়",
                        color = Color(0xFFC0392B),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (normalizeDigits(input.trim()) == SECRET_KEY) {
                    onConfirm()
                } else {
                    error = true
                    input = ""
                }
            }) {
                Text(confirmLabel, color = AppColors.HeaderTeal, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("বাতিল", color = Color(0xFF6C6A64)) }
        }
    )
}
