package com.imran.clothstore.data.local

import com.imran.clothstore.data.backup.BackupPayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Room-এর BackupCacheDao-কে টাইপড BackupPayload ইন্টারফেসে মুড়ে রাখে, যাতে
 * BackupRepository (data/backup/) সরাসরি JSON এনকোডিং/DAO নিয়ে মাথা না ঘামায়।
 */
class LocalCacheRepository(private val dao: BackupCacheDao) {

    /** লোকাল ক্যাশে এখনো কিছু না থাকলে খালি BackupPayload() দেয় — প্রথমবার অ্যাপ চালু হলে */
    fun observe(): Flow<BackupPayload> = dao.observe().map { entity ->
        entity?.let { BackupJson.decode(it.payloadJson) } ?: BackupPayload()
    }

    suspend fun getOnce(): BackupPayload {
        val entity = dao.getOnce() ?: return BackupPayload()
        return BackupJson.decode(entity.payloadJson) ?: BackupPayload()
    }

    /**
     * @param markPendingSync true হলে বোঝায় এই পরিবর্তন এখনো Firestore-এ push হয়নি
     * (ইউজারের করা এডিট) — sync worker এই ফ্ল্যাগ দেখে push করবে।
     * false হলে বোঝায় এই লেখা Firestore থেকে আসা (pull/listener), তাই ইতিমধ্যে sync করা।
     */
    suspend fun save(payload: BackupPayload, markPendingSync: Boolean) {
        dao.upsert(
            BackupCacheEntity(
                payloadJson = BackupJson.encode(payload),
                localUpdatedAt = System.currentTimeMillis(),
                pendingSync = markPendingSync
            )
        )
    }

    suspend fun hasPendingSync(): Boolean = dao.isPendingSync() ?: false
}
