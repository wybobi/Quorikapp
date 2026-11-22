package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object MediaStorageUtil {

    suspend fun saveCompressedImageFromUri(context: Context, uri: Uri, maxWidth: Int = 1000): String? =
        withContext(Dispatchers.IO) {
            try {
                val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
                val original = input.use { BitmapFactory.decodeStream(it) } ?: return@withContext null
                val scaled = if (original.width > maxWidth) {
                    val ratio = maxWidth.toFloat() / original.width.toFloat()
                    val targetHeight = (original.height * ratio).toInt().coerceAtLeast(1)
                    Bitmap.createScaledBitmap(original, maxWidth, targetHeight, true)
                } else {
                    original
                }
                val dir = File(context.filesDir, "media").apply { mkdirs() }
                val outFile = File(dir, "img_${System.currentTimeMillis()}_${(1000..9999).random()}.jpg")
                FileOutputStream(outFile).use { fos ->
                    scaled.compress(Bitmap.CompressFormat.JPEG, 82, fos)
                }
                if (scaled !== original) scaled.recycle()
                original.recycle()
                outFile.absolutePath
            } catch (_: Exception) {
                null
            }
        }

    suspend fun savePdfFromUri(context: Context, uri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                val input = context.contentResolver.openInputStream(uri) ?: return@withContext null
                val dir = File(context.filesDir, "pdfs").apply { mkdirs() }
                val outFile = File(dir, "pdf_${System.currentTimeMillis()}.pdf")
                input.use { ins ->
                    FileOutputStream(outFile).use { outs ->
                        ins.copyTo(outs)
                    }
                }
                outFile.absolutePath
            } catch (_: Exception) {
                null
            }
        }

    suspend fun readTextFromUri(context: Context, uri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            } catch (_: Exception) {
                null
            }
        }
}
