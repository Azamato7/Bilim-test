package com.example.data.model

import org.json.JSONObject

data class StudentAnswer(
    val questionNumber: Int,
    val selectedOption: String? = null,
    val openAnswerA: String = "",
    val openAnswerB: String = "",
    val essayText: String = "",
    val isMarkedForReview: Boolean = false,
    val scoreAwarded: Double = 0.0,
    val isGradedByTeacher: Boolean = false
) {
    fun isAnswered(type: QuestionType): Boolean {
        return when (type) {
            QuestionType.CLOSED_ABCD -> !selectedOption.isNullOrBlank()
            QuestionType.OPEN_TWO_PARTS -> openAnswerA.isNotBlank() || openAnswerB.isNotBlank()
            QuestionType.ESSAY -> essayText.isNotBlank()
        }
    }

    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("q", questionNumber)
        selectedOption?.let { obj.put("opt", it) }
        if (openAnswerA.isNotBlank()) obj.put("a", openAnswerA)
        if (openAnswerB.isNotBlank()) obj.put("b", openAnswerB)
        if (essayText.isNotBlank()) obj.put("essay", essayText)
        obj.put("marked", isMarkedForReview)
        obj.put("score", scoreAwarded)
        obj.put("graded", isGradedByTeacher)
        return obj
    }

    companion object {
        fun fromJson(json: JSONObject): StudentAnswer {
            return StudentAnswer(
                questionNumber = json.optInt("q", 1),
                selectedOption = if (json.has("opt")) json.getString("opt") else null,
                openAnswerA = json.optString("a", ""),
                openAnswerB = json.optString("b", ""),
                essayText = json.optString("essay", ""),
                isMarkedForReview = json.optBoolean("marked", false),
                scoreAwarded = json.optDouble("score", 0.0),
                isGradedByTeacher = json.optBoolean("graded", false)
            )
        }

        fun parseAnswersMap(jsonStr: String): Map<Int, StudentAnswer> {
            val map = mutableMapOf<Int, StudentAnswer>()
            if (jsonStr.isBlank() || jsonStr == "{}") return map
            try {
                val root = JSONObject(jsonStr)
                val keys = root.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val qNum = key.toIntOrNull() ?: continue
                    val obj = root.getJSONObject(key)
                    map[qNum] = fromJson(obj)
                }
            } catch (e: Exception) {
                // Return empty if parsing error
            }
            return map
        }

        fun serializeAnswersMap(map: Map<Int, StudentAnswer>): String {
            val root = JSONObject()
            map.forEach { (qNum, ans) ->
                root.put(qNum.toString(), ans.toJson())
            }
            return root.toString()
        }
    }
}
