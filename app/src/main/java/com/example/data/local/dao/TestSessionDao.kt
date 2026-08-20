package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TestSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestSessionDao {
    @Query("SELECT * FROM test_sessions ORDER BY createdAt DESC")
    fun getAllTests(): Flow<List<TestSessionEntity>>

    @Query("SELECT * FROM test_sessions WHERE id = :id")
    fun getTestById(id: String): Flow<TestSessionEntity?>

    @Query("SELECT * FROM test_sessions WHERE id = :id")
    suspend fun getTestByIdDirect(id: String): TestSessionEntity?

    @Query("SELECT * FROM test_sessions WHERE accessCode = :code LIMIT 1")
    suspend fun getTestByAccessCode(code: String): TestSessionEntity?

    @Query("SELECT * FROM test_sessions WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getTestsByCreator(creatorId: String): Flow<List<TestSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestSessionEntity)

    @Update
    suspend fun updateTest(test: TestSessionEntity)

    @Query("UPDATE test_sessions SET isFinished = 1, finishedAt = :finishedAt WHERE id = :id")
    suspend fun markTestFinished(id: String, finishedAt: Long = System.currentTimeMillis())

    @Query("UPDATE test_sessions SET areCertificatesIssued = 1 WHERE id = :id")
    suspend fun markCertificatesIssued(id: String)

    @Query("DELETE FROM test_sessions WHERE id = :id")
    suspend fun deleteTest(id: String)
}
