package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.CertificateLevel
import com.example.data.model.ExamSubject
import com.example.data.model.QuestionType
import com.example.data.model.StudentAnswer
import com.example.data.model.SubmissionStatus
import com.example.data.model.UserRole
import com.example.data.repository.ExamRepository
import com.example.domain.pdf.PdfGeneratorService
import com.example.domain.rasch.RaschScoringEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

enum class AppScreen {
    AUTH,
    HOME,
    CREATE_TEST,
    STUDENT_EXAM,
    WAITING_RESULTS,
    TEST_RESULT,
    LIVE_MONITORING,
    CERTIFICATE_VIEW,
    STATISTICS,
    SETTINGS_SET_TAB,
    MY_TESTS,
    MY_RESULTS,
    MY_CERTIFICATES,
    HELP
}

data class AppUiSettings(
    val isDarkMode: Boolean = false,
    val showResultAfterTest: Boolean = true,
    val allowRevisitQuestions: Boolean = true,
    val autoSaveAnswers: Boolean = true,
    val language: String = "UZ" // "UZ", "RU", "EN"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExamRepository
    init {
        val db = AppDatabase.getDatabase(application)
        repository = ExamRepository(db)
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            // Auto login default user if available
            val users = repository.allUsers.firstOrNull()
            if (!users.isNullOrEmpty() && _currentUser.value == null) {
                _currentUser.value = users.first()
            }
        }
    }

    // Navigation & Current Screen
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Auth & User
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Global Settings (SetTab)
    private val _settings = MutableStateFlow(AppUiSettings())
    val settings: StateFlow<AppUiSettings> = _settings.asStateFlow()

    // All Tests flow
    val allTests: StateFlow<List<TestSessionEntity>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Submissions flow
    val allSubmissions: StateFlow<List<StudentSubmissionEntity>> = repository.allSubmissions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Selected Test & Questions
    private val _selectedTest = MutableStateFlow<TestSessionEntity?>(null)
    val selectedTest: StateFlow<TestSessionEntity?> = _selectedTest.asStateFlow()

    private val _testQuestions = MutableStateFlow<List<QuestionEntity>>(emptyList())
    val testQuestions: StateFlow<List<QuestionEntity>> = _testQuestions.asStateFlow()

    // Active Student Exam State
    private val _activeSubmission = MutableStateFlow<StudentSubmissionEntity?>(null)
    val activeSubmission: StateFlow<StudentSubmissionEntity?> = _activeSubmission.asStateFlow()

    private val _studentAnswers = MutableStateFlow<Map<Int, StudentAnswer>>(emptyMap())
    val studentAnswers: StateFlow<Map<Int, StudentAnswer>> = _studentAnswers.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0) // 0 to 44
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow(5400L) // default 90 mins
    val remainingTimeSeconds: StateFlow<Long> = _remainingTimeSeconds.asStateFlow()

    // Live Monitoring Submissions for Creator
    private val _monitoringSubmissions = MutableStateFlow<List<StudentSubmissionEntity>>(emptyList())
    val monitoringSubmissions: StateFlow<List<StudentSubmissionEntity>> = _monitoringSubmissions.asStateFlow()

    // Selected Certificate for Viewing
    private val _viewingSubmission = MutableStateFlow<StudentSubmissionEntity?>(null)
    val viewingSubmission: StateFlow<StudentSubmissionEntity?> = _viewingSubmission.asStateFlow()

    // Aliases for clear UI binding
    val activeTestSession: StateFlow<TestSessionEntity?> get() = _selectedTest
    val activeQuestions: StateFlow<List<QuestionEntity>> get() = _testQuestions
    val examRemainingSeconds: StateFlow<Long> get() = _remainingTimeSeconds
    val studentSubmission: StateFlow<StudentSubmissionEntity?> get() = _activeSubmission
    val activeTestSubmissions: StateFlow<List<StudentSubmissionEntity>> get() = _monitoringSubmissions
    val uiSettings: StateFlow<AppUiSettings> get() = _settings

    // Toast/Alert message
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    private var timerJob: Job? = null

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    // --- Authentication Actions ---
    fun loginWithEmail(email: String, pass: String, asRole: UserRole = UserRole.TEACHER_CREATOR) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
                _currentScreen.value = AppScreen.HOME
                showMessage("Xush kelibsiz, ${user.fullName}!")
            } else {
                // Auto create demo account with provided email
                val name = email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
                val newUser = UserEntity(
                    id = "user_${UUID.randomUUID()}",
                    fullName = name,
                    email = email,
                    role = asRole,
                    personalCode = "419099" + (10000000..99999999).random().toString()
                )
                repository.registerOrUpdateUser(newUser)
                _currentUser.value = newUser
                _currentScreen.value = AppScreen.HOME
                showMessage("Ro'yxatdan o'tdingiz: $name")
            }
        }
    }

    fun loginAsDemo(role: UserRole) {
        viewModelScope.launch {
            val users = repository.allUsers.firstOrNull() ?: emptyList()
            val target = if (role == UserRole.TEACHER_CREATOR) {
                users.firstOrNull { it.role == UserRole.TEACHER_CREATOR }
            } else {
                users.firstOrNull { it.role == UserRole.STUDENT }
            }
            if (target != null) {
                _currentUser.value = target
                _currentScreen.value = AppScreen.HOME
                showMessage("Kirildi: ${target.fullName} (${if (target.role == UserRole.TEACHER_CREATOR) "O'qituvchi" else "O'quvchi"})")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = AppScreen.AUTH
    }

    fun switchRole() {
        val user = _currentUser.value ?: return
        val newRole = if (user.role == UserRole.TEACHER_CREATOR) UserRole.STUDENT else UserRole.TEACHER_CREATOR
        val updated = user.copy(role = newRole)
        viewModelScope.launch {
            repository.updateUser(updated)
            _currentUser.value = updated
            showMessage("Rol o'zgartirildi: ${if (newRole == UserRole.TEACHER_CREATOR) "O'qituvchi (Creator)" else "O'quvchi (Student)"}")
        }
    }

    // --- Settings (SetTab) Actions ---
    fun updateProfile(fullName: String, email: String, phone: String) {
        val user = _currentUser.value ?: return
        val parts = fullName.trim().split(" ")
        val last = parts.getOrNull(0) ?: user.lastName
        val first = parts.getOrNull(1) ?: user.firstName
        val updated = user.copy(
            fullName = fullName,
            email = email,
            phone = phone,
            lastName = last,
            firstName = first
        )
        viewModelScope.launch {
            repository.updateUser(updated)
            _currentUser.value = updated
            showMessage("Ma'lumotlar muvaffaqiyatli saqlandi!")
        }
    }

    fun updateSettings(
        isDarkMode: Boolean = _settings.value.isDarkMode,
        showResult: Boolean = _settings.value.showResultAfterTest,
        allowRevisit: Boolean = _settings.value.allowRevisitQuestions,
        autoSave: Boolean = _settings.value.autoSaveAnswers,
        language: String = _settings.value.language
    ) {
        _settings.value = AppUiSettings(
            isDarkMode = isDarkMode,
            showResultAfterTest = showResult,
            allowRevisitQuestions = allowRevisit,
            autoSaveAnswers = autoSave,
            language = language
        )
    }

    // --- Test Creation Actions ---
    fun createNewTest(
        subject: ExamSubject,
        title: String,
        description: String,
        timeLimitMinutes: Int,
        questions: List<QuestionEntity>? = null,
        onCreated: (TestSessionEntity) -> Unit
    ) {
        val creator = _currentUser.value ?: return
        viewModelScope.launch {
            val created = repository.createNewTestWith45Questions(
                creator = creator,
                subject = subject,
                title = title,
                description = description,
                timeLimitMinutes = timeLimitMinutes,
                questionsList = questions
            )
            _selectedTest.value = created
            _testQuestions.value = repository.getQuestionsForTestDirect(created.id)
            showMessage("Yangi 45 ta savolli test yaratildi! Kod: ${created.accessCode}")
            onCreated(created)
        }
    }

    // --- Student Exam Flow Actions ---
    fun joinTestByCode(code: String, onJoined: (Boolean) -> Unit) {
        viewModelScope.launch {
            val test = repository.getTestByAccessCode(code)
            if (test != null) {
                openTestForTaking(test)
                onJoined(true)
            } else {
                showMessage("Bunday kodli test topilmadi. Kodni tekshiring.")
                onJoined(false)
            }
        }
    }

    fun openTestForTaking(test: TestSessionEntity) {
        val student = _currentUser.value ?: return
        viewModelScope.launch {
            _selectedTest.value = test
            val questions = repository.getQuestionsForTestDirect(test.id)
            _testQuestions.value = questions

            val sub = repository.startOrResumeSubmission(test, student)
            _activeSubmission.value = sub
            _studentAnswers.value = StudentAnswer.parseAnswersMap(sub.answersJson)
            _currentQuestionIndex.value = 0

            // If already certified/submitted
            if (test.areCertificatesIssued && sub.status == SubmissionStatus.CERTIFIED) {
                _viewingSubmission.value = sub
                _currentScreen.value = AppScreen.TEST_RESULT
                return@launch
            } else if (sub.status == SubmissionStatus.SUBMITTED || sub.status == SubmissionStatus.GRADED) {
                _currentScreen.value = AppScreen.WAITING_RESULTS
                return@launch
            }

            // Start countdown timer
            startTimer(test.timeLimitMinutes * 60L - sub.timeSpentSeconds)
            _currentScreen.value = AppScreen.STUDENT_EXAM
        }
    }

    private fun startTimer(durationSeconds: Long) {
        timerJob?.cancel()
        _remainingTimeSeconds.value = durationSeconds.coerceAtLeast(0)
        timerJob = viewModelScope.launch {
            while (_remainingTimeSeconds.value > 0) {
                delay(1000)
                _remainingTimeSeconds.value--
                // Auto save progress every 30s
                if (_remainingTimeSeconds.value % 30 == 0L) {
                    saveCurrentAnswersProgress()
                }
            }
            // Auto submit on time out
            submitStudentExam()
        }
    }

    fun selectQuestionIndex(index: Int) {
        if (index in 0 until _testQuestions.value.size) {
            _currentQuestionIndex.value = index
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _testQuestions.value.size - 1) {
            _currentQuestionIndex.value++
        }
    }

    fun prevQuestion() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value--
        }
    }

    fun answerClosedQuestion(qNum: Int, option: String) {
        val map = _studentAnswers.value.toMutableMap()
        val existing = map[qNum] ?: StudentAnswer(questionNumber = qNum)
        map[qNum] = existing.copy(selectedOption = option)
        _studentAnswers.value = map
        if (_settings.value.autoSaveAnswers) {
            saveCurrentAnswersProgress()
        }
    }

    fun answerOpenQuestion(qNum: Int, partA: String, partB: String) {
        val map = _studentAnswers.value.toMutableMap()
        val existing = map[qNum] ?: StudentAnswer(questionNumber = qNum)
        map[qNum] = existing.copy(openAnswerA = partA, openAnswerB = partB)
        _studentAnswers.value = map
        if (_settings.value.autoSaveAnswers) {
            saveCurrentAnswersProgress()
        }
    }

    fun answerEssayQuestion(qNum: Int, text: String) {
        val map = _studentAnswers.value.toMutableMap()
        val existing = map[qNum] ?: StudentAnswer(questionNumber = qNum)
        map[qNum] = existing.copy(essayText = text)
        _studentAnswers.value = map
        if (_settings.value.autoSaveAnswers) {
            saveCurrentAnswersProgress()
        }
    }

    fun toggleMarkForReview(qNum: Int) {
        val map = _studentAnswers.value.toMutableMap()
        val existing = map[qNum] ?: StudentAnswer(questionNumber = qNum)
        map[qNum] = existing.copy(isMarkedForReview = !existing.isMarkedForReview)
        _studentAnswers.value = map
    }

    private fun saveCurrentAnswersProgress() {
        val sub = _activeSubmission.value ?: return
        val test = _selectedTest.value ?: return
        val timeSpent = (test.timeLimitMinutes * 60L) - _remainingTimeSeconds.value
        viewModelScope.launch {
            _studentAnswers.value.forEach { (qNum, ans) ->
                repository.autoSaveStudentAnswer(sub.id, qNum, ans, timeSpent.coerceAtLeast(0))
            }
        }
    }

    fun submitStudentExam() {
        timerJob?.cancel()
        val sub = _activeSubmission.value ?: return
        val test = _selectedTest.value ?: return
        val timeSpent = (test.timeLimitMinutes * 60L) - _remainingTimeSeconds.value

        viewModelScope.launch {
            saveCurrentAnswersProgress()
            repository.submitTest(sub.id, timeSpent.coerceAtLeast(0))

            // Refresh test
            val updatedTest = repository.getTestByIdDirect(test.id)
            if (updatedTest != null && updatedTest.areCertificatesIssued) {
                val updatedSub = repository.getSubmissionByIdDirect(sub.id)
                _viewingSubmission.value = updatedSub
                _currentScreen.value = AppScreen.TEST_RESULT
            } else {
                _currentScreen.value = AppScreen.WAITING_RESULTS
            }
            showMessage("Test muvaffaqiyatli topshirildi!")
        }
    }

    // --- Live Monitoring & Creator Management ---
    fun openMonitoringForTest(test: TestSessionEntity) {
        viewModelScope.launch {
            _selectedTest.value = test
            val questions = repository.getQuestionsForTestDirect(test.id)
            _testQuestions.value = questions
            val subs = repository.getSubmissionsForTestDirect(test.id)
            _monitoringSubmissions.value = subs
            _currentScreen.value = AppScreen.LIVE_MONITORING
        }
    }

    fun refreshMonitoring() {
        val test = _selectedTest.value ?: return
        viewModelScope.launch {
            val subs = repository.getSubmissionsForTestDirect(test.id)
            _monitoringSubmissions.value = subs
            val updatedTest = repository.getTestByIdDirect(test.id)
            if (updatedTest != null) _selectedTest.value = updatedTest
        }
    }

    fun gradeStudentQuestion(submissionId: String, qNum: Int, score: Double) {
        viewModelScope.launch {
            repository.updateTeacherGrading(submissionId, qNum, score)
            refreshMonitoring()
            showMessage("Baholandi: $score ball")
        }
    }

    fun finishTest() {
        val test = _selectedTest.value ?: return
        viewModelScope.launch {
            repository.finishTestSession(test.id)
            _selectedTest.value = test.copy(isFinished = true)
            refreshMonitoring()
            showMessage("Test yakunlandi. Endi sertifikatlarni chiqarishingiz mumkin!")
        }
    }

    fun issueCertificates() {
        val test = _selectedTest.value ?: return
        viewModelScope.launch {
            repository.finishTestSession(test.id)
            repository.issueCertificatesWithRaschModel(test.id)
            _selectedTest.value = test.copy(isFinished = true, areCertificatesIssued = true)
            refreshMonitoring()
            showMessage("Rasch modeli bo'yicha baholandi va barcha talabgorlarga rasmiy sertifikatlar taqdim etildi!")
        }
    }

    // --- Certificate & PDF Export Actions ---
    fun viewCertificate(sub: StudentSubmissionEntity) {
        _viewingSubmission.value = sub
        _currentScreen.value = AppScreen.CERTIFICATE_VIEW
    }

    fun downloadQuestionsPdf(test: TestSessionEntity, onFileReady: (File?) -> Unit) {
        viewModelScope.launch {
            val questions = repository.getQuestionsForTestDirect(test.id)
            val file = PdfGeneratorService.generateQuestionsPdf(getApplication(), test, questions)
            onFileReady(file)
        }
    }

    fun loginUser(email: String, role: UserRole, fullName: String? = null) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
                _currentScreen.value = AppScreen.HOME
                showMessage("Xush kelibsiz, ${user.fullName}!")
            } else {
                val name = fullName?.takeIf { it.isNotBlank() }
                    ?: email.substringBefore("@").replace(".", " ").replaceFirstChar { it.uppercase() }
                val newUser = UserEntity(
                    id = "user_${UUID.randomUUID()}",
                    fullName = name,
                    email = email,
                    role = role,
                    personalCode = "419099" + (10000000..99999999).random().toString()
                )
                repository.registerOrUpdateUser(newUser)
                _currentUser.value = newUser
                _currentScreen.value = AppScreen.HOME
                showMessage("Ro'yxatdan o'tdingiz: $name")
            }
        }
    }

    fun openLiveMonitoring(test: TestSessionEntity) = openMonitoringForTest(test)
    fun startStudentExam(test: TestSessionEntity) = openTestForTaking(test)

    fun openTestResult(test: TestSessionEntity) {
        val student = _currentUser.value ?: return
        viewModelScope.launch {
            _selectedTest.value = test
            val sub = repository.getSubmissionDirect(test.id, student.id)
            if (sub != null) {
                _viewingSubmission.value = sub
                _activeSubmission.value = sub
                _currentScreen.value = AppScreen.TEST_RESULT
            } else {
                openTestForTaking(test)
            }
        }
    }

    fun generateQuestionsPdf(test: TestSessionEntity): File? {
        val questions = kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.IO) {
            repository.getQuestionsForTestDirect(test.id)
        }
        return PdfGeneratorService.generateQuestionsPdf(getApplication(), test, questions)
    }

    fun generateCertificatePdf(sub: StudentSubmissionEntity, subjectTitle: String): File? {
        return PdfGeneratorService.generateCertificatePdf(getApplication(), sub, subjectTitle)
    }

    fun refreshResults() = refreshMonitoring()
    fun finishTestSession(testId: String) = finishTest()
    fun issueCertificatesForTest(testId: String) = issueCertificates()
    fun saveAnswerClosed(qNum: Int, opt: String) = answerClosedQuestion(qNum, opt)
    fun saveAnswerOpen(qNum: Int, a: String, b: String) = answerOpenQuestion(qNum, a, b)
    fun saveAnswerEssay(qNum: Int, text: String) = answerEssayQuestion(qNum, text)
    fun toggleBookmark(qNum: Int) = toggleMarkForReview(qNum)
    fun updateUiSettings(isDark: Boolean, showRes: Boolean, allowRev: Boolean, autoSave: Boolean, lang: String) =
        updateSettings(isDark, showRes, allowRev, autoSave, lang)
    fun openCertificateForSubmission(sub: StudentSubmissionEntity) = viewCertificate(sub)
}
