package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.CertificateLevel
import com.example.data.model.SubmissionStatus

@Entity(
    tableName = "student_submissions",
    indices = [Index(value = ["testId", "studentId"], unique = true)]
)
data class StudentSubmissionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val studentId: String,
    val studentName: String,
    val studentLastName: String = "ABDUQODIROV",
    val studentFirstName: String = "AZIZBEK",
    val studentFatherName: String = "ALISHER O'G'LI",
    val studentPersonalCode: String = "41909931330028",
    val studentAvatarUrl: String? = null,
    val status: SubmissionStatus = SubmissionStatus.NOT_STARTED,
    val startedAt: Long = System.currentTimeMillis(),
    val submittedAt: Long? = null,
    val timeSpentSeconds: Long = 0L,
    val answersJson: String = "{}", // Map of questionNumber -> answer payload
    val closedCorrectCount: Int = 0,
    val closedTotalCount: Int = 36,
    val openScore: Double = 0.0,
    val essayScore: Double = 0.0,
    val rawTotalScore: Double = 0.0,
    val raschScaledScore: Double = 0.0,
    val percentage: Double = 0.0,
    val certificateLevel: CertificateLevel = CertificateLevel.NONE,
    val rankPosition: Int = 1,
    val totalParticipants: Int = 1,
    val certificateId: String? = null,
    val certificateIssueDate: String? = null,
    val testScorePart: Double = 0.0,
    val writtenScorePart: Double = 0.0
)
