package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CertificateLevel
import com.example.data.model.ExamSubject
import com.example.data.model.QuestionType
import com.example.data.model.SubmissionStatus
import com.example.data.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name

    @TypeConverter
    fun toUserRole(value: String): UserRole = runCatching { UserRole.valueOf(value) }.getOrDefault(UserRole.STUDENT)

    @TypeConverter
    fun fromExamSubject(value: ExamSubject): String = value.name

    @TypeConverter
    fun toExamSubject(value: String): ExamSubject = runCatching { ExamSubject.valueOf(value) }.getOrDefault(ExamSubject.ONA_TILI)

    @TypeConverter
    fun fromQuestionType(value: QuestionType): String = value.name

    @TypeConverter
    fun toQuestionType(value: String): QuestionType = runCatching { QuestionType.valueOf(value) }.getOrDefault(QuestionType.CLOSED_ABCD)

    @TypeConverter
    fun fromSubmissionStatus(value: SubmissionStatus): String = value.name

    @TypeConverter
    fun toSubmissionStatus(value: String): SubmissionStatus = runCatching { SubmissionStatus.valueOf(value) }.getOrDefault(SubmissionStatus.NOT_STARTED)

    @TypeConverter
    fun fromCertificateLevel(value: CertificateLevel): String = value.name

    @TypeConverter
    fun toCertificateLevel(value: String): CertificateLevel = runCatching { CertificateLevel.valueOf(value) }.getOrDefault(CertificateLevel.NONE)
}
