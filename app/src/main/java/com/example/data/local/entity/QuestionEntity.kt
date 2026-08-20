package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.QuestionType

@Entity(
    tableName = "questions",
    indices = [Index(value = ["testId", "questionNumber"], unique = true)]
)
data class QuestionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val questionNumber: Int,
    val type: QuestionType,
    val questionText: String,
    val imageUrl: String? = null,
    val optionA: String? = null,
    val optionB: String? = null,
    val optionC: String? = null,
    val optionD: String? = null,
    val correctOption: String? = null, // "A", "B", "C", "D"
    val openPartAPrompt: String? = null,
    val openPartBPrompt: String? = null,
    val correctAnswerA: String? = null,
    val correctAnswerB: String? = null,
    val essayPrompt: String? = null,
    val sectionName: String? = null,
    val maxScore: Double = 1.0
)
