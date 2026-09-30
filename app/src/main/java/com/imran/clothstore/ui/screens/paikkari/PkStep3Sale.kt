package com.imran.clothstore.ui.screens.paikkari

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.screens.reports.formatTaka

/**
 * ওয়েব অ্যাপের STEP 3 (Sales & Purchase) এর সমতুল্য ফর্ম সেকশন।
 * নগদ/বাকি বিক্রয়, নগদ/বাকি ক্রয়, পুরনো আদায় ও দেনা — সবার নিচে "মোট বিক্রি" স্বয়ংক্রিয়ভাবে দেখায়।
 */
@Composable
fun PkStep3Sale(
    input: PaikkariInput,
    onUpdate: ((PaikkariInput) -> PaikkariInput) -> Unit
) {
    var cashSale by remember { mutableStateOf(input.cashSale.takeIf { it != 0.0 }?.toString() ?: "") }
    var creditSale by remember { mutableStateOf(input.creditSale.takeIf { it != 0.0 }?.toString() ?: "") }
    var cashPurchase by remember { mutableStateOf(input.cashPurchase.takeIf { it != 0.0 }?.toString() ?: "") }
    var creditPurchase by remember { mutableStateOf(input.creditPurchase.takeIf { it != 0.0 }?.toString() ?: "") }
    var oldCollection by remember { mutableStateOf(input.oldCollection.takeIf { it != 0.0 }?.toString() ?: "") }
    var oldDebt by remember { mutableStateOf(input.oldDebt.takeIf { it != 0.0 }?.toString() ?: "") }

    fun sync() {
        onUpdate {
            it.copy(
                cashSale = cashSale.toInputDouble(),
                creditSale = creditSale.toInputDouble(),
                cashPurchase = cashPurchase.toInputDouble(),
                creditPurchase = creditPurchase.toInputDouble(),
                oldCollection = oldCollection.toInputDouble(),
                oldDebt = oldDebt.toInputDouble()
            )
        }
    }

    Column {
        val totalSale = cashSale.toInputDouble() + creditSale.toInputDouble()
        PkSectionCard("বিক্রয় — Sale", topPadding = 20.dp) {
            PkInputRow("নগদ বিক্রয়", "৳", cashSale, { cashSale = it; sync() }, isDecimal = true)
            PkInputRow("বাকি বিক্রয়", "৳", creditSale, { creditSale = it; sync() }, isDecimal = true)
            PkAutoRow("মোট বিক্রি", formatTaka(totalSale), last = true)
        }

        PkSectionCard("ক্রয় — Purchase", topPadding = 20.dp) {
            PkInputRow("নগদ ক্রয়", "৳", cashPurchase, { cashPurchase = it; sync() }, isDecimal = true)
            PkInputRow("বাকি ক্রয়", "৳", creditPurchase, { creditPurchase = it; sync() }, isDecimal = true, last = true)
        }

        PkSectionCard("আদায় ও দেনা", topPadding = 20.dp) {
            PkInputRow("পুরনো বাকি আদায়", "৳", oldCollection, { oldCollection = it; sync() }, isDecimal = true)
            PkInputRow("পুরনো দেনা পরিশোধ", "৳", oldDebt, { oldDebt = it; sync() }, isDecimal = true, last = true)
        }
    }
}
