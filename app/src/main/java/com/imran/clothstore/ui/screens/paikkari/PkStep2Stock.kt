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
 * ওয়েব অ্যাপের STEP 2 (Stock) এর সমতুল্য ফর্ম সেকশন।
 * ক্রয়কৃত গজ, বিক্রিত গজ, প্রতি গজে লাভ — নিচে স্বয়ংক্রিয় "আনুমানিক মোট লাভ" দেখায়।
 */
@Composable
fun PkStep2Stock(
    input: PaikkariInput,
    onUpdate: ((PaikkariInput) -> PaikkariInput) -> Unit
) {
    var stockInYard by remember { mutableStateOf(input.stockInYard.takeIf { it != 0.0 }?.toString() ?: "") }
    var soldGaj by remember { mutableStateOf(input.soldGaj.takeIf { it != 0.0 }?.toString() ?: "") }
    var profitGaj by remember { mutableStateOf(input.profitGaj.takeIf { it != 0.0 }?.toString() ?: "") }

    fun sync() {
        onUpdate {
            it.copy(
                stockInYard = stockInYard.toInputDouble(),
                soldGaj = soldGaj.toInputDouble(),
                profitGaj = profitGaj.toInputDouble()
            )
        }
    }

    Column {
        val estProfit = soldGaj.toInputDouble() * profitGaj.toInputDouble()
        PkSectionCard("স্টক — Stock", topPadding = 20.dp) {
            PkInputRow("ক্রয়কৃত গজ", "গজ", stockInYard, { stockInYard = it; sync() }, isDecimal = true)
            PkInputRow("বিক্রিত গজ", "গজ", soldGaj, { soldGaj = it; sync() }, isDecimal = true)
            PkInputRow("প্রতি গজে লাভ", "৳/গজ", profitGaj, { profitGaj = it; sync() }, isDecimal = true)
            PkAutoRow("আনুমানিক মোট লাভ", formatTaka(estProfit), last = true)
        }
    }
}
