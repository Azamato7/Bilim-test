package com.example.data.repository

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
import java.util.UUID

object SeedDataGenerator {

    fun getDefaultUsers(): List<UserEntity> {
        return listOf(
            UserEntity(
                id = "user_azizbek",
                fullName = "Azizbek Abduqodirov",
                email = "example@gmail.com",
                phone = "+998 90 123 45 67",
                role = UserRole.TEACHER_CREATOR,
                personalCode = "41909931330028",
                lastName = "ABDUQODIROV",
                firstName = "AZIZBEK",
                fatherName = "ALISHER O'G'LI"
            ),
            UserEntity(
                id = "user_elmiraxon",
                fullName = "Elmiraxon Toirova",
                email = "elmiraxon@gmail.com",
                phone = "+998 93 456 78 90",
                role = UserRole.STUDENT,
                personalCode = "41909931330028",
                lastName = "TOIROVA",
                firstName = "ELMIRAXON",
                fatherName = "ABDIKAYUM QIZI"
            ),
            UserEntity(
                id = "user_sardor",
                fullName = "Sardor Pirmamatov",
                email = "sardor@gmail.com",
                phone = "+998 97 789 12 34",
                role = UserRole.STUDENT,
                personalCode = "51709096060074",
                lastName = "PIRMAMATOV",
                firstName = "SARDOR",
                fatherName = "UCHQUN O'G'LI"
            ),
            UserEntity(
                id = "user_farangiz",
                fullName = "Farangiz Zikrilloyeva",
                email = "farangiz@gmail.com",
                phone = "+998 99 321 65 47",
                role = UserRole.STUDENT,
                personalCode = "62209095360017",
                lastName = "ZIKRILLOYEVA",
                firstName = "FARANGIZ",
                fatherName = "FARRUXOVNA"
            ),
            UserEntity(
                id = "user_oybek",
                fullName = "Oybekjon Obidov",
                email = "oybek@gmail.com",
                phone = "+998 91 888 22 11",
                role = UserRole.STUDENT,
                personalCode = "50501096110033",
                lastName = "OBIDOV",
                firstName = "OYBEKJON",
                fatherName = "ODILJONOVICH"
            )
        )
    }

    fun getDefaultTests(): List<TestSessionEntity> {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        return listOf(
            TestSessionEntity(
                id = "test_onatili_13",
                accessCode = "OT-2024-0513",
                creatorId = "user_azizbek",
                creatorName = "Azizbek Abduqodirov",
                subject = ExamSubject.ONA_TILI,
                title = "Ona tili - Test #13",
                description = "Milliy Sertifikat talablari asosidagi 45 ta savolli namunaviy sinov testi (yopiq, ochiq va insho)",
                totalQuestions = 45,
                timeLimitMinutes = 90,
                isFinished = false,
                areCertificatesIssued = false,
                createdAt = now - oneDay
            ),
            TestSessionEntity(
                id = "test_onatili_12",
                accessCode = "OT-2024-0512",
                creatorId = "user_azizbek",
                creatorName = "Azizbek Abduqodirov",
                subject = ExamSubject.ONA_TILI,
                title = "Ona tili - Test #12",
                description = "O'zbek tili va adabiyoti fanidan to'liq baholangan va sertifikat berilgan test",
                totalQuestions = 45,
                timeLimitMinutes = 90,
                isFinished = true,
                areCertificatesIssued = true,
                createdAt = now - 5 * oneDay,
                finishedAt = now - 5 * oneDay + 5400000L
            ),
            TestSessionEntity(
                id = "test_english_7",
                accessCode = "EN-2024-0007",
                creatorId = "user_azizbek",
                creatorName = "Azizbek Abduqodirov",
                subject = ExamSubject.ENGLISH,
                title = "English - Test #7",
                description = "CEFR B2-C1 National Certificate mock assessment test with reading, grammar, and essay",
                totalQuestions = 45,
                timeLimitMinutes = 90,
                isFinished = true,
                areCertificatesIssued = true,
                createdAt = now - 6 * oneDay,
                finishedAt = now - 6 * oneDay + 5400000L
            ),
            TestSessionEntity(
                id = "test_math_9",
                accessCode = "MATH-2024-0009",
                creatorId = "user_azizbek",
                creatorName = "Azizbek Abduqodirov",
                subject = ExamSubject.MATEMATIKA,
                title = "Matematika - Test #9",
                description = "Algebra, geometriya va matematik analiz asoslari bo'yicha 45 ta savol",
                totalQuestions = 45,
                timeLimitMinutes = 120,
                isFinished = false,
                areCertificatesIssued = false,
                createdAt = now - 7 * oneDay
            ),
            TestSessionEntity(
                id = "test_fizika_5",
                accessCode = "FIZ-2024-0005",
                creatorId = "user_azizbek",
                creatorName = "Azizbek Abduqodirov",
                subject = ExamSubject.FIZIKA,
                title = "Fizika - Test #5",
                description = "Mexanika, termodinamika va elektrodinamika masalalari",
                totalQuestions = 45,
                timeLimitMinutes = 120,
                isFinished = false,
                areCertificatesIssued = false,
                createdAt = now - 8 * oneDay
            )
        )
    }

    fun generate45QuestionsForTest(testId: String, subject: ExamSubject): List<QuestionEntity> {
        val list = mutableListOf<QuestionEntity>()

        for (qNum in 1..45) {
            val qId = "${testId}_q$qNum"
            when (subject) {
                ExamSubject.ONA_TILI, ExamSubject.ENGLISH -> {
                    when {
                        qNum in 1..35 -> {
                            list.add(createSampleClosedQuestion(qId, testId, qNum, subject))
                        }
                        qNum in 36..39 -> {
                            // 36-39: Closed ABCD marked
                            list.add(createSampleSpecialClosedQuestion(qId, testId, qNum, subject))
                        }
                        qNum in 40..44 -> {
                            // 40-44: Open 2 parts (a and b)
                            list.add(createSampleOpenTwoPartsQuestion(qId, testId, qNum, subject))
                        }
                        qNum == 45 -> {
                            // 45: Essay
                            list.add(createSampleEssayQuestion(qId, testId, qNum, subject))
                        }
                    }
                }
                else -> {
                    // All other subjects: 1-35 closed, 36-45 open with two parts (a & b)
                    if (qNum in 1..35) {
                        list.add(createSampleClosedQuestion(qId, testId, qNum, subject))
                    } else {
                        list.add(createSampleOpenTwoPartsQuestion(qId, testId, qNum, subject))
                    }
                }
            }
        }
        return list
    }

    private fun createSampleClosedQuestion(id: String, testId: String, qNum: Int, subject: ExamSubject): QuestionEntity {
        val (text, a, b, c, d, correct) = when (subject) {
            ExamSubject.ONA_TILI -> when (qNum % 5) {
                0 -> Tuple6("Qaysi qatorda faqat yasama so'zlar berilgan?", "Guldor, bilim, suvsiz", "Kitob, qalam, daftar", "Oqish, ko'rish, borish", "Dengiz, daryo, ko'l", "A")
                1 -> Tuple6("Ushbu gapdagi ega qaysi so'z turkumi bilan ifodalangan: 'O'qigan o'zini o'tga ham, suvga ham urmaydi'?", "Fe'lning sifatdosh shakli", "Olmosh", "Otdan yasalgan sifat", "Harakat nomi", "A")
                2 -> Tuple6("Quyidagi gapda nechta so'z birikmasi mavjud: 'Bahorning iliq nafasi dalalarga yoyildi'?", "4 ta", "3 ta", "5 ta", "2 ta", "B")
                3 -> Tuple6("Sinonim so'zlar qatorini aniqlang:", "Go'zal, chiroyli, suluv", "Katta, ulkan, kichik", "Baland, past, chuqur", "Oq, qora, qizil", "A")
                else -> Tuple6("Qaysi javobda imloviy jihatdan to'g'ri yozilgan so'zlar berilgan?", "Taassurot, muddao, mutolaa", "Tasurot, muddo, mutola", "Taassurot, muddo, mutola", "Tasurot, muddao, mutolaa", "A")
            }
            ExamSubject.ENGLISH -> when (qNum % 4) {
                0 -> Tuple6("Choose the correct sentence in Reported Speech:", "She asked if I had seen her keys.", "She asked had I seen her keys.", "She asked that did I see her keys.", "She asked if have I seen her keys.", "A")
                1 -> Tuple6("The new museum exhibition is well worth ______.", "visiting", "to visit", "visit", "visited", "A")
                2 -> Tuple6("By the time we arrived at the station, the train ______.", "had already left", "has already left", "already left", "would leave", "A")
                else -> Tuple6("Which word is closest in meaning to 'METICULOUS'?", "Careful and thorough", "Careless and fast", "Lazy and slow", "Courageous", "A")
            }
            ExamSubject.MATEMATIKA -> when (qNum % 4) {
                0 -> Tuple6("Tenglamani yeching: 2x² - 8x + 6 = 0", "x₁ = 1, x₂ = 3", "x₁ = -1, x₂ = -3", "x₁ = 2, x₂ = 4", "x₁ = 0, x₂ = 3", "A")
                1 -> Tuple6("To'g'ri burchakli uchburchakning katetlari 6 cm va 8 cm. Gipotenuzasini toping.", "10 cm", "12 cm", "14 cm", "9 cm", "A")
                2 -> Tuple6("Funksiyaning hosilasini toping: f(x) = x³ + 4x² - 5x + 7", "3x² + 8x - 5", "3x² + 4x - 5", "x² + 8x", "3x³ + 8x - 5", "A")
                else -> Tuple6("Hisoblang: log₂(32) + log₃(27)", "8", "5", "6", "9", "A")
            }
            ExamSubject.FIZIKA -> Tuple6("Jismning tezlanishini toping: F = 20 N, m = 4 kg", "5 m/s²", "80 m/s²", "16 m/s²", "0.2 m/s²", "A")
            ExamSubject.KIMYO -> Tuple6("Sulfat kislotaning molekulyar formulasini ko'rsating:", "H₂SO₄", "HCl", "HNO₃", "H₃PO₄", "A")
            ExamSubject.BIOLOGIYA -> Tuple6("O'simlik hujayrasida fotosintez jarayoni qaysi organoidda kechadi?", "Xloroplastlarda", "Mitoxondriyada", "Ribosomada", "Vakuolada", "A")
            ExamSubject.TARIX -> Tuple6("Amir Temur davlatining poytaxti qaysi shahar bo'lgan?", "Samarqand", "Buxoro", "Xiva", "Toshkent", "A")
            ExamSubject.GEOGRAFIYA -> Tuple6("Dunyoning eng katta okeanini aniqlang:", "Tinch okeani", "Atlantika okeani", "Hind okeani", "Shimoliy Muz okeani", "A")
        }

        return QuestionEntity(
            id = id,
            testId = testId,
            questionNumber = qNum,
            type = QuestionType.CLOSED_ABCD,
            questionText = "$qNum. $text",
            optionA = a,
            optionB = b,
            optionC = c,
            optionD = d,
            correctOption = correct,
            maxScore = 1.0
        )
    }

    private fun createSampleSpecialClosedQuestion(id: String, testId: String, qNum: Int, subject: ExamSubject): QuestionEntity {
        val (text, a, b, c, d, correct) = when (qNum) {
            36 -> Tuple6("Quyidagi gapda qaysi so'z turkumiga kiradi?\n'Ilm - insonni yuksaltiradigan eng katta boylikdir.'", "Ilm - ot", "Insonni - sifat", "Yuksaltiradigan - ravish", "Boylikdir - fe'l", "A")
            37 -> Tuple6("Qaysi qatorda modal ma'no bildiruvchi so'zlar qatnashgan?", "Shubhasiz, bu ish tez orada hal bo'ladi.", "U kitobni o'qib bo'ldi.", "Daraxtlar barg to'kmoqda.", "Quyosh charoqlab turibdi.", "A")
            38 -> Tuple6("Qo'shma gap turini aniqlang: 'Bahor kelsa, tabiat uyg'onadi.'", "Shart ergash gapli qo'shma gap", "Bog'langan qo'shma gap", "Sabab ergash gapli qo'shma gap", "Bog'lovchisiz qo'shma gap", "A")
            else -> Tuple6("Muallif nutqi va ko'chirma gap munosabatida qaysi tinish belgisi xato qo'yilgan?", "'Biz albatta yutamiz' - dedi u ishonch bilan.", "Muallif nutqidan oldin ikki nuqta qo'yiladi.", "Ko'chirma gap qo'shtirnoqqa olinadi.", "Barchasi to'g'ri", "A")
        }

        return QuestionEntity(
            id = id,
            testId = testId,
            questionNumber = qNum,
            type = QuestionType.CLOSED_ABCD,
            questionText = "$qNum. $text",
            optionA = a,
            optionB = b,
            optionC = c,
            optionD = d,
            correctOption = correct,
            maxScore = 1.5
        )
    }

    private fun createSampleOpenTwoPartsQuestion(id: String, testId: String, qNum: Int, subject: ExamSubject): QuestionEntity {
        val (mainText, promptA, promptB, ansA, ansB) = when (subject) {
            ExamSubject.ONA_TILI, ExamSubject.ENGLISH -> when (qNum) {
                40 -> Tuple5(
                    "Berilgan matndagi uslubiy xatoliklarni tahlil qiling va tuzating.",
                    "a) 1-gapdagi mantiqiy yoki leksik xatolikni ko'rsatib, to'g'ri shaklini yozing:",
                    "b) 2-gapdagi fe'l nisbati nomuvofiqligini bartaraf eting:",
                    "samaradorlik", "amalga oshirildi"
                )
                41 -> Tuple5(
                    "She'riy parchadagi badiiy san'atlarni aniqlang.",
                    "a) Ushbu baytda qanday istiora san'ati qo'llangan?",
                    "b) Baytdagi tashxis elementini izohlang:",
                    "g'uncha lab", "tabiat tabassumi"
                )
                42 -> Tuple5(
                    "Quyidagi matn yuzasidan xulosa chiqaring va tahlil qiling.",
                    "a) Muallif ilgari surgan asosiy g'oyani 1-2 gapda ifodalang:",
                    "b) Ushbu g'oyani hayotiy misol bilan asoslang:",
                    "odob va axloq", "yoshlar tarbiyasi"
                )
                43 -> Tuple5(
                    "Morfologik va sintaktik birliklarni aniqlang.",
                    "a) Gapdagi ajratilgan bo'lak vazifasini yozing:",
                    "b) Kirish so'zning ifodalagan ma'no turini ko'rsating:",
                    "hol", "ishonch"
                )
                else -> Tuple5(
                    "Frazeologik birikmalar tahlili.",
                    "a) 'Ko'z yummoq' iborasining ko'chma ma'nolarini yozing:",
                    "b) Unga zid (antonimik) ibora keltiring:",
                    "e'tiborsiz qoldirmoq", "ogoh bo'lmoq"
                )
            }
            else -> Tuple5(
                "Masala va amaliy topshiriq tahlili.",
                "a) Berilgan topshiriq bo'yicha oraliq hisob-kitob natijasini yozing:",
                "b) Yakuniy xulosa va asosiy javob qiymatini ko'rsating:",
                "45", "120"
            )
        }

        return QuestionEntity(
            id = id,
            testId = testId,
            questionNumber = qNum,
            type = QuestionType.OPEN_TWO_PARTS,
            questionText = "$qNum. $mainText",
            openPartAPrompt = promptA,
            openPartBPrompt = promptB,
            correctAnswerA = ansA,
            correctAnswerB = ansB,
            maxScore = 2.0
        )
    }

    private fun createSampleEssayQuestion(id: String, testId: String, qNum: Int, subject: ExamSubject): QuestionEntity {
        val topic = if (subject == ExamSubject.ONA_TILI) {
            "Insho mavzusi: 'Kitob mutolaasi – inson kamoloti va tafakkuri kaliti'.\n\nTalablar:\n1. Mavzuni to'liq va mantiqiy ochib berish.\n2. Shaxsiy fikr-mulohazalarni dalillar bilan asoslash.\n3. Adabiy til me'yorlari va imlo qoidalariga qat'iy rioya qilish (kamida 120-150 so'z)."
        } else {
            "Writing Task: 'Some people believe that technology enhances students' learning abilities, while others argue it causes distractions. Discuss both views and give your own opinion.' (At least 150 words)."
        }

        return QuestionEntity(
            id = id,
            testId = testId,
            questionNumber = qNum,
            type = QuestionType.ESSAY,
            questionText = "$qNum. Insho (Esse) topshirig'i",
            essayPrompt = topic,
            maxScore = 10.0
        )
    }

    fun getSampleSubmissions(testId: String): List<StudentSubmissionEntity> {
        val now = System.currentTimeMillis()
        val answersMap1 = mutableMapOf<Int, StudentAnswer>()
        for (i in 1..35) {
            answersMap1[i] = StudentAnswer(questionNumber = i, selectedOption = if (i % 6 != 0) "A" else "B")
        }
        for (i in 36..39) {
            answersMap1[i] = StudentAnswer(questionNumber = i, selectedOption = "A")
        }
        for (i in 40..44) {
            answersMap1[i] = StudentAnswer(
                questionNumber = i,
                openAnswerA = "Mavzu bo'yicha to'g'ri tahlil va javob yozildi",
                openAnswerB = "Qo'shimcha izoh va asoslangan fikr kiritildi",
                scoreAwarded = 2.0,
                isGradedByTeacher = true
            )
        }
        answersMap1[45] = StudentAnswer(
            questionNumber = 45,
            essayText = "Kitob mutolaasi insonning ma'naviy dunyosini boyitadi, so'z boyligini oshiradi va tafakkurini kengaytiradi. Tarixga nazar tashlasak, barcha buyuk allomalar kitobga oshno bo'lishgan...",
            scoreAwarded = 9.0,
            isGradedByTeacher = true
        )

        return listOf(
            StudentSubmissionEntity(
                id = "sub_${testId}_elmiraxon",
                testId = testId,
                studentId = "user_elmiraxon",
                studentName = "Elmiraxon Toirova",
                studentLastName = "TOIROVA",
                studentFirstName = "ELMIRAXON",
                studentFatherName = "ABDIKAYUM QIZI",
                studentPersonalCode = "41909931330028",
                status = SubmissionStatus.CERTIFIED,
                startedAt = now - 5400000L,
                submittedAt = now - 1800000L,
                timeSpentSeconds = 3600L,
                answersJson = StudentAnswer.serializeAnswersMap(answersMap1),
                closedCorrectCount = 34,
                closedTotalCount = 39,
                openScore = 9.0,
                essayScore = 9.0,
                rawTotalScore = 52.0,
                raschScaledScore = 70.32,
                percentage = 88.3,
                certificateLevel = CertificateLevel.A_PLUS,
                rankPosition = 1,
                totalParticipants = 126,
                certificateId = "UZ26 641200",
                certificateIssueDate = "10.03.2026",
                testScorePart = 69.64,
                writtenScorePart = 71.0
            ),
            StudentSubmissionEntity(
                id = "sub_${testId}_sardor",
                testId = testId,
                studentId = "user_sardor",
                studentName = "Sardor Pirmamatov",
                studentLastName = "PIRMAMATOV",
                studentFirstName = "SARDOR",
                studentFatherName = "UCHQUN O'G'LI",
                studentPersonalCode = "51709096060074",
                status = SubmissionStatus.CERTIFIED,
                startedAt = now - 5000000L,
                submittedAt = now - 2000000L,
                timeSpentSeconds = 3000L,
                answersJson = StudentAnswer.serializeAnswersMap(answersMap1),
                closedCorrectCount = 31,
                closedTotalCount = 39,
                openScore = 7.5,
                essayScore = 7.0,
                rawTotalScore = 45.5,
                raschScaledScore = 59.46,
                percentage = 81.9,
                certificateLevel = CertificateLevel.A,
                rankPosition = 2,
                totalParticipants = 126,
                certificateId = "UZ26 728104",
                certificateIssueDate = "26.03.2026",
                testScorePart = 63.55,
                writtenScorePart = 55.36
            ),
            StudentSubmissionEntity(
                id = "sub_${testId}_farangiz",
                testId = testId,
                studentId = "user_farangiz",
                studentName = "Farangiz Zikrilloyeva",
                studentLastName = "ZIKRILLOYEVA",
                studentFirstName = "FARANGIZ",
                studentFatherName = "FARRUXOVNA",
                studentPersonalCode = "62209095360017",
                status = SubmissionStatus.CERTIFIED,
                startedAt = now - 4800000L,
                submittedAt = now - 2200000L,
                timeSpentSeconds = 2600L,
                answersJson = StudentAnswer.serializeAnswersMap(answersMap1),
                closedCorrectCount = 28,
                closedTotalCount = 39,
                openScore = 8.0,
                essayScore = 7.5,
                rawTotalScore = 43.5,
                raschScaledScore = 61.72,
                percentage = 78.6,
                certificateLevel = CertificateLevel.B_PLUS,
                rankPosition = 3,
                totalParticipants = 126,
                certificateId = "UZ26 597165",
                certificateIssueDate = "13.02.2026",
                testScorePart = 48.43,
                writtenScorePart = 75.0
            ),
            StudentSubmissionEntity(
                id = "sub_${testId}_oybek",
                testId = testId,
                studentId = "user_oybek",
                studentName = "Oybekjon Obidov",
                studentLastName = "OBIDOV",
                studentFirstName = "OYBEKJON",
                studentFatherName = "ODILJONOVICH",
                studentPersonalCode = "50501096110033",
                status = SubmissionStatus.CERTIFIED,
                startedAt = now - 4500000L,
                submittedAt = now - 2500000L,
                timeSpentSeconds = 2000L,
                answersJson = StudentAnswer.serializeAnswersMap(answersMap1),
                closedCorrectCount = 25,
                closedTotalCount = 39,
                openScore = 6.0,
                essayScore = 6.0,
                rawTotalScore = 37.0,
                raschScaledScore = 50.13,
                percentage = 67.12,
                certificateLevel = CertificateLevel.B,
                rankPosition = 4,
                totalParticipants = 126,
                certificateId = "UZ25 539506",
                certificateIssueDate = "25.12.2025",
                testScorePart = 42.0,
                writtenScorePart = 48.0
            )
        )
    }

    private data class Tuple5<A, B, C, D, E>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E)
    private data class Tuple6<A, B, C, D, E, F>(val first: A, val second: B, val third: C, val fourth: D, val fifth: E, val sixth: F)
}
