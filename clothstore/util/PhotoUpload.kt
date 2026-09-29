package com.imran.clothstore.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * ওয়েব অ্যাপের _photoCompress()/_photoUpload() (index.html লাইন ৪৭৩৮-৪৭৭০) এর
 * Android সমতুল্য — Firebase Storage ব্যবহার করা হয় না; ছবি রিসাইজ+কম্প্রেস করে
 * base64 JPEG data URL হিসেবে সরাসরি Entry.photoUrl ফিল্ডে (এবং তাই Firestore
 * ডকুমেন্টে) রাখা হয়। এই দুই অ্যাপ একই photoUrl ফরম্যাট শেয়ার করে বলে ওয়েব অ্যাপে
 * তোলা ছবি Android-এ এবং উল্টোটাও দেখা যাবে।
 */
object PhotoUpload {
    private const val MAX_SIZE_PX = 320
    private const val JPEG_QUALITY = 82
    private const val MAX_BYTES = 300 * 1024 // ওয়েব অ্যাপের ৩০০KB সেফ-মার্জিনের সাথে মিল রেখে

    sealed class Result {
        data class Success(val dataUrl: String) : Result()
        data class Error(val message: String) : Result()
    }

    /**
     * গ্যালারি/ক্যামেরা থেকে বাছাই করা ছবির Uri থেকে একটা ৩২০px-ম্যাক্স, JPEG-কম্প্রেসড
     * base64 data URL ("data:image/jpeg;base64,...") বানায়। ৩০০KB-র বেশি হলে এরর দেয়
     * (ওয়েব অ্যাপের approxBytes চেক অনুযায়ী)।
     */
    fun compressToDataUrl(contentResolver: ContentResolver, uri: Uri): Result {
        val bitmap = try {
            contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            } ?: return Result.Error("ছবি লোড হয়নি")
        } catch (e: Exception) {
            return Result.Error("ফাইল পড়া যায়নি")
        }

        val (targetW, targetH) = scaledDimensions(bitmap.width, bitmap.height)
        val scaled = if (targetW == bitmap.width && targetH == bitmap.height) {
            bitmap
        } else {
            Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
        }

        val outputStream = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
        val bytes = outputStream.toByteArray()

        if (bytes.size > MAX_BYTES) {
            return Result.Error("ছবি অনেক বড় — আবার চেষ্টা করুন")
        }

        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
        return Result.Success("data:image/jpeg;base64,$base64")
    }

    /** ওয়েব অ্যাপের canvas রিসাইজ লজিকের সমতুল্য: বড় পাশ MAX_SIZE_PX-এ নামিয়ে আসপেক্ট রেশিও বজায় রাখা */
    private fun scaledDimensions(w: Int, h: Int): Pair<Int, Int> {
        if (w <= MAX_SIZE_PX && h <= MAX_SIZE_PX) return w to h
        return if (w > h) {
            MAX_SIZE_PX to (h.toFloat() * MAX_SIZE_PX / w).toInt().coerceAtLeast(1)
        } else {
            (w.toFloat() * MAX_SIZE_PX / h).toInt().coerceAtLeast(1) to MAX_SIZE_PX
        }
    }
}
