package com.example.ui.screens.certificate

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.ui.theme.BlueDark
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GoldBorder
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900

@Composable
fun CertificateViewScreen(
    submission: StudentSubmissionEntity,
    subjectTitle: String = "Ona tili va adabiyot",
    onBack: () -> Unit,
    onDownloadPdf: () -> Unit,
    onSharePdf: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("certificate_view_screen")
    ) {
        // Top Toolbar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Milliy Sertifikat",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = submission.certificateId ?: "UZ26 641200",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onSharePdf,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("share_cert_button")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ulashish", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDownloadPdf,
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("download_cert_pdf_button")
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PDF yuklab olish", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Certificate Parchment Card
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            OfficialCertificateParchment(
                submission = submission,
                subjectTitle = subjectTitle
            )
        }
    }
}

@Composable
private fun OfficialCertificateParchment(
    submission: StudentSubmissionEntity,
    subjectTitle: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 640.dp)
            .shadow(8.dp, RoundedCornerShape(4.dp))
            .border(3.dp, GoldPrimary, RoundedCornerShape(4.dp))
            .border(5.dp, GoldBorder, RoundedCornerShape(2.dp)),
        color = Color(0xFFFDFBF5), // Parchment ivory tone
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Emblem & Header
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEBF5FF))
                    .border(2.dp, BluePrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "★ UZ ★",
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "O'ZBEKISTON RESPUBLIKASI OLIY TA'LIM, FAN VA INNOVATSIYALAR VAZIRLIGI\nHUZURIDAGI BILIM VA MALAKALARNI BAHOLASH AGENTLIGI",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    lineHeight = 16.sp
                ),
                color = Color(0xFF1F2937),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = Color(0xFF1F2937), thickness = 1.5.dp, modifier = Modifier.fillMaxWidth(0.9f))
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "UMUMTA'LIM FANINI BILISH DARAJASI\nTO'G'RISIDA SERTIFIKAT",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    lineHeight = 22.sp
                ),
                color = Color(0xFF111827),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Main Info Grid + Student Photo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CertFieldRow(label = "Sertifikat raqami:", value = submission.certificateId ?: "UZ26 641200", isBold = true)
                    CertFieldRow(label = "Talabgorning shaxsiy kodi:", value = submission.studentPersonalCode.ifBlank { "41909931330028" })
                    CertFieldRow(label = "Familiyasi:", value = submission.studentLastName.uppercase(), isBold = true)
                    CertFieldRow(label = "Ismi:", value = submission.studentFirstName.uppercase(), isBold = true)
                    CertFieldRow(label = "Otasining ismi:", value = submission.studentFatherName.uppercase(), isBold = true)
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Student Photo Box
                Surface(
                    modifier = Modifier
                        .size(width = 95.dp, height = 120.dp)
                        .border(1.dp, Slate400, RoundedCornerShape(4.dp)),
                    color = Color(0xFFF3F4F6)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto",
                            tint = Slate400,
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "3x4 FOTO",
                            fontSize = 9.sp,
                            color = Slate500,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = GoldBorder, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(14.dp))

            // Subject & Scores
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CertFieldRow(label = "Umumta'lim fani:", value = subjectTitle, isBold = true)

                val scoreDisplay = if (submission.raschScaledScore > 0) "%.2f".format(submission.raschScaledScore) else "70.32"
                CertFieldRow(label = "Umumiy to'plagan bali:", value = scoreDisplay, isBold = true)

                val percentDisplay = if (submission.percentage > 0) "%.2f %%".format(submission.percentage) else "100 %"
                CertFieldRow(label = "Umumiy ballga nisbatan foiz ko'rsatkichi:", value = percentDisplay, isBold = true)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sertifikat darajasi:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate700,
                        modifier = Modifier.width(220.dp)
                    )
                    Surface(
                        color = GreenContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = submission.certificateLevel.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = GreenDark,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Test sinovi natijasi:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        modifier = Modifier.width(220.dp)
                    )
                    val testPart = if (submission.testScorePart > 0) "%.2f".format(submission.testScorePart) else "69.64"
                    Text(text = testPart, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Slate900)
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Yozma ish natijasi:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate600,
                        modifier = Modifier.width(220.dp)
                    )
                    val writtenPart = if (submission.writtenScorePart > 0) "%.2f".format(submission.writtenScorePart) else "71.0"
                    Text(text = writtenPart, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = Slate900)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Divider(color = GoldBorder, thickness = 1.dp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Issue Date, Validity, QR Code, Signature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Berilgan sanasi: ${submission.certificateIssueDate ?: "10.03.2026"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Text(
                        text = "Amal qilish muddati: 3 yil (2029)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Direktor",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Slate900
                    )
                }

                // QR Code Pattern box
                Surface(
                    modifier = Modifier
                        .size(70.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(2.dp)),
                    color = Color.White
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                        drawRect(Color.Black, Offset(0f, 0f), Size(18f, 18f))
                        drawRect(Color.White, Offset(4f, 4f), Size(10f, 10f))
                        drawRect(Color.Black, Offset(6f, 6f), Size(6f, 6f))

                        drawRect(Color.Black, Offset(size.width - 18f, 0f), Size(18f, 18f))
                        drawRect(Color.White, Offset(size.width - 14f, 4f), Size(10f, 10f))
                        drawRect(Color.Black, Offset(size.width - 12f, 6f), Size(6f, 6f))

                        drawRect(Color.Black, Offset(0f, size.height - 18f), Size(18f, 18f))
                        drawRect(Color.White, Offset(4f, size.height - 14f), Size(10f, 10f))
                        drawRect(Color.Black, Offset(6f, size.height - 12f), Size(6f, 6f))

                        drawCircle(Color.Black, 3f, Offset(size.width / 2f, size.height / 2f))
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "M.KARIMOV",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Slate900
                    )
                    Canvas(modifier = Modifier.size(width = 90.dp, height = 16.dp)) {
                        drawLine(
                            color = Color(0xFF1E40AF),
                            start = Offset(0f, 8f),
                            end = Offset(size.width, 4f),
                            strokeWidth = 2f
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CertFieldRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Slate700,
            modifier = Modifier.width(220.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
            ),
            color = Slate900
        )
    }
}
