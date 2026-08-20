package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.StudentSubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSubmissionDao {
    @Query("SELECT * FROM student_submissions ORDER BY startedAt DESC")
    fun getAllSubmissions(): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE testId = :testId ORDER BY rawTotalScore DESC")
    fun getSubmissionsForTest(testId: String): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE testId = :testId ORDER BY rawTotalScore DESC")
    suspend fun getSubmissionsForTestDirect(testId: String): List<StudentSubmissionEntity>

    @Query("SELECT * FROM student_submissions WHERE studentId = :studentId ORDER BY startedAt DESC")
    fun getSubmissionsForStudent(studentId: String): Flow<List<StudentSubmissionEntity>>

    @Query("SELECT * FROM student_submissions WHERE id = :id")
    fun getSubmissionById(id: String): Flow<StudentSubmissionEntity?>

    @Query("SELECT * FROM student_submissions WHERE id = :id")
    suspend fun getSubmissionByIdDirect(id: String): StudentSubmissionEntity?

    @Query("SELECT * FROM student_submissions WHERE testId = :testId AND studentId = :studentId LIMIT 1")
    fun getSubmission(testId: String, studentId: String): Flow<StudentSubmissionEntity?>

    @Query("SELECT * FROM student_submissions WHERE testId = :testId AND studentId = :studentId LIMIT 1")
    suspend fun getSubmissionDirect(testId: String, studentId: String): StudentSubmissionEntity?

    @Query("SELECT * FROM student_submissions WHERE certificateId IS NOT NULL AND studentId = :studentId ORDER BY startedAt DESC")
    fun getIssuedCertificatesForStudent(studentId: String): Flow<List<StudentSubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: StudentSubmissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<StudentSubmissionEntity>)

    @Update
    suspend fun updateSubmission(submission: StudentSubmissionEntity)

    @Query("DELETE FROM student_submissions WHERE id = :id")
    suspend fun deleteSubmission(id: String)
}
