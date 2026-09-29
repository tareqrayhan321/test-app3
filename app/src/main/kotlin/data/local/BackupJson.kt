package com.imran.clothstore.data.local

import com.imran.clothstore.data.backup.BackupPayload
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** BackupPayload ↔ JSON — শুধু লোকাল Room ক্যাশে সংরক্ষণের জন্য (Firestore POJO ম্যাপিং আলাদা) */
object BackupJson {
    private val json = Json {
        ignoreUnknownKeys = true // ভবিষ্যতে ওয়েব অ্যাপ নতুন ফিল্ড যোগ করলে ক্র্যাশ না করার জন্য
        encodeDefaults = true
    }

    fun encode(payload: BackupPayload): String = json.encodeToString(payload)

    /** পার্স ব্যর্থ হলে null — ক্র্যাশ না করে caller খালি BackupPayload() দিয়ে ফলব্যাক করবে */
    fun decode(raw: String): BackupPayload? = try {
        json.decodeFromString(BackupPayload.serializer(), raw)
    } catch (e: Exception) {
        null
    }
}
