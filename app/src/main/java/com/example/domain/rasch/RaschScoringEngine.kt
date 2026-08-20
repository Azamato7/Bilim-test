package com.example.domain.rasch

import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.model.CertificateLevel
import com.example.data.model.QuestionType
import com.example.data.model.StudentAnswer
import com.example.data.model.SubmissionStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object RaschScoringEngine {

    data class RaschEvaluationResult(
        val updatedSubmissions: List<StudentSubmissionEntity>,
        val averageScore: Double,
        val highestScore: Double,
        val lowestScore: Double,
        val totalCompleted: Int,
        val levelCounts: Map<CertificateLevel, Int>,
        val distributionBuckets: Map<String, Int>
    )

    /**
     * Executes the Rasch model ranking and certificate issue process for all submissions of a test.
     */
    fun calculateAndIssueCertificates(
        questions: List<QuestionEntity>,
        submissions: List<StudentSubmissionEntity>,
        testSubjectName: String = "Ona tili"
    ): RaschEvaluationResult {
        if (submissions.isEmpty()) {
            return RaschEvaluationResult(
                updatedSubmissions = emptyList(),
                averageScore = 0.0,
                highestScore = 0.0,
                lowestScore = 0.0,
                totalCompleted = 0,
                levelCounts = emptyMap(),
                distributionBuckets = emptyMap()
            )
        }

        val parsedAnswers = submissions.map { sub ->
            sub.id to StudentAnswer.parseAnswersMap(sub.answersJson)
        }.toMap()

        // 1. Calculate item difficulties (proportion correct across students)
        val closedQuestions = questions.filter { it.type == QuestionType.CLOSED_ABCD }
        val itemDifficulties = mutableMapOf<Int, Double>()

        for (q in closedQuestions) {
            var correctCount = 0
            for (sub in submissions) {
                val ans = parsedAnswers[sub.id]?.get(q.questionNumber)
                if (ans != null && ans.selectedOption.equals(q.correctOption, ignoreCase = true)) {
                    correctCount++
                }
            }
            val p = (correctCount + 0.5) / (submissions.size + 1.0)
            val clampedP = p.coerceIn(0.05, 0.95)
            // Logit difficulty
            val b = -ln(clampedP / (1.0 - clampedP))
            itemDifficulties[q.questionNumber] = b
        }

        val avgItemDifficulty = if (itemDifficulties.isNotEmpty()) itemDifficulties.values.average() else 0.0

        // 2. Score each submission
        val scoredList = submissions.map { sub ->
            val answers = parsedAnswers[sub.id] ?: emptyMap()
            var closedCorrect = 0
            var closedTotal = 0
            var openTotalScore = 0.0
            var essayTotalScore = 0.0
            var maxPossibleScore = 0.0

            for (q in questions) {
                maxPossibleScore += q.maxScore
                val ans = answers[q.questionNumber]

                when (q.type) {
                    QuestionType.CLOSED_ABCD -> {
                        closedTotal++
                        if (ans != null && ans.selectedOption.equals(q.correctOption, ignoreCase = true)) {
                            closedCorrect++
                        }
                    }
                    QuestionType.OPEN_TWO_PARTS -> {
                        if (ans != null) {
                            if (ans.isGradedByTeacher) {
                                openTotalScore += ans.scoreAwarded
                            } else {
                                // Auto-grade if keys match or non-empty reasonable answer
                                var autoScore = 0.0
                                if (ans.openAnswerA.isNotBlank()) {
                                    if (!q.correctAnswerA.isNullOrBlank() && ans.openAnswerA.trim().equals(q.correctAnswerA.trim(), ignoreCase = true)) {
                                        autoScore += q.maxScore / 2.0
                                    } else if (ans.openAnswerA.length >= 2) {
                                        autoScore += (q.maxScore / 2.0) * 0.75
                                    }
                                }
                                if (ans.openAnswerB.isNotBlank()) {
                                    if (!q.correctAnswerB.isNullOrBlank() && ans.openAnswerB.trim().equals(q.correctAnswerB.trim(), ignoreCase = true)) {
                                        autoScore += q.maxScore / 2.0
                                    } else if (ans.openAnswerB.length >= 2) {
                                        autoScore += (q.maxScore / 2.0) * 0.75
                                    }
                                }
                                openTotalScore += autoScore
                            }
                        }
                    }
                    QuestionType.ESSAY -> {
                        if (ans != null) {
                            if (ans.isGradedByTeacher) {
                                essayTotalScore += ans.scoreAwarded
                            } else if (ans.essayText.isNotBlank()) {
                                // Default essay score estimation based on word count/quality
                                val wordCount = ans.essayText.trim().split("\\s+".toRegex()).size
                                val ratio = (wordCount / 120.0).coerceIn(0.4, 1.0)
                                essayTotalScore += q.maxScore * ratio
                            }
                        }
                    }
                }
            }

            val rawScore = closedCorrect.toDouble() + openTotalScore + essayTotalScore
            val maxScore = if (maxPossibleScore > 0) maxPossibleScore else 45.0
            val proportion = (rawScore / maxScore).coerceIn(0.01, 0.99)

            // Rasch Ability θ = ln(p / (1 - p)) + avgItemDifficulty
            val theta = ln(proportion / (1.0 - proportion)) + avgItemDifficulty

            // Scaled score to 100-point scale with Rasch smoothing
            // Scale: theta mapped smoothly to ~20..100
            val raschScore100 = (50.0 + (theta * 14.5)).coerceIn(15.0, 100.0)
            val roundedRaschScore = (raschScore100 * 100.0).roundToInt() / 100.0

            // 800-point scale standard (e.g. 642)
            val raschScore800 = (400.0 + (theta * 115.0)).coerceIn(200.0, 800.0).roundToInt().toDouble()

            val percentage = ((rawScore / maxScore) * 100.0 * 10.0).roundToInt() / 10.0

            val testSinoviPart = if (closedTotal > 0) {
                ((closedCorrect.toDouble() / closedTotal.toDouble()) * 100.0 * 10.0).roundToInt() / 10.0
            } else 0.0

            val writtenWorkPart = if (openTotalScore + essayTotalScore > 0) {
                val maxWritten = maxScore - closedTotal
                val wRatio = if (maxWritten > 0) (openTotalScore + essayTotalScore) / maxWritten else 0.5
                (wRatio * 100.0 * 10.0).roundToInt() / 10.0
            } else 0.0

            val level = determineLevel(percentage)

            // Generate official certificate serial number
            val certYear = "26"
            val randomSerial = 500000 + Random(sub.id.hashCode().toLong()).nextInt(400000)
            val certificateId = "UZ$certYear $randomSerial"

            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val issueDate = dateFormat.format(Date())

            ScoredSubmission(
                rawEntity = sub,
                closedCorrect = closedCorrect,
                closedTotal = closedTotal,
                openScore = openTotalScore,
                essayScore = essayTotalScore,
                rawTotalScore = rawScore,
                raschScaledScore = roundedRaschScore,
                rasch800Score = raschScore800,
                percentage = percentage,
                testSinoviPart = testSinoviPart,
                writtenScorePart = writtenWorkPart,
                level = level,
                certificateId = certificateId,
                issueDate = issueDate
            )
        }

        // 3. Rank submissions
        val rankedList = scoredList.sortedByDescending { it.rawTotalScore }
        val totalCount = rankedList.size

        val updatedEntities = rankedList.mapIndexed { index, scored ->
            val rank = index + 1
            scored.rawEntity.copy(
                status = SubmissionStatus.CERTIFIED,
                closedCorrectCount = scored.closedCorrect,
                closedTotalCount = scored.closedTotal,
                openScore = scored.openScore,
                essayScore = scored.essayScore,
                rawTotalScore = scored.rawTotalScore,
                raschScaledScore = scored.raschScaledScore,
                percentage = scored.percentage,
                certificateLevel = scored.level,
                rankPosition = rank,
                totalParticipants = totalCount,
                certificateId = scored.certificateId,
                certificateIssueDate = scored.issueDate,
                testScorePart = scored.testSinoviPart,
                writtenScorePart = scored.writtenScorePart,
                submittedAt = scored.rawEntity.submittedAt ?: System.currentTimeMillis()
            )
        }

        val avgScore = if (updatedEntities.isNotEmpty()) {
            ((updatedEntities.map { it.percentage }.average() * 10.0).roundToInt() / 10.0)
        } else 0.0

        val maxScore = updatedEntities.maxOfOrNull { it.raschScaledScore } ?: 0.0
        val minScore = updatedEntities.minOfOrNull { it.raschScaledScore } ?: 0.0

        val levelCounts = updatedEntities.groupingBy { it.certificateLevel }.eachCount()

        // Rasch distribution buckets (200-300, 300-400, 400-500, 500-600, 600-700, 700-800)
        val buckets = mutableMapOf(
            "200-300" to 0,
            "300-400" to 0,
            "400-500" to 0,
            "500-600" to 0,
            "600-700" to 0,
            "700-800" to 0
        )

        for (item in scoredList) {
            val score800 = item.rasch800Score
            when {
                score800 < 300 -> buckets["200-300"] = (buckets["200-300"] ?: 0) + 1
                score800 < 400 -> buckets["300-400"] = (buckets["300-400"] ?: 0) + 1
                score800 < 500 -> buckets["400-500"] = (buckets["400-500"] ?: 0) + 1
                score800 < 600 -> buckets["500-600"] = (buckets["500-600"] ?: 0) + 1
                score800 < 700 -> buckets["600-700"] = (buckets["600-700"] ?: 0) + 1
                else -> buckets["700-800"] = (buckets["700-800"] ?: 0) + 1
            }
        }

        return RaschEvaluationResult(
            updatedSubmissions = updatedEntities,
            averageScore = avgScore,
            highestScore = maxScore,
            lowestScore = minScore,
            totalCompleted = totalCount,
            levelCounts = levelCounts,
            distributionBuckets = buckets
        )
    }

    fun determineLevel(percentage: Double): CertificateLevel {
        return when {
            percentage >= 86.0 -> CertificateLevel.A_PLUS
            percentage >= 70.0 -> CertificateLevel.A
            percentage >= 65.0 -> CertificateLevel.B_PLUS
            percentage >= 60.0 -> CertificateLevel.B
            percentage >= 55.0 -> CertificateLevel.C_PLUS
            percentage >= 50.0 -> CertificateLevel.C
            else -> CertificateLevel.NONE
        }
    }

    private data class ScoredSubmission(
        val rawEntity: StudentSubmissionEntity,
        val closedCorrect: Int,
        val closedTotal: Int,
        val openScore: Double,
        val essayScore: Double,
        val rawTotalScore: Double,
        val raschScaledScore: Double,
        val rasch800Score: Double,
        val percentage: Double,
        val testSinoviPart: Double,
        val writtenScorePart: Double,
        val level: CertificateLevel,
        val certificateId: String,
        val issueDate: String
    )
}
