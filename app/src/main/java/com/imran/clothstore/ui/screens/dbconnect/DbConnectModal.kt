package com.imran.clothstore.ui.screens.dbconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * ওয়েব অ্যাপের dbOverlay (ডাটাবেইজ সংযোগ মোডাল) এর সরাসরি Kotlin/Compose সংস্করণ।
 * এই Android অ্যাপ Firestore-নেটিভ বলে dbPush()/dbPull() নেই — শুধু সংযোগ স্ট্যাটাস ও
 * সিঙ্ক যাচাইয়ের বাটন আছে (ক্লাউড ডেটা মুছে ফেলার বাটন সরিয়ে দেওয়া হয়েছে)।
 */
@Composable
fun DbConnectModal(
    onClose: () -> Unit,
    viewModel: DbConnectViewModel = viewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val lastSyncText by viewModel.lastSyncText.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── হেডার: স্ট্যাটাস বারের নিচে টাইটেল + হালকা ডিভাইডার (ব্যাক করতে সিস্টেম ব্যাক ব্যবহার হবে) ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 16.dp)
        ) {
            Text("ফায়ারবেস ডাটাবেইজ", color = Color(0xFF00363A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE3E6E6))
        )
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(14.dp))

        // ── স্ট্যাটাস বার ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF3F6F6))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dotColor = when (connectionState) {
                DbConnectionState.CONNECTED -> Color(0xFF2BB673)
                DbConnectionState.CONNECTING -> Color(0xFFE0B84A)
                DbConnectionState.ERROR -> Color(0xFFD9452B)
            }
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(
                    text = when (connectionState) {
                        DbConnectionState.CONNECTED -> "সংযুক্ত ✓"
                        DbConnectionState.CONNECTING -> "সংযোগ যাচাই করা হচ্ছে…"
                        DbConnectionState.ERROR -> "সংযোগ ব্যর্থ"
                    },
                    color = Color(0xFF1A1A1A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (lastSyncText.isNotBlank()) {
                    Text(lastSyncText, color = Color(0xFF6C6A64), fontSize = 10.5.sp, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }

        // ── ডেটা কোথায় আছে, ওয়েব অ্যাপের সাথে কীভাবে শেয়ার হয় তার ব্যাখ্যা ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE8F3F8))
                .padding(12.dp)
        ) {
            Text(
                "এই অ্যাপ ও ওয়েব অ্যাপ একই ডেটা শেয়ার করে",
                color = Color(0xFF1E5A75),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "সব ডেটা একটামাত্র নথিতে থাকে — imran_store/backup। আপনার Firebase প্রজেক্টে " +
                    "ওয়েব অ্যাপ থেকে যা ইতিমধ্যে আছে, এই অ্যাপ সেটাই সরাসরি দেখায় ও আপডেট করে — " +
                    "আলাদা করে ইম্পোর্ট/এক্সপোর্ট করার দরকার নেই। শুধু নিশ্চিত করুন এই অ্যাপ ও ওয়েব " +
                    "অ্যাপ একই Firebase প্রজেক্টে কানেক্ট করা আছে।",
                color = Color(0xFF3E5F6E),
                fontSize = 10.5.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ── অ্যাকশন ──
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedButton(
                onClick = { viewModel.checkConnection() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Firestore যাচাই ও সিঙ্ক করুন", fontSize = 13.sp)
            }
        }
    }
}
