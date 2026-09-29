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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.ui.screens.reports.formatTaka

/**
 * ওয়েব অ্যাপের STEP 1 (Fixed Costs + Maintenance Costs) এর সমতুল্য ফর্ম সেকশন।
 * নিচে সাপ্তাহিক স্থায়ী খরচের স্বয়ংক্রিয় ব্রেকডাউন (weeklyFixed()) দেখায়।
 */
@Composable
fun PkStep1Fixed(
    input: PaikkariInput,
    onUpdate: ((PaikkariInput) -> PaikkariInput) -> Unit
) {
    var yearlyRent by remember { mutableStateOf(input.yearlyRent.takeIf { it != 0.0 }?.toString() ?: "") }
    var monthlyElec by remember { mutableStateOf(input.monthlyElec.takeIf { it != 0.0 }?.toString() ?: "") }
    var monthlyWifi by remember { mutableStateOf(input.monthlyWifi.takeIf { it != 0.0 }?.toString() ?: "") }
    var weeklyMath by remember { mutableStateOf(input.weeklyMath.takeIf { it != 0.0 }?.toString() ?: "") }
    var weeklyToilet by remember { mutableStateOf(input.weeklyToilet.takeIf { it != 0.0 }?.toString() ?: "") }

    var salary by remember { mutableStateOf(input.salary.takeIf { it != 0.0 }?.toString() ?: "") }
    var ownerSalary by remember { mutableStateOf(input.ownerSalary.takeIf { it != 0.0 }?.toString() ?: "") }
    var transport by remember { mutableStateOf(input.transport.takeIf { it != 0.0 }?.toString() ?: "") }
    var vehicle by remember { mutableStateOf(input.vehicle.takeIf { it != 0.0 }?.toString() ?: "") }
    var misc by remember { mutableStateOf(input.misc.takeIf { it != 0.0 }?.toString() ?: "") }

    fun sync() {
        onUpdate {
            it.copy(
                yearlyRent = yearlyRent.toInputDouble(),
                monthlyElec = monthlyElec.toInputDouble(),
                monthlyWifi = monthlyWifi.toInputDouble(),
                weeklyMath = weeklyMath.toInputDouble(),
                weeklyToilet = weeklyToilet.toInputDouble(),
                salary = salary.toInputDouble(),
                ownerSalary = ownerSalary.toInputDouble(),
                transport = transport.toInputDouble(),
                vehicle = vehicle.toInputDouble(),
                misc = misc.toInputDouble()
            )
        }
    }

    Column {
        SectionTitle("স্থায়ী খরচ — Fixed Costs")
        PkInputRow("বাৎসরিক ভাড়া", "৳/বছর", yearlyRent, { yearlyRent = it; sync() }, isDecimal = true)
        PkInputRow("মাসিক বিদ্যুৎ", "৳/মাস", monthlyElec, { monthlyElec = it; sync() }, isDecimal = true)
        PkInputRow("মাসিক ওয়াইফাই", "৳/মাস", monthlyWifi, { monthlyWifi = it; sync() }, isDecimal = true)
        PkInputRow("সাপ্তাহিক মাঠ ভাড়া", "৳/সপ্তাহ", weeklyMath, { weeklyMath = it; sync() }, isDecimal = true)
        PkInputRow("সাপ্তাহিক টয়লেট", "৳/সপ্তাহ", weeklyToilet, { weeklyToilet = it; sync() }, isDecimal = true)

        SectionTitle("রক্ষণাবেক্ষণ খরচ — Maintenance Costs", topPadding = 20.dp)
        PkInputRow("কর্মচারী বেতন", "৳", salary, { salary = it; sync() }, isDecimal = true)
        PkInputRow("মালিকের বেতন", "৳", ownerSalary, { ownerSalary = it; sync() }, isDecimal = true)
        PkInputRow("যাতায়াত খরচ", "৳", transport, { transport = it; sync() }, isDecimal = true)
        PkInputRow("যানবাহন খরচ", "৳", vehicle, { vehicle = it; sync() }, isDecimal = true)
        PkInputRow("বিবিধ খরচ", "৳", misc, { misc = it; sync() }, isDecimal = true)

        // ── সাপ্তাহিক স্থায়ী খরচের স্বয়ংক্রিয় ব্রেকডাউন (weeklyFixed()) ──
        val f = computeWeeklyFixed(
            input.copy(
                yearlyRent = yearlyRent.toInputDouble(),
                monthlyElec = monthlyElec.toInputDouble(),
                monthlyWifi = monthlyWifi.toInputDouble(),
                weeklyMath = weeklyMath.toInputDouble(),
                weeklyToilet = weeklyToilet.toInputDouble()
            )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Text("সাপ্তাহিক স্থায়ী খরচ (স্বয়ংক্রিয়)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6C6A64))
            PkAutoRow("ভাড়া/সপ্তাহ", formatTaka(f.wRent))
            PkAutoRow("বিদ্যুৎ/সপ্তাহ", formatTaka(f.wElec))
            PkAutoRow("ওয়াইফাই/সপ্তাহ", formatTaka(f.wWifi))
            PkAutoRow("মোট (যাকাত ছাড়া)", formatTaka(f.totalExcludingZakat))
        }
    }
}

@Composable
internal fun SectionTitle(text: String, topPadding: androidx.compose.ui.unit.Dp = 0.dp) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF6C5A2A),
        modifier = Modifier.padding(top = topPadding, bottom = 6.dp)
    )
}
