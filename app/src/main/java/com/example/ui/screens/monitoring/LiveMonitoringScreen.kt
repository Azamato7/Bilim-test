package com.example.ui.screens.monitoring

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Grading
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.data.model.QuestionType
import com.example.data.model.StudentAnswer
import com.example.data.model.SubmissionStatus
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BlueDark
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun LiveMonitoringScreen(
    test: TestSessionEntity,
    questions: List<QuestionEntity>,
    submissions: List<StudentSubmissionEntity>,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onFinishTest: () -> Unit,
    onIssueCertificates: () -> Unit,
    onGradeQuestion: (submissionId: String, qNum: Int, score: Double) -> Unit,
    onDownloadQuestionsPdf: () -> Unit
) {
    var gradingSubmission by remember { mutableStateOf<StudentSubmissionEntity?>(null) }
    var showIssueConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("live_monitoring_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = test.title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Test kodi: ${test.accessCode}  •  Fan: ${test.subject.titleUz}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions row (scrollable)
                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDownloadQuestionsPdf,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Savollar PDF", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onRefresh,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Yangilash", fontSize = 11.sp)
                    }

                    if (!test.isFinished) {
                        Button(
                            onClick = onFinishTest,
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("finish_test_session_button")
                        ) {
                            Icon(imageVector = Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Testni tugatish", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Button(
                        onClick = { showIssueConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (test.areCertificatesIssued) GreenDark else GreenSuccess
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("issue_certificates_button")
                    ) {
                        Icon(imageVector = Icons.Default.CardMembership, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (test.areCertificatesIssued) "Sertifikatlar berilgan ✓" else "Sertifikatlarni chiqarish",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Live Monitoring Stats Cards (2x2 Grid)
        item {
            val totalStudents = submissions.size
            val submittedCount = submissions.count { it.status == SubmissionStatus.SUBMITTED || it.status == SubmissionStatus.CERTIFIED || it.status == SubmissionStatus.GRADED }
            val inProgressCount = submissions.count { it.status == SubmissionStatus.IN_PROGRESS }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MonitoringStatCard(
                        title = "Jami talabgorlar",
                        value = "$totalStudents nafar",
                        subtitle = "Ro'yxatdan o'tganlar",
                        color = BluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MonitoringStatCard(
                        title = "Topshirganlar",
                        value = "$submittedCount nafar",
                        subtitle = "Javoblar yuborilgan",
                        color = GreenSuccess,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MonitoringStatCard(
                        title = "Jarayonda",
                        value = "$inProgressCount nafar",
                        subtitle = "Hozir ishlayotganlar",
                        color = AmberWarning,
                        modifier = Modifier.weight(1f)
                    )
                    MonitoringStatCard(
                        title = "Status",
                        value = if (test.areCertificatesIssued) "Berilgan" else if (test.isFinished) "Yakunlangan" else "Faol",
                        subtitle = if (test.areCertificatesIssued) "Rasch hisoblangan" else "Natijalar kutilmoqda",
                        color = if (test.areCertificatesIssued) GreenSuccess else PurpleAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Submissions Table / List
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Talabgorlar va topshirilgan ishlar ro'yxati",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (submissions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Hozircha hech bir talabgor ushbu testga ulanmagan.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            submissions.forEach { sub ->
                                StudentSubmissionRow(
                                    submission = sub,
                                    onGradeClick = { gradingSubmission = sub }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Teacher Grading Modal Dialog
    gradingSubmission?.let { sub ->
        TeacherGradingDialog(
            submission = sub,
            questions = questions,
            onDismiss = { gradingSubmission = null },
            onGradeQuestion = { qNum, score ->
                onGradeQuestion(sub.id, qNum, score)
            }
        )
    }

    // Issue Certificates Confirmation Dialog
    if (showIssueConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showIssueConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = GreenSuccess)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sertifikatlarni hisoblash va chiqarish", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("Tizim barcha ${submissions.size} nafar talabgor natijalarini Rasch modeli asosida hisoblab chiqadi:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Har bir savol qiyinlik darajasi (b_i) tahlil qilinadi.")
                    Text("• Talabgorlar qobiliyati (θ) va foiz ko'rsatkichlari aniqlanadi.")
                    Text("• Rasmiy sertifikat raqamlari (UZ26 ...) va darajalar (A+, A, B+, B, C+, C) beriladi.")
                    Text("• O'quvchilarga o'z kabinetlarida sertifikatlarni yuklab olish imkoni ochiladi.")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showIssueConfirmDialog = false
                        onIssueCertificates()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                    modifier = Modifier.testTag("confirm_issue_certs_dialog_button")
                ) {
                    Text("Ha, chiqarish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showIssueConfirmDialog = false }) {
                    Text("Bekor qilish")
                }
            }
        )
    }
}

@Composable
private fun MonitoringStatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StudentSubmissionRow(
    submission: StudentSubmissionEntity,
    onGradeClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = submission.studentName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = submission.studentName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (submission.certificateLevel.isPassing()) {
                            Surface(
                                color = GreenContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "${submission.certificateLevel.displayName} (${submission.percentage}%)",
                                    color = GreenDark,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "JSHSHIR: ${submission.studentPersonalCode}  •  Vaqt: ${submission.timeSpentSeconds / 60} daqiqa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onGradeClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(imageVector = Icons.Default.Grading, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Javoblarni baholash", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun TeacherGradingDialog(
    submission: StudentSubmissionEntity,
    questions: List<QuestionEntity>,
    onDismiss: () -> Unit,
    onGradeQuestion: (Int, Double) -> Unit
) {
    val answersMap = StudentAnswer.parseAnswersMap(submission.answersJson)
    val openAndEssayQuestions = questions.filter { it.type != QuestionType.CLOSED_ABCD }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("${submission.studentName} ishini baholash", fontWeight = FontWeight.Bold)
                Text(
                    "Ochiq va insho savollari bo'yicha ball qo'yish",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (openAndEssayQuestions.isEmpty()) {
                    Text(
                        "Ushbu testda ochiq yoki insho savollari yo'q (barcha savollar yopiq ABCD avtomatik baholanadi).",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    openAndEssayQuestions.forEach { q ->
                        val ans = answersMap[q.questionNumber]
                        val qNum = q.questionNumber

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "$qNum-savol (${if (q.type == QuestionType.ESSAY) "Insho" else "Ochiq savol"})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Savol: ${q.questionText}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                if (q.type == QuestionType.OPEN_TWO_PARTS) {
                                    Text("Talaba a-javobi: ${ans?.openAnswerA?.ifBlank { "Topshirilmagan" } ?: "Topshirilmagan"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text("Talaba b-javobi: ${ans?.openAnswerB?.ifBlank { "Topshirilmagan" } ?: "Topshirilmagan"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                } else {
                                    Text("Talaba inshosi:\n${ans?.essayText?.ifBlank { "Yozilmagan" } ?: "Yozilmagan"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val maxPts = if (q.type == QuestionType.ESSAY) 20.0 else 3.0
                                var currentScoreText by remember(ans?.scoreAwarded) {
                                    mutableStateOf((ans?.scoreAwarded ?: 0.0).toString())
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Ball (Maks: $maxPts):", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = currentScoreText,
                                        onValueChange = {
                                            currentScoreText = it
                                            val sc = it.toDoubleOrNull() ?: 0.0
                                            if (sc in 0.0..maxPts) {
                                                onGradeQuestion(qNum, sc)
                                            }
                                        },
                                        modifier = Modifier.width(80.dp),
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Baholashni yakunlash")
            }
        }
    )
}
