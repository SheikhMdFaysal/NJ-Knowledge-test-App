package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface PracticeMode {
    object Idle : PracticeMode
    data class Study(val startIndex: Int = 0) : PracticeMode
    data class MockExam(
        val questions: List<QuestionWithProgress>,
        val answers: Map<Int, Int> = emptyMap(), // questionId -> selectedIndex
        val isSubmitted: Boolean = false,
        val elapsedSeconds: Int = 0,
        val finalScore: Int = 0
    ) : PracticeMode
    data class RefresherWrong(val questions: List<QuestionWithProgress>) : PracticeMode
    data class FlaggedMode(val questions: List<QuestionWithProgress>) : PracticeMode
    data class CategoryDrill(val category: String, val questions: List<QuestionWithProgress>) : PracticeMode
    data class MiniQuiz(val quizNumber: Int, val questions: List<QuestionWithProgress>) : PracticeMode
}

enum class ActiveTab {
    PRACTICE,
    STUDY_TOOLS,
    SEARCH,
    PROGRESS
}

class NJPermitViewModel(application: Application) : AndroidViewModel(application) {

    private val database = Room.databaseBuilder(
        application,
        NJPermitDatabase::class.java,
        "nj_permit_practice_db"
    ).build()

    private val repository = NJPermitRepository(
        database.questionProgressDao(),
        database.mockExamDao()
    )

    // Observable states from DB
    val questionsWithProgress: StateFlow<List<QuestionWithProgress>> = repository.questionsWithProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val examResults: StateFlow<List<MockExamResult>> = repository.allExamResults
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Navigation and tab states
    private val _activeTab = MutableStateFlow(ActiveTab.PRACTICE)
    val activeTab: StateFlow<ActiveTab> = _activeTab.asStateFlow()

    // Mode-specific navigation state
    private val _practiceMode = MutableStateFlow<PracticeMode>(PracticeMode.Idle)
    val practiceMode: StateFlow<PracticeMode> = _practiceMode.asStateFlow()

    // Active session index states (for sequential study/refresher)
    private val _currentSessionIndex = MutableStateFlow(0)
    val currentSessionIndex: StateFlow<Int> = _currentSessionIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _hasSubmittedAnswer = MutableStateFlow(false)
    val hasSubmittedAnswer: StateFlow<Boolean> = _hasSubmittedAnswer.asStateFlow()

    // Search Mode State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Flashcard tool state
    private val _currentFlashcardIndex = MutableStateFlow(0)
    val currentFlashcardIndex: StateFlow<Int> = _currentFlashcardIndex.asStateFlow()

    private val _flashcardSideIsFront = MutableStateFlow(true)
    val flashcardSideIsFront: StateFlow<Boolean> = _flashcardSideIsFront.asStateFlow()

    private val _flashcardCategoryFilter = MutableStateFlow("All")
    val flashcardCategoryFilter: StateFlow<String> = _flashcardCategoryFilter.asStateFlow()

    // Mind Map active detail sheet branch
    private val _activeMindMapBranch = MutableStateFlow<String?>(null)
    val activeMindMapBranch: StateFlow<String?> = _activeMindMapBranch.asStateFlow()

    // Mock Exam Timer Job
    private var timerJob: Job? = null

    init {
        // Automatically preheat progress DB rows on first launch if needed
        viewModelScope.launch {
            // Seed operations or migrations
        }
    }

    // Set Active Menu Tab
    fun setActiveTab(tab: ActiveTab) {
        _activeTab.value = tab
        // Close interactive mode screens when switching tabs to ensure neat flow
        if (tab != ActiveTab.PRACTICE) {
            cancelActiveMode()
        }
    }

    // Enter Study Mode
    fun startStudyMode(resumeIndex: Int = 0) {
        _currentSessionIndex.value = resumeIndex
        _selectedOptionIndex.value = null
        _hasSubmittedAnswer.value = false
        _practiceMode.value = PracticeMode.Study(startIndex = resumeIndex)
    }

    // Enter Mock Exam Mode
    fun startMockExam() {
        val shuffledQuestions = questionsWithProgress.value.shuffled().take(50)
        _practiceMode.value = PracticeMode.MockExam(
            questions = shuffledQuestions,
            answers = emptyMap(),
            isSubmitted = false,
            elapsedSeconds = 0
        )
        _currentSessionIndex.value = 0
        _selectedOptionIndex.value = null
        _hasSubmittedAnswer.value = false

        // Start 20-min countdown timer
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_practiceMode.value is PracticeMode.MockExam) {
                delay(1000)
                val current = _practiceMode.value as? PracticeMode.MockExam ?: break
                if (current.isSubmitted) break
                val nextSecs = current.elapsedSeconds + 1
                if (nextSecs >= 1200) { // 20 minutes limit reached
                    submitMockExam()
                    break
                } else {
                    _practiceMode.value = current.copy(elapsedSeconds = nextSecs)
                }
            }
        }
    }

    // Enter Review Wrong Answers / Refresher mode
    fun startRefresherMode() {
        val wrongList = questionsWithProgress.value.filter { it.timesIncorrect > 0 && !it.isMastered }
        if (wrongList.isNotEmpty()) {
            _practiceMode.value = PracticeMode.RefresherWrong(questions = wrongList)
            _currentSessionIndex.value = 0
            _selectedOptionIndex.value = null
            _hasSubmittedAnswer.value = false
        }
    }

    // Enter Flagged Mode
    fun startFlaggedMode() {
        val flaggedList = questionsWithProgress.value.filter { it.isFlagged }
        if (flaggedList.isNotEmpty()) {
            _practiceMode.value = PracticeMode.FlaggedMode(questions = flaggedList)
            _currentSessionIndex.value = 0
            _selectedOptionIndex.value = null
            _hasSubmittedAnswer.value = false
        }
    }

    // Enter Category Drills mode
    fun startCategoryDrill(category: String) {
        val filtered = questionsWithProgress.value.filter { it.question.category == category }
        if (filtered.isNotEmpty()) {
            _practiceMode.value = PracticeMode.CategoryDrill(category = category, questions = filtered)
            _currentSessionIndex.value = 0
            _selectedOptionIndex.value = null
            _hasSubmittedAnswer.value = false
        }
    }

    // Enter Mini Quiz mode
    fun startMiniQuiz(quizNum: Int) {
        // Group of 10 questions. Quiz 1: Q1-10, Quiz 2: Q11-20, etc.
        val startIndex = (quizNum - 1) * 10
        val subset = questionsWithProgress.value.drop(startIndex).take(10)
        if (subset.isNotEmpty()) {
            _practiceMode.value = PracticeMode.MiniQuiz(quizNumber = quizNum, questions = subset)
            _currentSessionIndex.value = 0
            _selectedOptionIndex.value = null
            _hasSubmittedAnswer.value = false
        }
    }

    // Select alternative answer
    fun selectOption(optionIndex: Int) {
        val currentMode = _practiceMode.value
        if (currentMode is PracticeMode.MockExam) {
            if (!currentMode.isSubmitted) {
                val updatedAnswers = currentMode.answers.toMutableMap()
                val activeQuestion = currentMode.questions[_currentSessionIndex.value]
                updatedAnswers[activeQuestion.question.id] = optionIndex
                _practiceMode.value = currentMode.copy(answers = updatedAnswers)
                _selectedOptionIndex.value = optionIndex
            }
        } else {
            if (!_hasSubmittedAnswer.value) {
                _selectedOptionIndex.value = optionIndex
            }
        }
    }

    // Navigate to next or previous items in active mode
    fun goToNextQuestion(questionsSize: Int) {
        if (_currentSessionIndex.value < questionsSize - 1) {
            _currentSessionIndex.value += 1
            _selectedOptionIndex.value = null
            _hasSubmittedAnswer.value = false
        }
    }

    fun goToPreviousQuestion() {
        if (_currentSessionIndex.value > 0) {
            _currentSessionIndex.value -= 1
            _selectedOptionIndex.value = null
            _hasSubmittedAnswer.value = false
        }
    }

    // Toggle Flag for question
    fun toggleFlagOnCurrent(questionId: Int, isFlagged: Boolean) {
        viewModelScope.launch {
            repository.toggleFlag(questionId, isFlagged)
        }
    }

    // Toggle Mastered for question
    fun toggleMasteredOnCurrent(questionId: Int, isMastered: Boolean) {
        viewModelScope.launch {
            repository.toggleMastered(questionId, isMastered)
        }
    }

    // Submit user answer in STUDY, REFRESHER, DRILLS, or MINI QUIZZES
    fun submitAnswer(activeQuestion: QuestionWithProgress) {
        val selected = _selectedOptionIndex.value ?: return
        _hasSubmittedAnswer.value = true
        val isCorrect = selected == activeQuestion.question.correctAnswerIndex

        viewModelScope.launch {
            repository.recordAnswer(
                questionId = activeQuestion.question.id,
                isCorrect = isCorrect,
                selectedOption = selected
            )
        }
    }

    // Submit entire Mock Exam session
    fun submitMockExam() {
        val current = _practiceMode.value as? PracticeMode.MockExam ?: return
        timerJob?.cancel()

        var correctCount = 0
        current.questions.forEach { joint ->
            val sel = current.answers[joint.question.id]
            if (sel == joint.question.correctAnswerIndex) {
                correctCount++
            }
        }

        val updated = current.copy(isSubmitted = true, finalScore = correctCount)
        _practiceMode.value = updated

        // Record results to historical mock results
        viewModelScope.launch {
            repository.saveExamResult(scoreCount = correctCount, elapsedSecs = current.elapsedSeconds)
            // also record answer hits to help train learning curve
            current.questions.forEach { joint ->
                val sel = current.answers[joint.question.id]
                if (sel != null) {
                    val wasCorrect = sel == joint.question.correctAnswerIndex
                    repository.recordAnswer(joint.question.id, wasCorrect, sel)
                }
            }
        }
    }

    // Exit active mode
    fun cancelActiveMode() {
        timerJob?.cancel()
        _practiceMode.value = PracticeMode.Idle
        _currentSessionIndex.value = 0
        _selectedOptionIndex.value = null
        _hasSubmittedAnswer.value = false
    }

    // For Search queries
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Flashcard Tools Controllers
    fun setFlashcardCategory(category: String) {
        _flashcardCategoryFilter.value = category
        _currentFlashcardIndex.value = 0
        _flashcardSideIsFront.value = true
    }

    fun flipFlashcard() {
        _flashcardSideIsFront.value = !_flashcardSideIsFront.value
    }

    fun nextFlashcard(deckSize: Int) {
        if (deckSize > 0) {
            _currentFlashcardIndex.value = (_currentFlashcardIndex.value + 1) % deckSize
            _flashcardSideIsFront.value = true
        }
    }

    fun prevFlashcard(deckSize: Int) {
        if (deckSize > 0) {
            _currentFlashcardIndex.value = if (_currentFlashcardIndex.value - 1 < 0) {
                deckSize - 1
            } else {
                _currentFlashcardIndex.value - 1
            }
            _flashcardSideIsFront.value = true
        }
    }

    // Mind Map Controller
    fun selectMindMapBranch(branchName: String?) {
        _activeMindMapBranch.value = branchName
    }

    // Clear history / fully reset analytics
    fun resetLearningHistory() {
        viewModelScope.launch {
            repository.resetAllStats()
        }
    }
}
