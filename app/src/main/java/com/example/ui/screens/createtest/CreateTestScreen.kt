package com.example.ui.screens.createtest

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entity.TestSessionEntity
import com.example.data.model.ExamSubject
import com.example.data.model.QuestionType
import com.example.data.repository.SeedDataGenerator
import com.example.ui.theme.BlueDark
import com.example.ui.theme.BlueLight
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTestScreen(
    onBackClick: () -> Unit,
    onSaveTest: (subject: ExamSubject, title: String, description: String, timeLimit: Int, questions: List<QuestionEntity>, onDone: (TestSessionEntity) -> Unit) -> Unit,
    onPreviewPdf: (TestSessionEntity) -> Unit
) {
    var selectedSubject by remember { mutableStateOf(ExamSubject.ONA_TILI) }
    var testTitle by remember { mutableStateOf("Ona tili - Test #13") }
    var testDesc by remember { mutableStateOf("Milliy sertifikat talablari asosidagi namunaviy test sinovi") }
    var timeLimitMinutes by remember { mutableIntStateOf(90) }
    var subjectDropdownExpanded by remember { mutableStateOf(false) }

    // 45 questions list in memory
    var questionsList by remember {
        mutableStateOf(SeedDataGenerator.generate45QuestionsForTest("temp_test", ExamSubject.ONA_TILI))
    }
    var activeQuestionIndex by remember { mutableIntStateOf(0) }

    var createdTestDialog by remember { mutableStateOf<TestSessionEntity?>(null) }

    // When subject changes, reload question structure
    fun updateSubject(newSubj: ExamSubject) {
        selectedSubject = newSubj
        testTitle = "${newSubj.titleUz} - Test #${(10..30).random()}"
        questionsList = SeedDataGenerator.generate45QuestionsForTest("temp_test", newSubj)
        activeQuestionIndex = 0
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 900.dp)
                .padding(16.dp)
                .testTag("create_test_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Orqaga")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Yangi test yaratish",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Milliy Sertifikat standarti (45 ta savol)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            onSaveTest(selectedSubject, testTitle, testDesc, timeLimitMinutes, questionsList) { created ->
                                createdTestDialog = created
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("save_test_button")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Saqlash", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Test Config Card (2 rows of 2 items each on compact screens)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1. Test parametrlari",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedButton(
                            onClick = {
                                questionsList = SeedDataGenerator.generate45QuestionsForTest("temp_test", selectedSubject)
                            },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Namuna bilan to'ldirish", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row 1: Subject + Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = subjectDropdownExpanded,
                            onExpandedChange = { subjectDropdownExpanded = !subjectDropdownExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedSubject.titleUz,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Umumta'lim fani") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = subjectDropdownExpanded,
                                onDismissRequest = { subjectDropdownExpanded = false }
                            ) {
                                ExamSubject.values().forEach { subj ->
                                    DropdownMenuItem(
                                        text = { Text(subj.titleUz) },
                                        onClick = {
                                            updateSubject(subj)
                                            subjectDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = testTitle,
                            onValueChange = { testTitle = it },
                            label = { Text("Test nomi") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row 2: 45 Questions (Locked) + Time Limit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = "45 ta savol (Standart)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Savollar soni") },
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = "Qat'iy 45 ta", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = "$timeLimitMinutes daqiqa",
                            onValueChange = {
                                val mins = it.filter { c -> c.isDigit() }.toIntOrNull() ?: 90
                                timeLimitMinutes = mins
                            },
                            label = { Text("Vaqt chegarasi") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subject Question Specification Alert Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (selectedSubject == ExamSubject.ONA_TILI || selectedSubject == ExamSubject.ENGLISH) {
                                    "Struktura: 1–35 yopiq (ABCD)  •  36–39 tanlash  •  40–44 ochiq (a va b)  •  45-savol: Insho"
                                } else {
                                    "Struktura: 1–35 yopiq (ABCD)  •  36–45 ochiq savollar (a va b bandlar)"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
        }

        // 45-Question Navigation Strip & Question Editor
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. Savollar muharriri (45 ta)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${activeQuestionIndex + 1} / 45",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1..45 Quick Navigator Strip
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(questionsList.size) { index ->
                            val q = questionsList[index]
                            val isSelected = index == activeQuestionIndex

                            val bg = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                q.type == QuestionType.ESSAY -> PurpleLight
                                q.type == QuestionType.OPEN_TWO_PARTS -> GreenContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            val fg = when {
                                isSelected -> Color.White
                                q.type == QuestionType.ESSAY -> PurpleAccent
                                q.type == QuestionType.OPEN_TWO_PARTS -> GreenDark
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .clickable { activeQuestionIndex = index }
                                    .testTag("question_pill_${index + 1}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = fg
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Current Question Form Editor
                    if (questionsList.isNotEmpty() && activeQuestionIndex in questionsList.indices) {
                        val currentQ = questionsList[activeQuestionIndex]

                        QuestionEditForm(
                            question = currentQ,
                            onUpdate = { updatedQ ->
                                val list = questionsList.toMutableList()
                                list[activeQuestionIndex] = updatedQ
                                questionsList = list
                            }
                        )
                    }
                }
            }
        }
    }

    // Test Created Success Dialog
    createdTestDialog?.let { test ->
        AlertDialog(
            onDismissRequest = { createdTestDialog = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Test muvaffaqiyatli yaratildi!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "O'quvchilar ushbu testga quyidagi maxsus kod orqali ulanishlari mumkin:",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = test.accessCode,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "45 ta savoldan iborat test barcha o'quvchilar topshirgandan so'ng Rasch modeli orqali baholanadi.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        createdTestDialog = null
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Bosh sahifaga qaytish")
                }
            }
        )
    }
}
}

@Composable
private fun QuestionEditForm(
    question: QuestionEntity,
    onUpdate: (QuestionEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Question Header & Type badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${question.questionNumber}-savol tahrirlash",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            val typeBadgeText = when (question.type) {
                QuestionType.CLOSED_ABCD -> "Yopiq (ABCD)"
                QuestionType.OPEN_TWO_PARTS -> "Ochiq (a va b)"
                QuestionType.ESSAY -> "Insho (Esse)"
            }
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = typeBadgeText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Question Text Input
        OutlinedTextField(
            value = question.questionText,
            onValueChange = { onUpdate(question.copy(questionText = it)) },
            label = { Text("Savol matni") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("question_text_input_${question.questionNumber}"),
            minLines = 2,
            shape = RoundedCornerShape(10.dp)
        )

        when (question.type) {
            QuestionType.CLOSED_ABCD -> {
                Text(
                    text = "Variantlar va to'g'ri javobni tanlang:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OptionInputRow(
                    optionLetter = "A",
                    value = question.optionA ?: "",
                    isCorrect = question.correctOption.equals("A", ignoreCase = true),
                    onValueChange = { onUpdate(question.copy(optionA = it)) },
                    onSelectCorrect = { onUpdate(question.copy(correctOption = "A")) }
                )
                OptionInputRow(
                    optionLetter = "B",
                    value = question.optionB ?: "",
                    isCorrect = question.correctOption.equals("B", ignoreCase = true),
                    onValueChange = { onUpdate(question.copy(optionB = it)) },
                    onSelectCorrect = { onUpdate(question.copy(correctOption = "B")) }
                )
                OptionInputRow(
                    optionLetter = "C",
                    value = question.optionC ?: "",
                    isCorrect = question.correctOption.equals("C", ignoreCase = true),
                    onValueChange = { onUpdate(question.copy(optionC = it)) },
                    onSelectCorrect = { onUpdate(question.copy(correctOption = "C")) }
                )
                OptionInputRow(
                    optionLetter = "D",
                    value = question.optionD ?: "",
                    isCorrect = question.correctOption.equals("D", ignoreCase = true),
                    onValueChange = { onUpdate(question.copy(optionD = it)) },
                    onSelectCorrect = { onUpdate(question.copy(correctOption = "D")) }
                )
            }

            QuestionType.OPEN_TWO_PARTS -> {
                Text(
                    text = "Ochiq savol tuzilmasi (O'quvchi o'z javobini yozadi):",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = question.openPartAPrompt ?: "",
                    onValueChange = { onUpdate(question.copy(openPartAPrompt = it)) },
                    label = { Text("a) 1-qism topshirig'i") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = question.correctAnswerA ?: "",
                    onValueChange = { onUpdate(question.copy(correctAnswerA = it)) },
                    label = { Text("a) Namunaviy to'g'ri kalit javob") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    value = question.openPartBPrompt ?: "",
                    onValueChange = { onUpdate(question.copy(openPartBPrompt = it)) },
                    label = { Text("b) 2-qism topshirig'i") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = question.correctAnswerB ?: "",
                    onValueChange = { onUpdate(question.copy(correctAnswerB = it)) },
                    label = { Text("b) Namunaviy to'g'ri kalit javob") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }

            QuestionType.ESSAY -> {
                Text(
                    text = "Insho (Esse) mavzusi va mezonlari:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = question.essayPrompt ?: "",
                    onValueChange = { onUpdate(question.copy(essayPrompt = it)) },
                    label = { Text("Insho mavzusi va talablari") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@Composable
private fun OptionInputRow(
    optionLetter: String,
    value: String,
    isCorrect: Boolean,
    onValueChange: (String) -> Unit,
    onSelectCorrect: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSelectCorrect,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = if (isCorrect) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = "To'g'ri javob",
                tint = if (isCorrect) GreenSuccess else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("$optionLetter varianti matni") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isCorrect) GreenSuccess else MaterialTheme.colorScheme.primary
            )
        )
    }
}
