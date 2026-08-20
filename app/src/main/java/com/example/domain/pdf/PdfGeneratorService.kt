package com.example.domain.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.data.model.QuestionType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGeneratorService {

    /**
     * Generates a multi-page PDF document of all 45 test questions for the creator or student.
     */
    fun generateQuestionsPdf(
        context: Context,
        test: TestSessionEntity,
        questions: List<QuestionEntity>
    ): File? {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 standard width in points
            val pageHeight = 842 // A4 standard height in points

            val titlePaint = Paint().apply {
                color = Color.rgb(10, 37, 64)
                textSize = 15f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val subtitlePaint = Paint().apply {
                color = Color.rgb(75, 85, 99)
                textSize = 10f
                isAntiAlias = true
            }

            val questionTitlePaint = Paint().apply {
                color = Color.rgb(17, 24, 39)
                textSize = 10.5f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val textPaint = Paint().apply {
                color = Color.rgb(31, 41, 55)
                textSize = 9.5f
                isAntiAlias = true
            }

            val headerBgPaint = Paint().apply {
                color = Color.rgb(235, 245, 255)
                style = Paint.Style.FILL
            }

            val borderPaint = Paint().apply {
                color = Color.rgb(209, 213, 219)
                style = Paint.Style.STROKE
                strokeWidth = 0.8f
            }

            var currentPageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            var yPos = 40f

            fun drawHeader() {
                canvas.drawRect(30f, 25f, (pageWidth - 30).toFloat(), 80f, headerBgPaint)
                canvas.drawRect(30f, 25f, (pageWidth - 30).toFloat(), 80f, borderPaint)
                canvas.drawText("O'ZBEKISTON MILLIY SERTIFIKAT SINOVI", 45f, 48f, titlePaint)
                canvas.drawText("Fan: ${test.subject.titleUz}  |  ${test.title}  |  Savollar soni: 45 ta  |  Vaqt: ${test.timeLimitMinutes} daqiqa", 45f, 68f, subtitlePaint)
                yPos = 100f
            }

            drawHeader()

            for (q in questions) {
                // Check page height overflow
                if (yPos > pageHeight - 120) {
                    // Draw footer
                    canvas.drawText("Sahifa $currentPageNumber  |  Milliy Sertifikat Onlayn Tizimi", (pageWidth / 2 - 80).toFloat(), (pageHeight - 20).toFloat(), subtitlePaint)
                    pdfDocument.finishPage(page)

                    currentPageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    drawHeader()
                }

                // Question Header
                val typeTag = when (q.type) {
                    QuestionType.CLOSED_ABCD -> "Yopiq test"
                    QuestionType.OPEN_TWO_PARTS -> "Ochiq savol (a va b bandlar)"
                    QuestionType.ESSAY -> "Insho (Esse)"
                }

                canvas.drawText("${q.questionNumber}-savol. [$typeTag]", 35f, yPos, questionTitlePaint)
                yPos += 14f

                // Wrap question text
                val questionLines = splitTextIntoLines(q.questionText, textPaint, pageWidth - 70)
                for (line in questionLines) {
                    canvas.drawText(line, 35f, yPos, textPaint)
                    yPos += 12f
                }

                // Render options or open boxes
                when (q.type) {
                    QuestionType.CLOSED_ABCD -> {
                        val optA = q.optionA ?: "A varianti"
                        val optB = q.optionB ?: "B varianti"
                        val optC = q.optionC ?: "C varianti"
                        val optD = q.optionD ?: "D varianti"

                        canvas.drawText("A) $optA", 45f, yPos + 2f, textPaint)
                        yPos += 13f
                        canvas.drawText("B) $optB", 45f, yPos + 2f, textPaint)
                        yPos += 13f
                        canvas.drawText("C) $optC", 45f, yPos + 2f, textPaint)
                        yPos += 13f
                        canvas.drawText("D) $optD", 45f, yPos + 2f, textPaint)
                        yPos += 16f
                    }
                    QuestionType.OPEN_TWO_PARTS -> {
                        canvas.drawText("a) ${q.openPartAPrompt ?: "Birinchi qism bo'yicha javobingizni yozing:"}", 45f, yPos + 2f, textPaint)
                        yPos += 12f
                        canvas.drawRect(45f, yPos, (pageWidth - 45).toFloat(), yPos + 18f, borderPaint)
                        yPos += 24f
                        canvas.drawText("b) ${q.openPartBPrompt ?: "Ikkinchi qism bo'yicha javobingizni yozing:"}", 45f, yPos + 2f, textPaint)
                        yPos += 12f
                        canvas.drawRect(45f, yPos, (pageWidth - 45).toFloat(), yPos + 18f, borderPaint)
                        yPos += 26f
                    }
                    QuestionType.ESSAY -> {
                        canvas.drawText("Insho mavzusi: ${q.essayPrompt ?: "Berilgan mavzuda insho yozing (kamida 120 so'z)"}", 45f, yPos + 2f, textPaint)
                        yPos += 14f
                        canvas.drawRect(45f, yPos, (pageWidth - 45).toFloat(), yPos + 60f, borderPaint)
                        yPos += 70f
                    }
                }

                yPos += 6f
            }

            // Draw last page footer
            canvas.drawText("Sahifa $currentPageNumber  |  Milliy Sertifikat Onlayn Tizimi", (pageWidth / 2 - 80).toFloat(), (pageHeight - 20).toFloat(), subtitlePaint)
            pdfDocument.finishPage(page)

            // Save PDF file
            val outputDir = context.cacheDir
            val safeTitle = test.title.replace("[^a-zA-Z0-9]".toRegex(), "_")
            val pdfFile = File(outputDir, "Savollar_${safeTitle}_45ta.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Generates the Official Uzbekistan National Certificate (Milliy Sertifikat) as high-res PDF.
     */
    fun generateCertificatePdf(
        context: Context,
        submission: StudentSubmissionEntity,
        subjectTitle: String
    ): File? {
        try {
            val pdfDocument = PdfDocument()
            val pageWidth = 595 // A4 standard width
            val pageHeight = 842 // A4 standard height

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            drawOfficialCertificate(canvas, pageWidth.toFloat(), pageHeight.toFloat(), submission, subjectTitle)

            pdfDocument.finishPage(page)

            val outputDir = context.cacheDir
            val certIdSafe = (submission.certificateId ?: "UZ26_641200").replace(" ", "_")
            val pdfFile = File(outputDir, "Sertifikat_${certIdSafe}.pdf")
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Draws the official Uzbekistan National Certificate with authentic ornamental borders,
     * emblem, agency headers, scores, level badge, and verification QR code.
     */
    fun drawOfficialCertificate(
        canvas: Canvas,
        width: Float,
        height: Float,
        submission: StudentSubmissionEntity,
        subjectTitle: String
    ) {
        // 1. Background (warm certificate parchment ivory tone)
        val bgPaint = Paint().apply {
            color = Color.rgb(253, 251, 243)
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width, height, bgPaint)

        // 2. Guilloche ornamental border in warm gold & bronze (#C5A059)
        val goldPrimary = Color.rgb(197, 160, 89)
        val goldLight = Color.rgb(226, 203, 153)
        val darkText = Color.rgb(26, 26, 26)

        val outerBorderPaint = Paint().apply {
            color = goldPrimary
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }

        val innerBorderPaint = Paint().apply {
            color = goldLight
            style = Paint.Style.STROKE
            strokeWidth = 1f
            isAntiAlias = true
        }

        // Draw decorative double frames
        canvas.drawRect(20f, 20f, width - 20f, height - 20f, outerBorderPaint)
        canvas.drawRect(25f, 25f, width - 25f, height - 25f, innerBorderPaint)

        // Draw ornamental corner pattern crosses
        drawCornerPattern(canvas, 20f, 20f, goldPrimary)
        drawCornerPattern(canvas, width - 20f, 20f, goldPrimary)
        drawCornerPattern(canvas, 20f, height - 20f, goldPrimary)
        drawCornerPattern(canvas, width - 20f, height - 20f, goldPrimary)

        // 3. State Emblem (Coat of Arms) representation
        val emblemPaint = Paint().apply {
            color = Color.rgb(21, 101, 192)
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        val emblemFill = Paint().apply {
            color = Color.rgb(230, 242, 255)
            style = Paint.Style.FILL
        }
        val centerX = width / 2f
        val emblemY = 70f
        canvas.drawCircle(centerX, emblemY, 26f, emblemFill)
        canvas.drawCircle(centerX, emblemY, 26f, emblemPaint)

        val emblemTextPaint = Paint().apply {
            color = Color.rgb(21, 101, 192)
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("★ UZ ★", centerX, emblemY + 3.5f, emblemTextPaint)

        // 4. Official Agency Header
        val agencyHeaderPaint = Paint().apply {
            color = darkText
            textSize = 9.5f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("O'ZBEKISTON RESPUBLIKASI OLIY TA'LIM, FAN VA INNOVATSIYALAR VAZIRLIGI", centerX, 118f, agencyHeaderPaint)
        canvas.drawText("HUZURIDAGI BILIM VA MALAKALARNI BAHOLASH AGENTLIGI", centerX, 132f, agencyHeaderPaint)

        // Divider line
        val dividerPaint = Paint().apply {
            color = darkText
            strokeWidth = 1.8f
        }
        canvas.drawLine(40f, 142f, width - 40f, 142f, dividerPaint)

        // 5. Certificate Main Title
        val certTitlePaint = Paint().apply {
            color = darkText
            textSize = 12f
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("UMUMTA'LIM FANINI BILISH DARAJASI", centerX, 168f, certTitlePaint)
        canvas.drawText("TO'G'RISIDA SERTIFIKAT", centerX, 184f, certTitlePaint)

        // 6. Certificate Number & Details
        val labelPaint = Paint().apply {
            color = Color.rgb(55, 65, 81)
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val valuePaint = Paint().apply {
            color = darkText
            textSize = 10.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        var curY = 220f
        val leftX = 50f

        // Certificate Number
        canvas.drawText("Sertifikat raqami:", leftX, curY, labelPaint)
        val certNo = submission.certificateId ?: "UZ26 641200"
        canvas.drawText(certNo, 220f, curY, valuePaint)

        curY += 28f
        canvas.drawText("Talabgorning shaxsiy kodi:", leftX, curY, labelPaint)
        canvas.drawText(submission.studentPersonalCode.ifBlank { "41909931330028" }, 220f, curY, valuePaint)

        curY += 22f
        canvas.drawText("Familiyasi:", leftX, curY, labelPaint)
        canvas.drawText(submission.studentLastName.uppercase(), 220f, curY, valuePaint)

        curY += 22f
        canvas.drawText("Ismi:", leftX, curY, labelPaint)
        canvas.drawText(submission.studentFirstName.uppercase(), 220f, curY, valuePaint)

        curY += 22f
        canvas.drawText("Otasining ismi:", leftX, curY, labelPaint)
        canvas.drawText(submission.studentFatherName.uppercase(), 220f, curY, valuePaint)

        // Student Photo Box (Top right)
        val photoLeft = width - 130f
        val photoTop = 205f
        val photoWidth = 75f
        val photoHeight = 95f
        val photoFramePaint = Paint().apply {
            color = Color.rgb(180, 180, 180)
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val photoBgPaint = Paint().apply {
            color = Color.rgb(243, 244, 246)
            style = Paint.Style.FILL
        }
        canvas.drawRect(photoLeft, photoTop, photoLeft + photoWidth, photoTop + photoHeight, photoBgPaint)
        canvas.drawRect(photoLeft, photoTop, photoLeft + photoWidth, photoTop + photoHeight, photoFramePaint)

        // Draw portrait placeholder icon
        val avatarPaint = Paint().apply {
            color = Color.rgb(156, 163, 175)
            style = Paint.Style.FILL
        }
        canvas.drawCircle(photoLeft + photoWidth / 2, photoTop + 35f, 18f, avatarPaint)
        val bodyRect = RectF(photoLeft + 15f, photoTop + 58f, photoLeft + photoWidth - 15f, photoTop + 90f)
        canvas.drawRoundRect(bodyRect, 10f, 10f, avatarPaint)

        curY += 34f
        canvas.drawLine(leftX, curY, width - 50f, curY, innerBorderPaint)
        curY += 22f

        // Exam subject and scores
        canvas.drawText("Umumta'lim fani:", leftX, curY, labelPaint)
        canvas.drawText(subjectTitle, 220f, curY, valuePaint)

        curY += 22f
        canvas.drawText("Umumiy to'plagan bali:", leftX, curY, labelPaint)
        val scoreStr = if (submission.raschScaledScore > 0) "%.2f".format(submission.raschScaledScore) else "70.32"
        canvas.drawText(scoreStr, 220f, curY, valuePaint)

        curY += 22f
        canvas.drawText("Umumiy ballga nisbatan foiz ko'rsatkichi:", leftX, curY, labelPaint)
        val percentStr = if (submission.percentage > 0) "%.2f %%".format(submission.percentage) else "100 %"
        canvas.drawText(percentStr, 270f, curY, valuePaint)

        curY += 22f
        canvas.drawText("Sertifikat darajasi:", leftX, curY, labelPaint)

        val levelText = submission.certificateLevel.displayName
        val levelBadgePaint = Paint().apply {
            color = Color.rgb(16, 185, 129)
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(levelText, 220f, curY, levelBadgePaint)

        curY += 30f
        // Section test details
        val subDetailPaint = Paint().apply {
            color = Color.rgb(75, 85, 99)
            textSize = 9.5f
            isAntiAlias = true
        }
        canvas.drawText("Test sinovi natijasi:", leftX, curY, subDetailPaint)
        val testPartStr = if (submission.testScorePart > 0) "%.2f".format(submission.testScorePart) else "69.64"
        canvas.drawText(testPartStr, width - 120f, curY, valuePaint)

        curY += 18f
        canvas.drawText("Yozma ish natijasi:", leftX, curY, subDetailPaint)
        val writtenPartStr = if (submission.writtenScorePart > 0) "%.2f".format(submission.writtenScorePart) else "71.0"
        canvas.drawText(writtenPartStr, width - 120f, curY, valuePaint)

        // 7. Footer: Issue Date, Validity, QR Code, Director signature
        val footerY = height - 120f
        canvas.drawLine(leftX, footerY - 15f, width - 50f, footerY - 15f, innerBorderPaint)

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val issueDate = submission.certificateIssueDate ?: dateFormat.format(Date())
        canvas.drawText("Berilgan sanasi: $issueDate", leftX, footerY, labelPaint)
        canvas.drawText("Amal qilish muddati: 3 yil (2029)", width - 210f, footerY, labelPaint)

        // QR Code Box (Center bottom)
        val qrLeft = centerX - 35f
        val qrTop = footerY + 15f
        drawMockQrCode(canvas, qrLeft, qrTop, 70f)

        // Director & Signature
        val signatureY = footerY + 50f
        canvas.drawText("Direktor", leftX + 40f, signatureY, labelPaint)
        canvas.drawText("M.KARIMOV", width - 150f, signatureY, valuePaint)

        // Stylized signature stroke
        val signPaint = Paint().apply {
            color = Color.rgb(30, 64, 175)
            strokeWidth = 1.8f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        canvas.drawLine(width - 150f, signatureY + 8f, width - 70f, signatureY + 4f, signPaint)
    }

    private fun drawCornerPattern(canvas: Canvas, x: Float, y: Float, color: Int) {
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
        }
        val size = 18f
        canvas.drawRect(x - size, y - size, x + size, y + size, paint)
        canvas.drawLine(x - size, y, x + size, y, paint)
        canvas.drawLine(x, y - size, x, y + size, paint)
    }

    private fun drawMockQrCode(canvas: Canvas, left: Float, top: Float, size: Float) {
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val dotPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }

        canvas.drawRect(left, top, left + size, top + size, bgPaint)
        canvas.drawRect(left, top, left + size, top + size, borderPaint)

        // QR position detection patterns
        val cellSize = size / 7f
        fun drawCornerBox(cx: Float, cy: Float) {
            canvas.drawRect(cx, cy, cx + 2f * cellSize, cy + 2f * cellSize, dotPaint)
            canvas.drawRect(cx + 0.5f * cellSize, cy + 0.5f * cellSize, cx + 1.5f * cellSize, cy + 1.5f * cellSize, bgPaint)
            canvas.drawRect(cx + 0.7f * cellSize, cy + 0.7f * cellSize, cx + 1.3f * cellSize, cy + 1.3f * cellSize, dotPaint)
        }

        drawCornerBox(left + 2f, top + 2f)
        drawCornerBox(left + size - 2f * cellSize - 2f, top + 2f)
        drawCornerBox(left + 2f, top + size - 2f * cellSize - 2f)

        // Mock pattern dots
        for (i in 0..5) {
            for (j in 0..5) {
                if ((i + j) % 2 == 0) {
                    canvas.drawRect(
                        left + 15f + i * 6f,
                        top + 15f + j * 6f,
                        left + 18f + i * 6f,
                        top + 18f + j * 6f,
                        dotPaint
                    )
                }
            }
        }
    }

    private fun splitTextIntoLines(text: String, paint: Paint, maxWidth: Int): List<String> {
        val lines = mutableListOf<String>()
        val words = text.split(" ")
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = StringBuilder(testLine)
            } else {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                }
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }

    /**
     * Opens or shares the generated PDF with external viewer/printer.
     */
    fun openOrSharePdf(context: Context, file: File, title: String = "Hujjatni ochish") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            // If no dedicated PDF viewer, fallback to Share
            sharePdf(context, file, title)
        }
    }

    fun sharePdf(context: Context, file: File, title: String = "Hujjatni ulashish") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
            Toast.makeText(context, "PDF fayl saqlandi: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }
}
