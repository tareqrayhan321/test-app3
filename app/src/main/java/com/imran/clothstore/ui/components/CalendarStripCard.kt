package com.imran.clothstore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imran.clothstore.util.AR_MONTHS_SHORT
import com.imran.clothstore.util.BN_MONTHS
import com.imran.clothstore.util.getBanglaDateToday
import com.imran.clothstore.util.getHijriDateToday
import com.imran.clothstore.util.toArabicDigits
import com.imran.clothstore.util.toBengaliDigits
import java.util.Calendar

private val CalCanvas = Color(0xFFFAF9F5)
private val CalHairline = Color(0xFFE6DFD8)
private val CalMuted = Color(0xFF6C6A64)
private val CalMutedSoft = Color(0xFF8E8B82)
private val CalPrimary = Color(0xFFCC785C)
private val CalPrimarySoft = Color(0xFFF3E3DC)
private val CalPrimaryActive = Color(0xFFA9583E)
private val CalInk = Color(0xFF141413)

private val EN_MONTHS_SHORT = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

private data class WeekDay(val dayIdx: Int, val name: String)

// সোম(1) মঙ্গল(2) বুধ(3) বৃহ(4) শুক্র(5) শনি(6) রবি(0) — ওয়েব অ্যাপের order[] হুবহু
private val WEEK_ORDER = listOf(
    WeekDay(1, "সোম"), WeekDay(2, "মঙ্গল"), WeekDay(3, "বুধ"),
    WeekDay(4, "বৃহ."), WeekDay(5, "শুক্র"), WeekDay(6, "শনি"), WeekDay(0, "রবি")
)

/**
 * ওয়েব অ্যাপের #dashCalCard (.cal-card) এর সমতুল্য — বাংলা/হিজরি/ইংরেজি তারিখ বাটন
 * (index.html লাইন ৩৩৮৫-৩৪০৪) + নিচে সাপ্তাহিক দিন-স্ট্রিপ, আজকের দিন হাইলাইট করা।
 */
@Composable
fun CalendarStripCard(modifier: Modifier = Modifier) {
    val today = remember { Calendar.getInstance() }
    val todayWeekday = remember { today.get(Calendar.DAY_OF_WEEK) - 1 } // Calendar: 1=রবি..7=শনি → 0=রবি..6=শনি
    val monthDates = remember {
        val jsWeekday = todayWeekday // 0=রবি..6=শনি, ওয়েব অ্যাপের getDay() এর সমতুল্য
        val diff = (jsWeekday - 1 + 7) % 7
        val mon = today.clone() as Calendar
        mon.add(Calendar.DAY_OF_MONTH, -diff)
        WEEK_ORDER.mapIndexed { i, wd ->
            val d = mon.clone() as Calendar
            d.add(Calendar.DAY_OF_MONTH, i)
            wd.dayIdx to d.get(Calendar.DAY_OF_MONTH)
        }.toMap()
    }

    val banglaLabel = remember {
        val bd = getBanglaDateToday()
        "${bd.day.toBengaliDigits()} ${BN_MONTHS[bd.monthIdx]} ${bd.year.toBengaliDigits()}"
    }
    val hijriLabel = remember {
        val hd = getHijriDateToday()
        if (hd != null) "${hd.day.toArabicDigits()} ${AR_MONTHS_SHORT[hd.monthIdx]} ${hd.year.toArabicDigits()}" else null
    }
    val engLabel = remember {
        "${today.get(Calendar.DAY_OF_MONTH)} ${EN_MONTHS_SHORT[today.get(Calendar.MONTH)]} ${today.get(Calendar.YEAR)}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CalCanvas)
            .border(1.dp, CalHairline, RoundedCornerShape(12.dp))
    ) {
        // ── তারিখ বাটন সারি (বাংলা | হিজরি | ইংরেজি) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = banglaLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CalMuted)
            if (hijriLabel != null) {
                Text(text = "  |  ", fontSize = 13.sp, color = CalHairline)
                Text(text = hijriLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CalMuted)
            }
            Text(text = "  |  ", fontSize = 13.sp, color = CalHairline)
            Text(text = engLabel, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = CalMuted)
        }

        // ── সাপ্তাহিক দিন-স্ট্রিপ (উপরে hairline বর্ডার-টপ, ওয়েব CSS .hdr-week-cal এর মতো) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = CalHairline,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            WEEK_ORDER.forEach { wd ->
                val isToday = wd.dayIdx == todayWeekday
                val dateNum = monthDates[wd.dayIdx] ?: 0
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .then(
                            if (isToday) Modifier
                                .background(CalPrimarySoft)
                                .border(1.5.dp, CalPrimary, RoundedCornerShape(10.dp))
                            else Modifier
                        )
                        .padding(vertical = 5.dp, horizontal = 2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = wd.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isToday) CalPrimaryActive else CalMutedSoft
                    )
                    Text(
                        text = dateNum.toBengaliDigits(),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isToday) CalPrimaryActive else CalInk
                    )
                }
            }
        }
    }
}
