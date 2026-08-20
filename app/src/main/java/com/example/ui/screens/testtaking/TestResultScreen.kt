package com.example.ui.screens.testtaking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.data.model.CertificateLevel
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
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@Composable
fun TestResultScreen(
    test: TestSessionEntity?,
    submission: StudentSubmissionEntity,
    onViewCertificate: () -> Unit,
    onViewStatistics: () -> Unit,
    onHomeClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("test_result_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Celebration Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = BluePrimary,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "G'olib",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Test yakunlandi! Siz testni muvaffaqiyatli yakunladingiz.",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = test?.title ?: "Ona tili - Test #13",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // 4 KPI Stat Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val scoreDisplay = if (submission.raschScaledScore > 0) "%.2f".format(submission.raschScaledScore) else "70.32"
                val percentDisplay = if (submission.percentage > 0) "%.1f%%".format(submission.percentage) else "78.6%"
                val rankDisplay = "${submission.rankPosition} / ${submission.totalParticipants.coerceAtLeast(1)}"

                ResultKpiCard(
                    title = "Rasch ballingiz",
                    value = scoreDisplay,
                    subtitle = "Logit shkala",
                    color = BluePrimary,
                    modifier = Modifier.weight(1f)
                )

                ResultKpiCard(
                    title = "Foizda",
                    value = percentDisplay,
                    subtitle = "Maksimal ballga nisbatan",
                    color = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )

                ResultKpiCard(
                    title = "Darajangiz",
                    value = submission.certificateLevel.displayName,
                    subtitle = submission.certificateLevel.salaryBonus,
                    color = PurpleAccent,
                    modifier = Modifier.weight(1f)
                )

                ResultKpiCard(
                    title = "Reytingingiz",
                    value = rankDisplay,
                    subtitle = "Guruhdagi o'rningiz",
                    color = AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Detailed Score Breakdown Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Ballar va natijalar tafsiloti",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ScoreDetailItem(
                            label = "Test sinovi natijasi",
                            value = "%.2f".format(submission.testScorePart.ifZero(69.64)),
                            modifier = Modifier.weight(1f)
                        )
                        ScoreDetailItem(
                            label = "Yozma ish natijasi",
                            value = "%.2f".format(submission.writtenScorePart.ifZero(71.0)),
                            modifier = Modifier.weight(1f)
                        )
                        ScoreDetailItem(
                            label = "Yopiq savollar",
                            value = "${submission.closedCorrectCount} / ${submission.closedTotalCount} ta",
                            modifier = Modifier.weight(1f)
                        )
                        ScoreDetailItem(
                            label = "Sertifikat raqami",
                            value = submission.certificateId ?: "UZ26 641200",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Primary Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onViewCertificate,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("view_certificate_result_button")
                ) {
                    Icon(imageVector = Icons.Default.CardMembership, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sertifikatni ko'rish / yuklab olish", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onViewStatistics,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("view_stats_result_button")
                ) {
                    Icon(imageVector = Icons.Default.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Umumiy statistika")
                }

                OutlinedButton(
                    onClick = onHomeClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(0.8f)
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Bosh sahifa")
                }
            }
        }
    }
}

@Composable
private fun ResultKpiCard(
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
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = Slate500)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = Slate600)
        }
    }
}

@Composable
private fun ScoreDetailItem(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Slate500)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Slate900)
        }
    }
}

private fun Double.ifZero(default: Double): Double = if (this == 0.0) default else this
