package com.example.data.model

import androidx.annotation.DrawableRes

enum class UserRole {
    TEACHER_CREATOR,
    STUDENT
}

enum class ExamSubject(
    val id: String,
    val titleUz: String,
    val titleRu: String,
    val titleEn: String,
    val iconColor: Long,
    val hasEssay: Boolean
) {
    ONA_TILI(
        id = "ona_tili",
        titleUz = "Ona tili",
        titleRu = "Родной язык (Узбекский)",
        titleEn = "Mother Tongue (Uzbek)",
        iconColor = 0xFF10B981, // Emerald green
        hasEssay = true
    ),
    ENGLISH(
        id = "english",
        titleUz = "English",
        titleRu = "Английский язык",
        titleEn = "English Language",
        iconColor = 0xFF3B82F6, // Blue
        hasEssay = true
    ),
    MATEMATIKA(
        id = "matematika",
        titleUz = "Matematika",
        titleRu = "Математика",
        titleEn = "Mathematics",
        iconColor = 0xFF8B5CF6, // Purple
        hasEssay = false
    ),
    FIZIKA(
        id = "fizika",
        titleUz = "Fizika",
        titleRu = "Физика",
        titleEn = "Physics",
        iconColor = 0xFFF97316, // Orange
        hasEssay = false
    ),
    KIMYO(
        id = "kimyo",
        titleUz = "Kimyo",
        titleRu = "Химия",
        titleEn = "Chemistry",
        iconColor = 0xFFEC4899, // Pink
        hasEssay = false
    ),
    BIOLOGIYA(
        id = "biologiya",
        titleUz = "Biologiya",
        titleRu = "Биология",
        titleEn = "Biology",
        iconColor = 0xFF14B8A6, // Teal
        hasEssay = false
    ),
    TARIX(
        id = "tarix",
        titleUz = "Tarix",
        titleRu = "История",
        titleEn = "History",
        iconColor = 0xFFD97706, // Amber
        hasEssay = false
    ),
    GEOGRAFIYA(
        id = "geografiya",
        titleUz = "Geografiya",
        titleRu = "География",
        titleEn = "Geography",
        iconColor = 0xFF06B6D4, // Cyan
        hasEssay = false
    )
}

enum class QuestionType {
    CLOSED_ABCD,
    OPEN_TWO_PARTS,
    ESSAY
}

enum class SubmissionStatus {
    NOT_STARTED,
    IN_PROGRESS,
    SUBMITTED,
    GRADED,
    CERTIFIED
}

enum class CertificateLevel(val displayName: String, val badgeColor: Long, val ustamaPercent: Int) {
    A_PLUS("A+", 0xFF059669, 50),
    A("A", 0xFF10B981, 50),
    B_PLUS("B+", 0xFF3B82F6, 20),
    B("B", 0xFF6366F1, 20),
    C_PLUS("C+", 0xFFF59E0B, 0),
    C("C", 0xFFD97706, 0),
    NONE("Qoniqarsiz", 0xFFEF4444, 0);

    val salaryBonus: String
        get() = if (ustamaPercent > 0) "$ustamaPercent% ustama" else "Ustamasiz"

    fun isPassing(): Boolean = this != NONE
}
