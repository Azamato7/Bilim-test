package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.CertificateLevel
import com.example.data.model.ExamSubject
import com.example.data.model.StudentAnswer
import com.example.data.model.SubmissionStatus
import com.example.data.model.UserRole
import com.example.domain.rasch.RaschScoringEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class ExamRepository(private val database: AppDatabase) {

    private val userDao = database.userDao()
    private val testDao = database.testSessionDao()
    private val questionDao = database.questionDao()
    private val submissionDao = database.studentSubmissionDao()

    val allTests: Flow<List<TestSessionEntity>> = testDao.getAllTests()
    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allSubmissions: Flow<List<StudentSubmissionEntity>> = submissionDao.getAllSubmissions()

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingUsers = userDao.getAllUsers().first()
        if (existingUsers.isEmpty()) {
            val users = SeedDataGenerator.getDefaultUsers()
            users.forEach { userDao.insertUser(it) }

            val tests = SeedDataGenerator.getDefaultTests()
            tests.forEach { test ->
                testDao.insertTest(test)
                val questions = SeedDataGenerator.generate45QuestionsForTest(test.id, test.subject)
                questionDao.insertQuestions(questions)

                val submissions = SeedDataGenerator.getSampleSubmissions(test.id)
                submissionDao.insertSubmissions(submissions)
            }
        }
    }

    // User & Auth
    fun getUser(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)

    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun registerOrUpdateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(userId: String) = withContext(Dispatchers.IO) {
        userDao.deleteUser(userId)
    }

    // Test Sessions
    fun getTestById(testId: String): Flow<TestSessionEntity?> = testDao.getTestById(testId)
    suspend fun getTestByIdDirect(testId: String): TestSessionEntity? = withContext(Dispatchers.IO) {
        testDao.getTestByIdDirect(testId)
    }

    suspend fun getTestByAccessCode(code: String): TestSessionEntity? = withContext(Dispatchers.IO) {
        testDao.getTestByAccessCode(code.trim().uppercase())
            ?: testDao.getTestByAccessCode(code.trim())
    }

    fun getTestsByCreator(creatorId: String): Flow<List<TestSessionEntity>> = testDao.getTestsByCreator(creatorId)

    suspend fun createNewTestWith45Questions(
        creator: UserEntity,
        subject: ExamSubject,
        title: String,
        description: String,
        timeLimitMinutes: Int,
        questionsList: List<QuestionEntity>? = null
    ): TestSessionEntity = withContext(Dispatchers.IO) {
        val testId = "test_${subject.id}_${System.currentTimeMillis()}"
        val cleanSubjCode = subject.name.take(3).uppercase()
        val randomDigits = (1000..9999).random()
        val accessCode = "$cleanSubjCode-$randomDigits"

        val testSession = TestSessionEntity(
            id = testId,
            accessCode = accessCode,
            creatorId = creator.id,
            creatorName = creator.fullName,
            subject = subject,
            title = title.ifBlank { "${subject.titleUz} - Test #${(1..50).random()}" },
            description = description,
            totalQuestions = 45,
            timeLimitMinutes = timeLimitMinutes,
            isFinished = false,
            areCertificatesIssued = false,
            createdAt = System.currentTimeMillis()
        )

        testDao.insertTest(testSession)

        val finalQuestions = questionsList ?: SeedDataGenerator.generate45QuestionsForTest(testId, subject)
        val adjustedQuestions = finalQuestions.mapIndexed { index, q ->
            q.copy(id = "${testId}_q${index + 1}", testId = testId, questionNumber = index + 1)
        }
        questionDao.insertQuestions(adjustedQuestions)

        testSession
    }

    suspend fun updateTestQuestions(testId: String, questions: List<QuestionEntity>) = withContext(Dispatchers.IO) {
        questionDao.deleteQuestionsForTest(testId)
        questionDao.insertQuestions(questions)
    }

    // Questions
    fun getQuestionsForTest(testId: String): Flow<List<QuestionEntity>> = questionDao.getQuestionsForTest(testId)

    suspend fun getQuestionsForTestDirect(testId: String): List<QuestionEntity> = withContext(Dispatchers.IO) {
        questionDao.getQuestionsForTestDirect(testId)
    }

    // Submissions & Student taking
    fun getSubmissionsForTest(testId: String): Flow<List<StudentSubmissionEntity>> =
        submissionDao.getSubmissionsForTest(testId)

    suspend fun getSubmissionsForTestDirect(testId: String): List<StudentSubmissionEntity> = withContext(Dispatchers.IO) {
        submissionDao.getSubmissionsForTestDirect(testId)
    }

    fun getSubmissionsForStudent(studentId: String): Flow<List<StudentSubmissionEntity>> =
        submissionDao.getSubmissionsForStudent(studentId)

    fun getSubmission(testId: String, studentId: String): Flow<StudentSubmissionEntity?> =
        submissionDao.getSubmission(testId, studentId)

    suspend fun getSubmissionDirect(testId: String, studentId: String): StudentSubmissionEntity? = withContext(Dispatchers.IO) {
        submissionDao.getSubmissionDirect(testId, studentId)
    }

    suspend fun getSubmissionByIdDirect(id: String): StudentSubmissionEntity? = withContext(Dispatchers.IO) {
        submissionDao.getSubmissionByIdDirect(id)
    }

    fun getIssuedCertificatesForStudent(studentId: String): Flow<List<StudentSubmissionEntity>> =
        submissionDao.getIssuedCertificatesForStudent(studentId)

    suspend fun startOrResumeSubmission(test: TestSessionEntity, student: UserEntity): StudentSubmissionEntity = withContext(Dispatchers.IO) {
        val existing = submissionDao.getSubmissionDirect(test.id, student.id)
        if (existing != null) {
            existing
        } else {
            val newSub = StudentSubmissionEntity(
                id = "sub_${test.id}_${student.id}",
                testId = test.id,
                studentId = student.id,
                studentName = student.fullName,
                studentLastName = student.lastName,
                studentFirstName = student.firstName,
                studentFatherName = student.fatherName,
                studentPersonalCode = student.personalCode,
                status = SubmissionStatus.IN_PROGRESS,
                startedAt = System.currentTimeMillis()
            )
            submissionDao.insertSubmission(newSub)
            newSub
        }
    }

    suspend fun autoSaveStudentAnswer(
        submissionId: String,
        questionNumber: Int,
        answer: StudentAnswer,
        timeSpentSeconds: Long
    ) = withContext(Dispatchers.IO) {
        val sub = submissionDao.getSubmissionByIdDirect(submissionId) ?: return@withContext
        val currentAnswers = StudentAnswer.parseAnswersMap(sub.answersJson).toMutableMap()
        currentAnswers[questionNumber] = answer
        val updated = sub.copy(
            answersJson = StudentAnswer.serializeAnswersMap(currentAnswers),
            timeSpentSeconds = timeSpentSeconds
        )
        submissionDao.updateSubmission(updated)
    }

    suspend fun submitTest(submissionId: String, timeSpentSeconds: Long) = withContext(Dispatchers.IO) {
        val sub = submissionDao.getSubmissionByIdDirect(submissionId) ?: return@withContext
        val updated = sub.copy(
            status = SubmissionStatus.SUBMITTED,
            submittedAt = System.currentTimeMillis(),
            timeSpentSeconds = timeSpentSeconds
        )
        submissionDao.updateSubmission(updated)
    }

    suspend fun updateTeacherGrading(
        submissionId: String,
        questionNumber: Int,
        scoreAwarded: Double
    ) = withContext(Dispatchers.IO) {
        val sub = submissionDao.getSubmissionByIdDirect(submissionId) ?: return@withContext
        val currentAnswers = StudentAnswer.parseAnswersMap(sub.answersJson).toMutableMap()
        val existingAns = currentAnswers[questionNumber] ?: StudentAnswer(questionNumber = questionNumber)
        currentAnswers[questionNumber] = existingAns.copy(
            scoreAwarded = scoreAwarded,
            isGradedByTeacher = true
        )
        val updated = sub.copy(
            answersJson = StudentAnswer.serializeAnswersMap(currentAnswers),
            status = SubmissionStatus.GRADED
        )
        submissionDao.updateSubmission(updated)
    }

    // Finish test and Issue Certificates with Rasch model
    suspend fun finishTestSession(testId: String) = withContext(Dispatchers.IO) {
        testDao.markTestFinished(testId, System.currentTimeMillis())
    }

    suspend fun issueCertificatesWithRaschModel(testId: String) = withContext(Dispatchers.IO) {
        val test = testDao.getTestByIdDirect(testId) ?: return@withContext
        val questions = questionDao.getQuestionsForTestDirect(testId)
        val submissions = submissionDao.getSubmissionsForTestDirect(testId)

        val evaluationResult = RaschScoringEngine.calculateAndIssueCertificates(
            questions = questions,
            submissions = submissions,
            testSubjectName = test.subject.titleUz
        )

        // Save all updated scored and certified submissions
        submissionDao.insertSubmissions(evaluationResult.updatedSubmissions)

        // Mark test session as certificates issued
        testDao.markCertificatesIssued(testId)
    }
}
