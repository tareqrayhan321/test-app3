package com.imran.clothstore.ui.screens.debtors

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imran.clothstore.ui.theme.AppColors

/**
 * ওয়েব অ্যাপের dlScreen (Debtors List / Party Ledger) এর সরাসরি Kotlin/Compose সংস্করণ।
 * হেডার + ক্যাটাগরি ট্যাব + সার্চ → Excel-স্টাইল টেবিল → ফুটারে মোট পাওনা/বকেয়া।
 * হেডারে ব্যাক আইকন নেই — ফোনের সিস্টেম ব্যাক জেসচার/বাটন দিয়ে ফেরা যায় (BackHandler)।
 */
@Composable
fun DebtorsListScreen(
    onBack: () -> Unit,
    viewModel: DebtorsListViewModel = viewModel()
) {
    BackHandler(onBack = onBack)
    val rows by viewModel.filteredRows.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val totalPawona by viewModel.totalPawona.collectAsState()
    val totalBokea by viewModel.totalBokea.collectAsState()

    var jomaDialogRow by remember { mutableStateOf<DebtorRow?>(null) }

    // কাপড় ক্রয় স্ক্রিনের কাঠামো: টিল পটভূমি → গ্রেডিয়েন্ট হেডার (স্ট্যাটাস বারের নিচে) → গোলাকার-মাথা বডি
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── হেডার — সাদা, সমান (গোলাকার নয়) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
        ) {
            Column {
                Text(
                    text = "Party Ledger",
                    color = Color(0xFF1A1A1A),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "সকল দেনাদার/পাওনাদারের সম্মিলিত তালিকা",
                    color = Color(0xFF6C6A64),
                    fontSize = 11.sp
                )
            }

            // ── ক্যাটাগরি ফিল্টার ট্যাব ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                viewModel.tabs.forEach { tab ->
                    val isActive = tab == activeTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isActive) Color(0xFFE0B84A) else Color(0xFFECE8DC))
                            .clickable { viewModel.onTabChange(tab) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tab.labelBn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A0F06)
                        )
                    }
                }
            }

            // সার্চ বক্স — গোল (pill) নয়, গোলাকার-কোণা আয়তক্ষেত্র; উচ্চতা আগের ৫৬dp থেকে কমিয়ে ৪৬dp
            val searchShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .height(46.dp)
                    .clip(searchShape)
                    .background(Color.White)
                    .border(1.dp, Color(0xFF79747E), searchShape)
                    .padding(horizontal = 14.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = query,
                    onValueChange = viewModel::onSearchChange,
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFF1C1B1F)),
                    cursorBrush = SolidColor(Color(0xFF0B4A4E)),
                    modifier = Modifier.fillMaxWidth()
                )
                if (query.isEmpty()) {
                    Text("নাম বা ঠিকানা খুঁজুন…", fontSize = 14.sp, color = Color(0xFF8A8794))
                }
            }
        }

        // ── বডি (সমান, গোলাকার নয়) ──
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
        ) {
        // ── বডি: টেবিল ──
        if (rows.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("কোনো এন্ট্রি পাওয়া যায়নি", fontSize = 13.sp, color = Color(0xFF9A96AD))
            }
        } else {
            // প্রতিটা কলামের প্রস্থ = ওই কলামের সবচেয়ে লম্বা লেখা যতটা জায়গা নেয় ততটা — নাম/ঠিকানা কাটা পড়বে না।
            // অস্বাভাবিক লম্বা নাম/ঠিকানা হলে কলাম একটা সীমায় থামে ও লেখা পরের লাইনে নামে (তবু পুরোটাই দেখা যায়)।
            // টেবিল স্ক্রিনের চেয়ে চওড়া হলে পাশে স্ক্রল করে বাকি অংশ দেখা যাবে।
            val textMeasurer = rememberTextMeasurer()
            val density = LocalDensity.current
            val nameStyle = LocalTextStyle.current.copy(fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            val moneyStyle = LocalTextStyle.current.copy(fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
            val addrStyle = LocalTextStyle.current.copy(fontSize = 10.5.sp)
            val headStyle = LocalTextStyle.current.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold)

            BoxWithConstraints(modifier = Modifier.weight(1f, fill = true).fillMaxWidth()) {
                val capWidth = maxWidth * 0.36f

                fun widthOf(text: String, style: TextStyle): Int =
                    textMeasurer.measure(text = text, style = style, softWrap = false).size.width

                val nameColWidth = remember(rows, nameStyle, density, capWidth) {
                    val maxPx = rows.maxOfOrNull { widthOf(it.entry.name.ifBlank { "—" }, nameStyle) } ?: 0
                    minOf(capWidth, maxOf(70.dp, with(density) { maxPx.toDp() } + 14.dp))
                }
                val moneyColWidth = remember(rows, moneyStyle, headStyle, density) {
                    val maxPx = maxOf(
                        rows.maxOfOrNull { widthOf("৳${it.entry.baki.toLong()}", moneyStyle) } ?: 0,
                        widthOf("পাওনা/বকেয়া", headStyle)
                    )
                    with(density) { maxPx.toDp() } + 14.dp
                }
                val addrColWidth = remember(rows, addrStyle, density, capWidth) {
                    val maxPx = rows.maxOfOrNull { widthOf(it.entry.addr.ifBlank { "—" }, addrStyle) } ?: 0
                    minOf(capWidth, maxOf(50.dp, with(density) { maxPx.toDp() } + 14.dp))
                }

                // পাঁচটা গ্রিড কলামের যোগফল (নাম + বাকি + ঠিকানা + কল + জমা)
                val tableWidth = maxOf(maxWidth, nameColWidth + moneyColWidth + addrColWidth + DlCallColWidth + DlJomaColWidth)
                Box(modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
                    LazyColumn(modifier = Modifier.width(tableWidth).fillMaxHeight()) {
                        // টেবিল হেডার — স্টিকি, ছোট ফন্ট, হালকা রঙের পট্টি; নিচে স্ক্রল করলেও উপরে আটকে থাকে
                        stickyHeader {
                            Row(modifier = Modifier.fillMaxWidth().background(DlHeaderBg).height(34.dp)) {
                                val hdrs = listOf(
                                    "পার্টির নাম" to nameColWidth,
                                    "পাওনা/বকেয়া" to moneyColWidth,
                                    "ঠিকানা" to addrColWidth,
                                    "কল" to DlCallColWidth,
                                    "জমা" to DlJomaColWidth
                                )
                                hdrs.forEach { (label, w) ->
                                    Box(
                                        modifier = Modifier.width(w).fillMaxHeight().border(0.5.dp, DlHeaderGrid),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(label, style = headStyle, color = Color.White, maxLines = 1, softWrap = false)
                                    }
                                }
                            }
                        }
                        // ডুপ্লিকেট id থাকলেও যেন অ্যাপ ক্র্যাশ না করে — তাই key-তে ইনডেক্সও আছে
                        itemsIndexed(rows, key = { index, it -> "${it.category.name}_${it.entry.id}_$index" }) { index, row ->
                            DlTableRow(
                                row = row,
                                nameWidth = nameColWidth,
                                moneyWidth = moneyColWidth,
                                addrWidth = addrColWidth,
                                rowIndex = index,
                                onJomaClick = { jomaDialogRow = row }
                            )
                        }
                    }
                }
            }

            // ── ফুটার — মোট পাওনা / মোট বকেয়া ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(top = 12.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("মোট পাওনা", fontSize = 10.sp, color = Color(0xFF6C6A64))
                    Text("৳${totalPawona.toLong()}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("মোট বকেয়া", fontSize = 10.sp, color = Color(0xFF6C6A64))
                    Text("৳${totalBokea.toLong()}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        }
    }

    // ── দ্রুত জমা ডায়ালগ ──
    jomaDialogRow?.let { row ->
        DlQuickJomaDialog(
            entryName = row.entry.name,
            onDismiss = { jomaDialogRow = null },
            onSave = { amount, date, note ->
                viewModel.addQuickJoma(row, amount, date, note)
                jomaDialogRow = null
            }
        )
    }
}
