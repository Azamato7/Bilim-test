package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.StudentAnswer
import com.example.data.model.UserRole
import com.example.ui.components.AppMobileBottomBar
import com.example.ui.components.AppSidebar
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.certificate.CertificateViewScreen
import com.example.ui.screens.certificates.CertificatesListScreen
import com.example.ui.screens.createtest.CreateTestScreen
import com.example.ui.screens.help.HelpScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.monitoring.LiveMonitoringScreen
import com.example.ui.screens.mytests.MyTestsScreen
import com.example.ui.screens.settings.SetTabScreen
import com.example.ui.screens.statistics.StatistikaScreen
import com.example.ui.screens.testtaking.StudentTestScreen
import com.example.ui.screens.testtaking.TestResultScreen
import com.example.ui.screens.testtaking.WaitingResultsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppUiSettings
import com.example.ui.viewmodel.MainViewModel
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = viewModel()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentUser by viewModel.currentUser.collectAsState()
            val allTests by viewModel.allTests.collectAsState()
            val activeTest by viewModel.activeTestSession.collectAsState()
            val questions by viewModel.activeQuestions.collectAsState()
            val currentQIndex by viewModel.currentQuestionIndex.collectAsState()
            val studentAnswers by viewModel.studentAnswers.collectAsState()
            val remainingSeconds by viewModel.examRemainingSeconds.collectAsState()
            val studentSubmission by viewModel.studentSubmission.collectAsState()
            val activeSubmissions by viewModel.activeTestSubmissions.collectAsState()
            val allSubmissions by viewModel.allSubmissions.collectAsState()
            val settings by viewModel.uiSettings.collectAsState()

            MyApplicationTheme(darkTheme = settings.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (currentScreen == AppScreen.AUTH || currentUser == null) {
                        AuthScreen(
                            onLogin = { email, pass, role ->
                                viewModel.loginWithEmail(email, pass, role)
                            },
                            onDemoLogin = { role ->
                                viewModel.loginAsDemo(role)
                            }
                        )
                    } else {
                        MainAppContent(
                            currentScreen = currentScreen,
                            viewModel = viewModel,
                            currentUser = currentUser,
                            allTests = allTests,
                            activeTest = activeTest,
                            questions = questions,
                            currentQIndex = currentQIndex,
                            studentAnswers = studentAnswers,
                            remainingSeconds = remainingSeconds,
                            studentSubmission = studentSubmission,
                            activeSubmissions = activeSubmissions,
                            allSubmissions = allSubmissions,
                            settings = settings,
                            onOpenPdfFile = { file -> openPdfFile(file) },
                            onSharePdfFile = { file -> sharePdfFile(file) }
                        )
                    }
                }
            }
        }
    }

    private fun openPdfFile(file: File?) {
        if (file == null) {
            Toast.makeText(this, "PDF fayl yaratilmadi", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "PDF fayl saqlandi: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }

    private fun sharePdfFile(file: File?) {
        if (file == null) {
            Toast.makeText(this, "PDF fayl yaratilmadi", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Sertifikatni ulashish"))
        } catch (e: Exception) {
            Toast.makeText(this, "Ulashishda xatolik: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun MainAppContent(
    currentScreen: AppScreen,
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    allTests: List<TestSessionEntity>,
    activeTest: TestSessionEntity?,
    questions: List<QuestionEntity>,
    currentQIndex: Int,
    studentAnswers: Map<Int, StudentAnswer>,
    remainingSeconds: Long,
    studentSubmission: StudentSubmissionEntity?,
    activeSubmissions: List<StudentSubmissionEntity>,
    allSubmissions: List<StudentSubmissionEntity>,
    settings: AppUiSettings,
    onOpenPdfFile: (File?) -> Unit,
    onSharePdfFile: (File?) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 768.dp
        val isExamTaking = currentScreen == AppScreen.STUDENT_EXAM

        if (isWide && !isExamTaking) {
            // Desktop / Tablet layout with side Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                AppSidebar(
                    currentScreen = currentScreen,
                    currentUser = currentUser,
                    onNavigate = { screen -> viewModel.navigateTo(screen) },
                    onLogout = { viewModel.logout() },
                    onSwitchRole = { viewModel.switchRole() }
                )

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    ScreenRouter(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        currentUser = currentUser,
                        allTests = allTests,
                        activeTest = activeTest,
                        questions = questions,
                        currentQIndex = currentQIndex,
                        studentAnswers = studentAnswers,
                        remainingSeconds = remainingSeconds,
                        studentSubmission = studentSubmission,
                        activeSubmissions = activeSubmissions,
                        allSubmissions = allSubmissions,
                        settings = settings,
                        onOpenPdfFile = onOpenPdfFile,
                        onSharePdfFile = onSharePdfFile
                    )
                }
            }
        } else {
            // Mobile layout with bottom navigation bar (when not in exam)
            Scaffold(
                bottomBar = {
                    if (!isExamTaking) {
                        AppMobileBottomBar(
                            currentScreen = currentScreen,
                            onNavigate = { screen -> viewModel.navigateTo(screen) }
                        )
                    }
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    ScreenRouter(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        currentUser = currentUser,
                        allTests = allTests,
                        activeTest = activeTest,
                        questions = questions,
                        currentQIndex = currentQIndex,
                        studentAnswers = studentAnswers,
                        remainingSeconds = remainingSeconds,
                        studentSubmission = studentSubmission,
                        activeSubmissions = activeSubmissions,
                        allSubmissions = allSubmissions,
                        settings = settings,
                        onOpenPdfFile = onOpenPdfFile,
                        onSharePdfFile = onSharePdfFile
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenRouter(
    currentScreen: AppScreen,
    viewModel: MainViewModel,
    currentUser: UserEntity?,
    allTests: List<TestSessionEntity>,
    activeTest: TestSessionEntity?,
    questions: List<QuestionEntity>,
    currentQIndex: Int,
    studentAnswers: Map<Int, StudentAnswer>,
    remainingSeconds: Long,
    studentSubmission: StudentSubmissionEntity?,
    activeSubmissions: List<StudentSubmissionEntity>,
    allSubmissions: List<StudentSubmissionEntity>,
    settings: AppUiSettings,
    onOpenPdfFile: (File?) -> Unit,
    onSharePdfFile: (File?) -> Unit
) {
    when (currentScreen) {
        AppScreen.AUTH -> {
            AuthScreen(
                onLogin = { email, pass, role -> viewModel.loginWithEmail(email, pass, role) },
                onDemoLogin = { role -> viewModel.loginAsDemo(role) }
            )
        }

        AppScreen.HOME -> {
            HomeScreen(
                currentUser = currentUser,
                tests = allTests,
                onCreateTestClick = { viewModel.navigateTo(AppScreen.CREATE_TEST) },
                onSubjectClick = { subj ->
                    val test = allTests.firstOrNull { it.subject == subj }
                    if (test != null) {
                        if (currentUser?.role == UserRole.TEACHER_CREATOR) {
                            viewModel.openLiveMonitoring(test)
                        } else {
                            viewModel.startStudentExam(test)
                        }
                    } else {
                        viewModel.joinTestByCode(subj.id.take(2).uppercase() + "-13") { }
                    }
                },
                onEnterCodeClick = { code -> viewModel.joinTestByCode(code) { } },
                onTestClick = { test ->
                    if (test.areCertificatesIssued) {
                        viewModel.openTestResult(test)
                    } else {
                        viewModel.startStudentExam(test)
                    }
                },
                onManageTestClick = { test -> viewModel.openLiveMonitoring(test) },
                onDownloadPdfClick = { test ->
                    val file = viewModel.generateQuestionsPdf(test)
                    onOpenPdfFile(file)
                }
            )
        }

        AppScreen.CREATE_TEST -> {
            CreateTestScreen(
                onBackClick = { viewModel.navigateTo(AppScreen.HOME) },
                onSaveTest = { subject, title, desc, timeLimit, qList, onDone ->
                    viewModel.createNewTest(subject, title, desc, timeLimit, qList, onDone)
                },
                onPreviewPdf = { test ->
                    val file = viewModel.generateQuestionsPdf(test)
                    onOpenPdfFile(file)
                }
            )
        }

        AppScreen.STUDENT_EXAM -> {
            if (activeTest != null) {
                StudentTestScreen(
                    test = activeTest,
                    questions = questions,
                    currentIndex = currentQIndex,
                    answers = studentAnswers,
                    remainingSeconds = remainingSeconds,
                    onSelectQuestion = { idx -> viewModel.selectQuestionIndex(idx) },
                    onNext = { viewModel.nextQuestion() },
                    onPrev = { viewModel.prevQuestion() },
                    onAnswerClosed = { qNum, opt -> viewModel.saveAnswerClosed(qNum, opt) },
                    onAnswerOpen = { qNum, a, b -> viewModel.saveAnswerOpen(qNum, a, b) },
                    onAnswerEssay = { qNum, text -> viewModel.saveAnswerEssay(qNum, text) },
                    onToggleBookmark = { qNum -> viewModel.toggleBookmark(qNum) },
                    onSubmitExam = { viewModel.submitStudentExam() },
                    onDownloadPdf = {
                        val file = viewModel.generateQuestionsPdf(activeTest)
                        onOpenPdfFile(file)
                    }
                )
            }
        }

        AppScreen.WAITING_RESULTS -> {
            WaitingResultsScreen(
                test = activeTest,
                onRefresh = { viewModel.refreshResults() },
                onHomeClick = { viewModel.navigateTo(AppScreen.HOME) }
            )
        }

        AppScreen.TEST_RESULT -> {
            if (studentSubmission != null) {
                TestResultScreen(
                    test = activeTest,
                    submission = studentSubmission,
                    onViewCertificate = { viewModel.navigateTo(AppScreen.CERTIFICATE_VIEW) },
                    onViewStatistics = { viewModel.navigateTo(AppScreen.STATISTICS) },
                    onHomeClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            }
        }

        AppScreen.LIVE_MONITORING -> {
            if (activeTest != null) {
                LiveMonitoringScreen(
                    test = activeTest,
                    questions = questions,
                    submissions = activeSubmissions,
                    onBack = { viewModel.navigateTo(AppScreen.HOME) },
                    onRefresh = { viewModel.refreshResults() },
                    onFinishTest = { viewModel.finishTestSession(activeTest.id) },
                    onIssueCertificates = { viewModel.issueCertificatesForTest(activeTest.id) },
                    onGradeQuestion = { subId, qNum, score ->
                        viewModel.gradeStudentQuestion(subId, qNum, score)
                    },
                    onDownloadQuestionsPdf = {
                        val file = viewModel.generateQuestionsPdf(activeTest)
                        onOpenPdfFile(file)
                    }
                )
            }
        }

        AppScreen.CERTIFICATE_VIEW -> {
            val sub = studentSubmission ?: allSubmissions.firstOrNull { it.certificateId != null }
            if (sub != null) {
                CertificateViewScreen(
                    submission = sub,
                    subjectTitle = activeTest?.subject?.titleUz ?: "Ona tili va adabiyot",
                    onBack = { viewModel.navigateTo(AppScreen.HOME) },
                    onDownloadPdf = {
                        val file = viewModel.generateCertificatePdf(sub, activeTest?.subject?.titleUz ?: "Ona tili va adabiyot")
                        onOpenPdfFile(file)
                    },
                    onSharePdf = {
                        val file = viewModel.generateCertificatePdf(sub, activeTest?.subject?.titleUz ?: "Ona tili va adabiyot")
                        onSharePdfFile(file)
                    }
                )
            }
        }

        AppScreen.STATISTICS -> {
            StatistikaScreen(
                submissions = allSubmissions,
                onDownloadPdf = {
                    if (activeTest != null) {
                        val file = viewModel.generateQuestionsPdf(activeTest)
                        onOpenPdfFile(file)
                    }
                }
            )
        }

        AppScreen.SETTINGS_SET_TAB -> {
            SetTabScreen(
                currentUser = currentUser,
                settings = settings,
                onUpdateProfile = { name, email, phone -> viewModel.updateProfile(name, email, phone) },
                onUpdateSettings = { isDark, showRes, allowRev, autoSave, lang ->
                    viewModel.updateUiSettings(isDark, showRes, allowRev, autoSave, lang)
                },
                onLogout = { viewModel.logout() }
            )
        }

        AppScreen.MY_TESTS -> {
            MyTestsScreen(
                currentUser = currentUser,
                tests = allTests,
                onCreateTestClick = { viewModel.navigateTo(AppScreen.CREATE_TEST) },
                onTestClick = { test ->
                    if (test.areCertificatesIssued) {
                        viewModel.openTestResult(test)
                    } else {
                        viewModel.startStudentExam(test)
                    }
                },
                onManageTestClick = { test -> viewModel.openLiveMonitoring(test) },
                onDownloadPdf = { test ->
                    val file = viewModel.generateQuestionsPdf(test)
                    onOpenPdfFile(file)
                }
            )
        }

        AppScreen.MY_RESULTS -> {
            val sub = studentSubmission ?: allSubmissions.firstOrNull()
            if (sub != null) {
                TestResultScreen(
                    test = allTests.firstOrNull { it.id == sub.testId } ?: activeTest,
                    submission = sub,
                    onViewCertificate = { viewModel.navigateTo(AppScreen.CERTIFICATE_VIEW) },
                    onViewStatistics = { viewModel.navigateTo(AppScreen.STATISTICS) },
                    onHomeClick = { viewModel.navigateTo(AppScreen.HOME) }
                )
            } else {
                HomeScreen(
                    currentUser = currentUser,
                    tests = allTests,
                    onCreateTestClick = { viewModel.navigateTo(AppScreen.CREATE_TEST) },
                    onSubjectClick = { subj -> viewModel.joinTestByCode(subj.id.take(2).uppercase() + "-13") { } },
                    onEnterCodeClick = { code -> viewModel.joinTestByCode(code) { } },
                    onTestClick = { test -> viewModel.startStudentExam(test) },
                    onManageTestClick = { test -> viewModel.openLiveMonitoring(test) },
                    onDownloadPdfClick = { test -> onOpenPdfFile(viewModel.generateQuestionsPdf(test)) }
                )
            }
        }

        AppScreen.MY_CERTIFICATES -> {
            CertificatesListScreen(
                submissions = allSubmissions,
                onViewCertificate = { sub ->
                    viewModel.openCertificateForSubmission(sub)
                },
                onDownloadPdf = { sub ->
                    val test = allTests.firstOrNull { it.id == sub.testId }
                    val file = viewModel.generateCertificatePdf(sub, test?.subject?.titleUz ?: "Ona tili va adabiyot")
                    onOpenPdfFile(file)
                }
            )
        }

        AppScreen.HELP -> {
            HelpScreen()
        }
    }
}
