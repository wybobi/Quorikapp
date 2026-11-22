package com.example.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfExportUtil {

    data class PdfSection(
        val heading: String,
        val body: String
    )

    fun exportAndSharePdf(
        context: Context,
        title: String,
        subtitle: String,
        code: String,
        sections: List<PdfSection>
    ): Boolean {
        return try {
            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842
            val margin = 40f
            val contentWidth = pageWidth - margin * 2

            val headerBgPaint = Paint().apply { color = Color.parseColor("#1E3B8B") }
            val brandPaint = Paint().apply {
                color = Color.WHITE
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val codePaint = Paint().apply {
                color = Color.parseColor("#FDE68A")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val titlePaint = Paint().apply {
                color = Color.parseColor("#0F172A")
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val subPaint = Paint().apply {
                color = Color.parseColor("#475569")
                textSize = 10.5f
                isAntiAlias = true
            }
            val sectionHeadPaint = Paint().apply {
                color = Color.parseColor("#1E3B8B")
                textSize = 12.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val bodyPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 11f
                isAntiAlias = true
            }
            val boxStrokePaint = Paint().apply {
                color = Color.parseColor("#CBD5E1")
                style = Paint.Style.STROKE
                strokeWidth = 1f
                isAntiAlias = true
            }
            val footerPaint = Paint().apply {
                color = Color.parseColor("#64748B")
                textSize = 9f
                isAntiAlias = true
            }

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            // Draw Royal Blue Header on first page
            canvas.drawRoundRect(
                RectF(margin, margin, pageWidth - margin, margin + 52f),
                12f, 12f, headerBgPaint
            )
            canvas.drawText("Quorik • BA Arts Study Portal", margin + 16f, margin + 24f, brandPaint)
            canvas.drawText(code, margin + 16f, margin + 40f, codePaint)

            var y = margin + 74f

            // Title & subtitle
            for (line in wrapText(title, titlePaint, contentWidth)) {
                canvas.drawText(line, margin, y, titlePaint)
                y += 20f
            }
            for (line in wrapText(subtitle, subPaint, contentWidth)) {
                canvas.drawText(line, margin, y, subPaint)
                y += 15f
            }
            y += 12f

            fun ensureSpace(neededHeight: Float) {
                if (y + neededHeight > pageHeight - margin - 24f) {
                    canvas.drawText(
                        "Quorik Free Study PDF • Page $pageNumber",
                        margin,
                        pageHeight - 24f,
                        footerPaint
                    )
                    document.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas
                    y = margin + 12f
                }
            }

            for (sec in sections) {
                val headLines = wrapText(sec.heading, sectionHeadPaint, contentWidth - 20f)
                val bodyLines = sec.body.split("\n").flatMap { para ->
                    if (para.isBlank()) listOf("") else wrapText(para, bodyPaint, contentWidth - 20f)
                }

                ensureSpace(44f)
                val startBoxY = y
                y += 16f
                for (hl in headLines) {
                    ensureSpace(18f)
                    canvas.drawText(hl, margin + 10f, y, sectionHeadPaint)
                    y += 17f
                }
                y += 4f
                for (bl in bodyLines) {
                    if (bl.isEmpty()) {
                        y += 8f
                    } else {
                        ensureSpace(16f)
                        canvas.drawText(bl, margin + 10f, y, bodyPaint)
                        y += 15f
                    }
                }
                y += 8f
                if (y > startBoxY + 20f && y < pageHeight - margin) {
                    canvas.drawRoundRect(
                        RectF(margin, startBoxY, pageWidth - margin, y),
                        8f, 8f, boxStrokePaint
                    )
                }
                y += 14f
            }

            canvas.drawText(
                "Quorik Free Study PDF • Page $pageNumber",
                margin,
                pageHeight - 24f,
                footerPaint
            )
            document.finishPage(page)

            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val safeName = code.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "Quorik_Doc" }
            val outFile = File(exportDir, "${safeName}_Quorik.pdf")
            FileOutputStream(outFile).use { fos -> document.writeTo(fos) }
            document.close()

            shareFile(context, outFile, "application/pdf", title)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun exportAndShareJsonBackup(context: Context, jsonContent: String): Boolean {
        return try {
            val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
            val outFile = File(exportDir, "quorik_by_bobi_backup.json")
            outFile.writeText(jsonContent)
            shareFile(context, outFile, "application/json", "Quorik by BOBI Backup JSON")
            true
        } catch (_: Exception) {
            false
        }
    }

    fun openLocalPdfFile(context: Context, filePath: String, title: String): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) return false
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(viewIntent)
            } catch (_: Exception) {
                shareFile(context, file, "application/pdf", title)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun shareFile(context: Context, file: File, mimeType: String, subject: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Open or Share $subject").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
    }

    fun readFromClipboard(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val item = clipboard?.primaryClip?.getItemAt(0)
        return item?.coerceToText(context)?.toString()?.trim()
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (words.isEmpty()) return emptyList()
        val lines = mutableListOf<String>()
        var current = StringBuilder()
        for (word in words) {
            val candidate = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(candidate) <= maxWidth) {
                current = StringBuilder(candidate)
            } else {
                if (current.isNotEmpty()) lines.add(current.toString())
                current = StringBuilder(word)
            }
        }
        if (current.isNotEmpty()) lines.add(current.toString())
        return lines
    }
}
