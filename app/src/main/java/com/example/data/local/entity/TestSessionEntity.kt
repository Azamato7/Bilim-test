package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ExamSubject

@Entity(tableName = "test_sessions")
data class TestSessionEntity(
    @PrimaryKey val id: String,
    val accessCode: String,
    val creatorId: String,
    val creatorName: String,
    val subject: ExamSubject,
    val title: String,
    val description: String = "",
    val totalQuestions: Int = 45,
    val timeLimitMinutes: Int = 90,
    val isFinished: Boolean = false,
    val areCertificatesIssued: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val finishedAt: Long? = null
)
