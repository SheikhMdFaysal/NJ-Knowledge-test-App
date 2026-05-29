package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class QuestionWithProgress(
    val question: NJQuestion,
    val progress: QuestionProgress?
) {
    val isFlagged: Boolean get() = progress?.isFlagged ?: false
    val isMastered: Boolean get() = progress?.isMastered ?: false
    val timesCorrect: Int get() = progress?.timesCorrect ?: 0
    val timesIncorrect: Int get() = progress?.timesIncorrect ?: 0
    val lastSelectedOption: Int? get() = progress?.lastSelectedOption
}

class NJPermitRepository(
    private val progressDao: QuestionProgressDao,
    private val examDao: MockExamDao
) {
    // Get all general progress rows
    val allProgress: Flow<List<QuestionProgress>> = progressDao.getAllProgress()

    // Get all mock exam results
    val allExamResults: Flow<List<MockExamResult>> = examDao.getAllExamResults()

    // Get joint list of questions with their respective progress
    val questionsWithProgress: Flow<List<QuestionWithProgress>> = progressDao.getAllProgress().map { progressList ->
        val progressMap = progressList.associateBy { it.questionId }
        QuestionBank.questions.map { question ->
            QuestionWithProgress(
                question = question,
                progress = progressMap[question.id]
            )
        }
    }

    // Toggle flag status for a question
    suspend fun toggleFlag(questionId: Int, isCurrentlyFlagged: Boolean) {
        val currentProgress = progressDao.getProgressForQuestion(questionId)
        if (currentProgress == null) {
            progressDao.saveProgress(
                QuestionProgress(questionId = questionId, isFlagged = !isCurrentlyFlagged)
            )
        } else {
            progressDao.updateFlagged(questionId, !isCurrentlyFlagged)
        }
    }

    // Toggle mastery status
    suspend fun toggleMastered(questionId: Int, isCurrentlyMastered: Boolean) {
        val currentProgress = progressDao.getProgressForQuestion(questionId)
        if (currentProgress == null) {
            progressDao.saveProgress(
                QuestionProgress(questionId = questionId, isMastered = !isCurrentlyMastered)
            )
        } else {
            progressDao.updateMastered(questionId, !isCurrentlyMastered)
        }
    }

    // Save student answer outcome
    suspend fun recordAnswer(questionId: Int, isCorrect: Boolean, selectedOption: Int) {
        val currentProgress = progressDao.getProgressForQuestion(questionId)
        val updated = if (currentProgress == null) {
            QuestionProgress(
                questionId = questionId,
                isFlagged = false,
                isMastered = isCorrect, // Master on first try correctly? Let's keep it simple
                timesCorrect = if (isCorrect) 1 else 0,
                timesIncorrect = if (isCorrect) 0 else 1,
                lastSelectedOption = selectedOption
            )
        } else {
            val newTimesCorrect = currentProgress.timesCorrect + (if (isCorrect) 1 else 0)
            val newTimesIncorrect = currentProgress.timesIncorrect + (if (isCorrect) 0 else 1)
            // Mastered of answered correctly 2 or more times, or on first correct? Let's say if consecutive timesCorrect >= 2
            val shouldBeMastered = newTimesCorrect >= 2
            currentProgress.copy(
                timesCorrect = newTimesCorrect,
                timesIncorrect = newTimesIncorrect,
                isMastered = shouldBeMastered,
                lastSelectedOption = selectedOption
            )
        }
        progressDao.saveProgress(updated)
    }

    // Add mock exam result
    suspend fun saveExamResult(scoreCount: Int, elapsedSecs: Int) {
        val passed = scoreCount >= 40 // NJ requires 40/50 correct (80%)
        examDao.insertExamResult(
            MockExamResult(
                score = scoreCount,
                totalQuestions = 50,
                elapsedSeconds = elapsedSecs,
                isPass = passed
            )
        )
    }

    // Reset statistics/wrong/flagged history
    suspend fun resetAllStats() {
        progressDao.clearAllProgress()
        examDao.clearExamResults()
    }
}
