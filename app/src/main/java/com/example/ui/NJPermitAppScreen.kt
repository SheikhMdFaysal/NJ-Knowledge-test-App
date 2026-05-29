package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.IncorrectRed
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NJPermitAppScreen(viewModel: NJPermitViewModel, modifier: Modifier = Modifier) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val practiceMode by viewModel.practiceMode.collectAsStateWithLifecycle()
    val questionsWithProgress by viewModel.questionsWithProgress.collectAsStateWithLifecycle()
    val examResults by viewModel.examResults.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (practiceMode == PracticeMode.Idle) {
                NJNavigationBar(
                    activeTab = activeTab,
                    onTabSelected = { viewModel.setActiveTab(it) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background ambient gradients depending on system theme
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                            )
                        )
                    )
            )

            AnimatedContent(
                targetState = practiceMode,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "AppStateTransition"
            ) { mode ->
                if (mode == PracticeMode.Idle) {
                    when (activeTab) {
                        ActiveTab.PRACTICE -> PracticeDashboard(
                            viewModel = viewModel,
                            questions = questionsWithProgress,
                            examResults = examResults
                        )
                        ActiveTab.STUDY_TOOLS -> StudyToolsDashboard(
                            viewModel = viewModel,
                            questions = questionsWithProgress
                        )
                        ActiveTab.SEARCH -> SearchDashboard(
                            viewModel = viewModel,
                            questions = questionsWithProgress
                        )
                        ActiveTab.PROGRESS -> DiagnosticsDashboard(
                            viewModel = viewModel,
                            questions = questionsWithProgress,
                            examResults = examResults
                        )
                    }
                } else {
                    // We are actively inside an interactive practice session
                    when (mode) {
                        is PracticeMode.Study -> SequentialStudyScreen(
                            viewModel = viewModel,
                            questions = questionsWithProgress,
                            currentIndex = viewModel.currentSessionIndex.collectAsStateWithLifecycle().value,
                            selectedOption = viewModel.selectedOptionIndex.collectAsStateWithLifecycle().value,
                            hasSubmitted = viewModel.hasSubmittedAnswer.collectAsStateWithLifecycle().value
                        )
                        is PracticeMode.MockExam -> MockExamScreen(
                            viewModel = viewModel,
                            examState = mode,
                            currentIndex = viewModel.currentSessionIndex.collectAsStateWithLifecycle().value
                        )
                        is PracticeMode.RefresherWrong -> RefresherScreen(
                            viewModel = viewModel,
                            questionsList = mode.questions,
                            currentIndex = viewModel.currentSessionIndex.collectAsStateWithLifecycle().value,
                            selectedOption = viewModel.selectedOptionIndex.collectAsStateWithLifecycle().value,
                            hasSubmitted = viewModel.hasSubmittedAnswer.collectAsStateWithLifecycle().value
                        )
                        is PracticeMode.FlaggedMode -> FlaggedSessionScreen(
                            viewModel = viewModel,
                            questionsList = mode.questions,
                            currentIndex = viewModel.currentSessionIndex.collectAsStateWithLifecycle().value,
                            selectedOption = viewModel.selectedOptionIndex.collectAsStateWithLifecycle().value,
                            hasSubmitted = viewModel.hasSubmittedAnswer.collectAsStateWithLifecycle().value
                        )
                        is PracticeMode.CategoryDrill -> CategoryDrillScreen(
                            viewModel = viewModel,
                            categoryName = mode.category,
                            questionsList = mode.questions,
                            currentIndex = viewModel.currentSessionIndex.collectAsStateWithLifecycle().value,
                            selectedOption = viewModel.selectedOptionIndex.collectAsStateWithLifecycle().value,
                            hasSubmitted = viewModel.hasSubmittedAnswer.collectAsStateWithLifecycle().value
                        )
                        is PracticeMode.MiniQuiz -> MiniQuizScreen(
                            viewModel = viewModel,
                            quizNumber = mode.quizNumber,
                            questionsList = mode.questions,
                            currentIndex = viewModel.currentSessionIndex.collectAsStateWithLifecycle().value,
                            selectedOption = viewModel.selectedOptionIndex.collectAsStateWithLifecycle().value,
                            hasSubmitted = viewModel.hasSubmittedAnswer.collectAsStateWithLifecycle().value
                        )
                        else -> {}
                    }
                }
            }
        }
    }
}

// Navigation Bar
@Composable
fun NJNavigationBar(
    activeTab: ActiveTab,
    onTabSelected: (ActiveTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("app_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        val items = listOf(
            Triple(ActiveTab.PRACTICE, "Practice", Icons.Default.PlayArrow),
            Triple(ActiveTab.STUDY_TOOLS, "Tools", Icons.Default.Info),
            Triple(ActiveTab.SEARCH, "Search", Icons.Default.Search),
            Triple(ActiveTab.PROGRESS, "Progress", Icons.Default.Person)
        )

        items.forEach { (tab, label, icon) ->
            NavigationBarItem(
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
                selected = activeTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(imageVector = icon, contentDescription = label) },
                label = { Text(label, fontWeight = FontWeight.SemiBold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

// PRACTICE TAB: Main Hub
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeDashboard(
    viewModel: NJPermitViewModel,
    questions: List<QuestionWithProgress>,
    examResults: List<MockExamResult>
) {
    // Collect progress metrics safely
    val totalCount = questions.size
    val masteredCount = questions.count { it.isMastered }
    val flaggedCount = questions.count { it.isFlagged }
    val wrongCount = questions.count { it.timesIncorrect > 0 && !it.isMastered }
    val masteryProgress = if (totalCount > 0) masteredCount.toFloat() / totalCount else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("practice_dashboard")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NJHeaderSection(
                title = "NJ MVC Knowledge Test",
                subtitle = "Permit Practice Assistant"
            )
        }

        // 1. Overall Progress Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("status_summary_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Your Learning Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mastery Rate",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${(masteryProgress * 100).roundToInt()}%",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // Circular Progress indicator
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(64.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { masteryProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 8.dp,
                                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            )
                            Text(
                                text = "$masteredCount/$totalCount",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        GridMiniMetric(
                            title = "Flagged",
                            value = flaggedCount.toString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        GridMiniMetric(
                            title = "Review Missteps",
                            value = wrongCount.toString(),
                            color = if (wrongCount > 0) IncorrectRed else MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        GridMiniMetric(
                            title = "Mock Exams",
                            value = examResults.size.toString(),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // 2. Practice Modes Selection
        item {
            Text(
                text = "⚡ Practice Modes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Study Mode
                FullWidthModeCard(
                    title = "Sequential Study Mode",
                    description = "Work through all 50 permit questions in orderly sequence. Remembers and resumes your exact current spot.",
                    icon = Icons.Default.List,
                    badgeText = "Best for Learning",
                    containerColor = MaterialTheme.colorScheme.surface,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    onClick = { viewModel.startStudyMode() },
                    testTag = "mode_button_study"
                )

                // Real Mock Exam Mode
                FullWidthModeCard(
                    title = "Timed Mock Exam",
                    description = "Simulates authentic NJ MVC Permit Test. 50 random questions, 20-minute countdown timer. Pass hurdle is correct in 40/50 (80%).",
                    icon = Icons.Default.PlayArrow,
                    badgeText = "Real Simulation",
                    containerColor = MaterialTheme.colorScheme.surface,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    onClick = { viewModel.startMockExam() },
                    testTag = "mode_button_mock_exam"
                )

                // Row of Wrongs & Flagged (Side-by-Side adaptive layout)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GridModeCard(
                            title = "Review Wrong",
                            count = wrongCount,
                            description = "Focus on incorrectly answered items",
                            icon = Icons.Default.Warning,
                            iconColor = IncorrectRed,
                            enabled = wrongCount > 0,
                            onClick = { viewModel.startRefresherMode() },
                            testTag = "mode_button_wrong_refresher"
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        GridModeCard(
                            title = "Flagged Quiz",
                            count = flaggedCount,
                            description = "Practice bookmarked questions list",
                            icon = Icons.Default.Star,
                            iconColor = MaterialTheme.colorScheme.tertiary,
                            enabled = flaggedCount > 0,
                            onClick = { viewModel.startFlaggedMode() },
                            testTag = "mode_button_flagged_quiz"
                        )
                    }
                }
            }
        }

        // 3. Mini Quizzes Section (Drills of 10 items)
        item {
            Text(
                text = "🎯 Mini Quizzes",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { quizNum ->
                    Card(
                        onClick = { viewModel.startMiniQuiz(quizNum) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mini_quiz_card_$quizNum"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = quizNum.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Mini-Quiz $quizNum",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Questions ${(quizNum - 1) * 10 + 1} to ${quizNum * 10}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Start Mini-Quiz",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

// STUDY TOOLS TAB: Category Drills, Flashcards, Mind Map Cheat Sheet
@Composable
fun StudyToolsDashboard(
    viewModel: NJPermitViewModel,
    questions: List<QuestionWithProgress>
) {
    var activeToolChapter by remember { mutableStateOf("FLASHCARDS") } // "FLASHCARDS" or "MINDMAP" or "CATEGORIES"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("study_tools_dashboard")
            .padding(16.dp)
    ) {
        NJHeaderSection(
            title = "Study & Learning Tools",
            subtitle = "Master GDL rules & signs visually"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Multi-option pill buttons for choosing tool sub-tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(32.dp)
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val chapters = listOf(
                "FLASHCARDS" to "Flashcards 🃏",
                "MINDMAP" to "Cheat Map 🗺️",
                "CATEGORIES" to "Drills 📚"
            )
            chapters.forEach { (chapterKey, label) ->
                val isActive = activeToolChapter == chapterKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                        .clickable { activeToolChapter = chapterKey }
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tool screen dynamic content
        Box(modifier = Modifier.weight(1f)) {
            when (activeToolChapter) {
                "FLASHCARDS" -> FlashcardsModule(viewModel = viewModel, questions = questions)
                "MINDMAP" -> MindMapModule(viewModel = viewModel)
                "CATEGORIES" -> CategoryDrillsModule(viewModel = viewModel, questions = questions)
            }
        }
    }
}

// STUDY TOOLS: FLASHCARDS COMPOSE ENGINE
@Composable
fun FlashcardsModule(
    viewModel: NJPermitViewModel,
    questions: List<QuestionWithProgress>
) {
    val activeCategoryFilter by viewModel.flashcardCategoryFilter.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentFlashcardIndex.collectAsStateWithLifecycle()
    val isFrontSide by viewModel.flashcardSideIsFront.collectAsStateWithLifecycle()

    // Retrieve categories dynamically
    val categories = listOf("All") + questions.map { it.question.category }.distinct()

    val filteredList = remember(activeCategoryFilter, questions) {
        if (activeCategoryFilter == "All") {
            questions
        } else {
            questions.filter { it.question.category == activeCategoryFilter }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("flashcards_module"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Category selector scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = activeCategoryFilter == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setFlashcardCategory(category) },
                    label = { Text(category, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No questions found for this category.")
            }
            return
        }

        val activeJoint = filteredList[currentIndex.coerceAtMost(filteredList.size - 1)]

        // Visual 3D Flip Card Widget
        val animatedRotation by animateFloatAsState(
            targetValue = if (isFrontSide) 0f else 180f,
            animationSpec = tween(durationMillis = 400),
            label = "CardRotation"
        )

        Spacer(modifier = Modifier.weight(0.1f))

        Card(
            onClick = { viewModel.flipFlashcard() },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .graphicsLayer {
                    rotationY = animatedRotation
                    cameraDistance = 14 * density
                }
                .testTag("flashcard_flip_surface"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (animatedRotation <= 90f) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (animatedRotation <= 90f) {
                    // Front side: The Question Text
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = activeJoint.question.category.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Text(
                            text = activeJoint.question.questionText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Text(
                            text = "👇 Click to reveal answer & explanation",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Back side: Answer & Explanation
                    Column(
                        modifier = Modifier
                            .graphicsLayer { rotationY = 180f },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Correct Answer ✅",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CorrectGreen,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = activeJoint.question.options[activeJoint.question.correctAnswerIndex],
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = activeJoint.question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // Progress indicators & arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .testTag("flashcard_prev"),
                onClick = { viewModel.prevFlashcard(filteredList.size) }
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Previous Card")
            }

            Text(
                text = "${currentIndex + 1} of ${filteredList.size}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            IconButton(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .testTag("flashcard_next"),
                onClick = { viewModel.nextFlashcard(filteredList.size) }
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next Card")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// STUDY TOOLS: MIND MAP DECK (CRITICAL USABILITY UPGRADE SUMMARY CARD)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MindMapModule(viewModel: NJPermitViewModel) {
    val activeBranch by viewModel.activeMindMapBranch.collectAsStateWithLifecycle()

    // 8 themed branches structure mapping essential knowledge numbers
    val branches = listOf(
        "gdl" to Pair("GP/GDL Rules", Icons.Default.Menu),
        "alcohol" to Pair("Alcohol Rules", Icons.Default.Warning),
        "parking" to Pair("Parking Distances", Icons.Default.Info),
        "speed" to Pair("Driving Speeds", Icons.Default.PlayArrow),
        "signs" to Pair("Road Signs", Icons.Default.Warning),
        "curves" to Pair("Curves & Forces", Icons.Default.PlayArrow),
        "sharing" to Pair("Sharing the Road", Icons.Default.Info),
        "seats" to Pair("Seat Belts", Icons.Default.List)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mind_map_module")
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Click any themed node to open the official NJ knowledge permit breakdown. This covers all key numerical rules instantly.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            branches.forEach { (key, pair) ->
                val isSelected = activeBranch == key
                InputChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) viewModel.selectMindMapBranch(null)
                        else viewModel.selectMindMapBranch(key)
                    },
                    label = { Text(pair.first, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    avatar = {
                        Icon(
                            imageVector = pair.second,
                            contentDescription = pair.first,
                            modifier = Modifier.size(16.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = activeBranch != null,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            val contentTitle: String
            val contentPoints: List<String>
            val colorAccent: Color

            when (activeBranch) {
                "gdl" -> {
                    contentTitle = "Probationary & GDL Constraints"
                    colorAccent = MaterialTheme.colorScheme.primary
                    contentPoints = listOf(
                        "🔴 Red decals are absolute requirements for license plates under age 21. Violation costs $100.",
                        "🌙 Limited driving hours: Forbidden strictly between 11:01 PM and 5:00 AM.",
                        "👥 Passenger cap: Restrained to dependents & one additional customer, unless assisted by dual supervising parent/guardian.",
                        "🚘 Supervising driver: Minimum 21 years old, licensed for 3 consecutive years in New Jersey."
                    )
                }
                "alcohol" -> {
                    contentTitle = "Sobering Regulations & DUI Limits"
                    colorAccent = IncorrectRed
                    contentPoints = listOf(
                        "🔞 Underage DUI threshold is absolute zero tolerance: 0.01% BAC or above.",
                        "🍺 Standard legal DUI limit for age 21 and older: 0.08% BAC or more.",
                        "🗣️ Implied Consent: In NJ, refusal of breath test triggers first offence DUI 0.10% equivalent surcharges & strict interlocks.",
                        "✖️ Blow risk multiplies: 0.05% doubles the accident chance, 0.10% increases it six-fold (6x), 0.15% increases it 25-fold (25x)."
                    )
                }
                "parking" -> {
                    contentTitle = "Official Stopping & Parking Distances"
                    colorAccent = MaterialTheme.colorScheme.tertiary
                    contentPoints = listOf(
                        "🧯 Fire Hydrant parking safety window: Minimum 10 feet clearance.",
                        "🚶 Pedestrian crosswalk limit at intersection: Minimum 25 feet.",
                        "🛑 Stop sign or active traffic regulator: Minimum 50 feet away.",
                        "📐 Downhill: Wheels turned toward curb; Uphill: wheels turned away."
                    )
                }
                "speed" -> {
                    contentTitle = "New Jersey Road Speed Zones"
                    colorAccent = CorrectGreen
                    contentPoints = listOf(
                        "🏫 School districts, active businesses, residential lanes: Strict default at 25 mph.",
                        "🏡 Suburban residential & custom marked sections: Standard 35 mph.",
                        "🌲 State highways & non-posted municipal roadways: High comfort limit of 50 mph.",
                        "🛣️ Major interstate expressways & turnpike systems: Limit posted 55 or 65 mph."
                    )
                }
                "signs" -> {
                    contentTitle = "Traffic Sign Shapes & Meanings"
                    colorAccent = MaterialTheme.colorScheme.secondary
                    contentPoints = listOf(
                        "🛑 Octagonal red (8-sided) devices are used exclusively for STOP actions.",
                        "⚠️ Diamond yellow sheets signify Warning or alert hazards ahead.",
                        "🔻 Triangular red-white outline represents YIELD operations.",
                        "⚡ Circular yellow icons warn you exclusively of coming Railroad crossings."
                    )
                }
                "curves" -> {
                    contentTitle = "Safe Curve Navigation"
                    colorAccent = MaterialTheme.colorScheme.primary
                    contentPoints = listOf(
                        "➡️ The centrifugal force will naturally drift a vehicle to keep going straight inside a bend.",
                        "🐢 Always slow down your vehicle before entering the curve to prevent friction sliding.",
                        "🚫 Never apply heavy panic brakes inside the peak curve sweep."
                    )
                }
                "sharing" -> {
                    contentTitle = "Sharing the Road Fairly"
                    colorAccent = MaterialTheme.colorScheme.primary
                    contentPoints = listOf(
                        "🚴 Bicyclists, skateboarders, and inline rollers bear the exact same rights & duties as motor cars.",
                        "🚌 Yield the line to active postal services or transit buses sliding into corridors.",
                        "🚨 Yield to police / ambulances from behind instantly by stopping at the extreme right road shoulder."
                    )
                }
                "seats" -> {
                    contentTitle = "Seat Belt Obligations & Safety rules"
                    colorAccent = MaterialTheme.colorScheme.secondary
                    contentPoints = listOf(
                        "🔗 Safety seat belts are primary offences for all front and back passengers in New Jersey.",
                        "👦 Drivers are fully accountable for all passengers under age 18.",
                        "🍼 Correct booster seats are required for infants."
                    )
                }
                else -> {
                    contentTitle = ""
                    colorAccent = Color.Gray
                    contentPoints = emptyList()
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                border = BorderStroke(1.5.dp, colorAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = contentTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { viewModel.selectMindMapBranch(null) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close detailed sheet")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    contentPoints.forEach { point ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = point,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// STUDY TOOLS: CATEGORY DRILLS SUBSECTION
@Composable
fun CategoryDrillsModule(
    viewModel: NJPermitViewModel,
    questions: List<QuestionWithProgress>
) {
    // Collect progress metrics grouped by category
    val categories = questions.map { it.question.category }.distinct()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("category_drills_module"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Targeted drills split by the chapters of the NJ MVC Driver Manual:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        items(categories) { category ->
            val subset = questions.filter { it.question.category == category }
            val correctCount = subset.count { it.isMastered }
            val totalCount = subset.size
            val completionPercent = if (totalCount > 0) correctCount.toFloat() / totalCount else 0f

            Card(
                onClick = { viewModel.startCategoryDrill(category) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_drill_item_$category"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$totalCount Practice Questions • $correctCount Mastered",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { completionPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Drill",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// SEARCH TAB: SEARCH ENGINE
@Composable
fun SearchDashboard(
    viewModel: NJPermitViewModel,
    questions: List<QuestionWithProgress>
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    val filteredList = remember(searchQuery, questions) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            questions.filter { joint ->
                joint.question.questionText.contains(searchQuery, ignoreCase = true) ||
                        joint.question.explanation.contains(searchQuery, ignoreCase = true) ||
                        joint.question.options.any { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_dashboard")
            .padding(16.dp)
    ) {
        NJHeaderSection(
            title = "Search traffic rules",
            subtitle = "Syllabus search engine on 50 questions"
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("questions_search_input"),
            placeholder = { Text("Type any search keyword e.g. 'decals', 'BAC', 'hydrant'...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search query")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search query empty",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Instant search",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                    Text(
                        text = "Enter GDL numbers or words to see real answers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            Text(
                text = "${filteredList.size} Matching Results Found",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList) { joint ->
                    SearchMatchCard(joint = joint, viewModel = viewModel)
                }
            }
        }
    }
}

// SEARCH: RENDERED CARD DETAILED LOOKUP
@Composable
fun SearchMatchCard(joint: QuestionWithProgress, viewModel: NJPermitViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("search_match_${joint.question.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = joint.question.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row {
                    IconButton(
                        onClick = { viewModel.toggleFlagOnCurrent(joint.question.id, joint.isFlagged) }
                    ) {
                        Icon(
                            imageVector = if (joint.isFlagged) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = "Flag question",
                            tint = if (joint.isFlagged) MaterialTheme.colorScheme.tertiary else Color.LightGray
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleMasteredOnCurrent(joint.question.id, joint.isMastered) }
                    ) {
                        Icon(
                            imageVector = if (joint.isMastered) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = "Master question",
                            tint = if (joint.isMastered) CorrectGreen else Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = joint.question.questionText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Display options list with highlight on correct index
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                joint.question.options.forEachIndexed { idx, option ->
                    val isCorrect = idx == joint.question.correctAnswerIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isCorrect) CorrectGreen.copy(alpha = 0.12f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = if (isCorrect) CorrectGreen else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCorrect) CorrectGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Explanation:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = joint.question.explanation,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

// PROGRESS DIAGNOSTICS TAB
@Composable
fun DiagnosticsDashboard(
    viewModel: NJPermitViewModel,
    questions: List<QuestionWithProgress>,
    examResults: List<MockExamResult>
) {
    val showResetDialog = remember { mutableStateOf(false) }

    val totalCount = questions.size
    val masteredCount = questions.count { it.isMastered }
    val flaggedCount = questions.count { it.isFlagged }
    val wrongCount = questions.count { it.timesIncorrect > 0 }
    val answeredCorrectlyPercent = if (totalCount > 0) (masteredCount.toFloat() / totalCount * 100).roundToInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("diagnostics_dashboard")
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NJHeaderSection(
                title = "Progress Diagnostics",
                subtitle = "Track mock records & analytics logs"
            )
        }

        // Metrics breakdown widget
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Diagnostics Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$answeredCorrectlyPercent%",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(text = "Overall Mastered", style = MaterialTheme.typography.bodySmall)
                        }

                        VerticalDivider(
                            modifier = Modifier.height(48.dp),
                            color = MaterialTheme.colorScheme.outline
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = wrongCount.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (wrongCount > 0) IncorrectRed else CorrectGreen
                            )
                            Text(text = "Wrong Questions", style = MaterialTheme.typography.bodySmall)
                        }

                        VerticalDivider(
                            modifier = Modifier.height(48.dp),
                            color = MaterialTheme.colorScheme.outline
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = flaggedCount.toString(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(text = "Flagged Stars", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        // Exam history
        item {
            Text(
                text = "📝 Mock Exam History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (examResults.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No mock exams recorded yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Take a Mock Exam to measure progress.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        } else {
            items(examResults) { result ->
                val dateStr = remember(result.date) {
                    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                    sdf.format(Date(result.date))
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Score: ${result.score}/50 (${(result.score.toFloat() / 50 * 100).roundToInt()}%)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Duration: ${result.elapsedSeconds / 60}m ${result.elapsedSeconds % 60}s",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                text = dateStr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (result.isPass) CorrectGreen.copy(alpha = 0.15f) else IncorrectRed.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (result.isPass) "PASSED ✅" else "FAILED ❌",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Black,
                                color = if (result.isPass) CorrectGreen else IncorrectRed,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Hard reset history button
        item {
            Button(
                onClick = { showResetDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = IncorrectRed.copy(alpha = 0.1f)),
                border = BorderStroke(1.dp, IncorrectRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_reset_history_button")
                    .padding(vertical = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear all",
                    tint = IncorrectRed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Learning & Scoring History", color = IncorrectRed, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showResetDialog.value) {
        AlertDialog(
            onDismissRequest = { showResetDialog.value = false },
            title = { Text("Reset learning logs?") },
            text = { Text("This will permanently clear your high score results, flagged bookmarks, and wrong mistakes collection. This operation cannot be undone.") },
            confirmButton = {
                TextButton(
                    modifier = Modifier.testTag("confirm_reset_action"),
                    onClick = {
                        viewModel.resetLearningHistory()
                        showResetDialog.value = false
                    }
                ) {
                    Text("Reset Everything", color = IncorrectRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// SHARED VIEW ELEMENT: HEADER STYLISTICS
@Composable
fun NJHeaderSection(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
        )
        Spacer(modifier = Modifier.height(14.dp))
    }
}

// SHARED VIEW ELEMENT: EXCEL METRIC WIDGET BLOCK
@Composable
fun GridMiniMetric(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

// STUDY SCREENS: SEQUENTIAL MODE COMPOSE
@Composable
fun SequentialStudyScreen(
    viewModel: NJPermitViewModel,
    questions: List<QuestionWithProgress>,
    currentIndex: Int,
    selectedOption: Int?,
    hasSubmitted: Boolean
) {
    if (questions.isEmpty()) return
    val joint = questions[currentIndex]

    StudyModeSkeleton(
        title = "Sequential Study Mode",
        progressText = "Question ${currentIndex + 1} of ${questions.size}",
        progressPercent = (currentIndex + 1).toFloat() / questions.size,
        joint = joint,
        selectedOption = selectedOption,
        hasSubmitted = hasSubmitted,
        isFirst = currentIndex == 0,
        isLast = currentIndex == questions.size - 1,
        onSelect = { viewModel.selectOption(it) },
        onSubmit = { viewModel.submitAnswer(joint) },
        onPrev = { viewModel.goToPreviousQuestion() },
        onNext = { viewModel.goToNextQuestion(questions.size) },
        onFlagToggle = { viewModel.toggleFlagOnCurrent(joint.question.id, joint.isFlagged) },
        onMasterToggle = { viewModel.toggleMasteredOnCurrent(joint.question.id, joint.isMastered) },
        onExit = { viewModel.cancelActiveMode() }
    )
}

// STUDY SCREENS: REFRESHER / WRONG MISTAKEN SCREEN
@Composable
fun RefresherScreen(
    viewModel: NJPermitViewModel,
    questionsList: List<QuestionWithProgress>,
    currentIndex: Int,
    selectedOption: Int?,
    hasSubmitted: Boolean
) {
    if (questionsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Refresher list is empty!")
        }
        return
    }
    val joint = questionsList[currentIndex]

    StudyModeSkeleton(
        title = "Review Mistakes",
        progressText = "Mistake ${currentIndex + 1} of ${questionsList.size}",
        progressPercent = (currentIndex + 1).toFloat() / questionsList.size,
        joint = joint,
        selectedOption = selectedOption,
        hasSubmitted = hasSubmitted,
        isFirst = currentIndex == 0,
        isLast = currentIndex == questionsList.size - 1,
        onSelect = { viewModel.selectOption(it) },
        onSubmit = { viewModel.submitAnswer(joint) },
        onPrev = { viewModel.goToPreviousQuestion() },
        onNext = { viewModel.goToNextQuestion(questionsList.size) },
        onFlagToggle = { viewModel.toggleFlagOnCurrent(joint.question.id, joint.isFlagged) },
        onMasterToggle = { viewModel.toggleMasteredOnCurrent(joint.question.id, joint.isMastered) },
        onExit = { viewModel.cancelActiveMode() }
    )
}

// STUDY SCREENS: FLAGGED SCREEN
@Composable
fun FlaggedSessionScreen(
    viewModel: NJPermitViewModel,
    questionsList: List<QuestionWithProgress>,
    currentIndex: Int,
    selectedOption: Int?,
    hasSubmitted: Boolean
) {
    if (questionsList.isEmpty()) return
    val joint = questionsList[currentIndex]

    StudyModeSkeleton(
        title = "Flagged Cards Quiz",
        progressText = "Flagged ${currentIndex + 1} of ${questionsList.size}",
        progressPercent = (currentIndex + 1).toFloat() / questionsList.size,
        joint = joint,
        selectedOption = selectedOption,
        hasSubmitted = hasSubmitted,
        isFirst = currentIndex == 0,
        isLast = currentIndex == questionsList.size - 1,
        onSelect = { viewModel.selectOption(it) },
        onSubmit = { viewModel.submitAnswer(joint) },
        onPrev = { viewModel.goToPreviousQuestion() },
        onNext = { viewModel.goToNextQuestion(questionsList.size) },
        onFlagToggle = { viewModel.toggleFlagOnCurrent(joint.question.id, joint.isFlagged) },
        onMasterToggle = { viewModel.toggleMasteredOnCurrent(joint.question.id, joint.isMastered) },
        onExit = { viewModel.cancelActiveMode() }
    )
}

// STUDY SCREENS: CATEGORY DRILL SCREEN
@Composable
fun CategoryDrillScreen(
    viewModel: NJPermitViewModel,
    categoryName: String,
    questionsList: List<QuestionWithProgress>,
    currentIndex: Int,
    selectedOption: Int?,
    hasSubmitted: Boolean
) {
    if (questionsList.isEmpty()) return
    val joint = questionsList[currentIndex]

    StudyModeSkeleton(
        title = "Drills: $categoryName",
        progressText = "Drill ${currentIndex + 1} of ${questionsList.size}",
        progressPercent = (currentIndex + 1).toFloat() / questionsList.size,
        joint = joint,
        selectedOption = selectedOption,
        hasSubmitted = hasSubmitted,
        isFirst = currentIndex == 0,
        isLast = currentIndex == questionsList.size - 1,
        onSelect = { viewModel.selectOption(it) },
        onSubmit = { viewModel.submitAnswer(joint) },
        onPrev = { viewModel.goToPreviousQuestion() },
        onNext = { viewModel.goToNextQuestion(questionsList.size) },
        onFlagToggle = { viewModel.toggleFlagOnCurrent(joint.question.id, joint.isFlagged) },
        onMasterToggle = { viewModel.toggleMasteredOnCurrent(joint.question.id, joint.isMastered) },
        onExit = { viewModel.cancelActiveMode() }
    )
}

// STUDY SCREENS: MINI QUIZ SESSION
@Composable
fun MiniQuizScreen(
    viewModel: NJPermitViewModel,
    quizNumber: Int,
    questionsList: List<QuestionWithProgress>,
    currentIndex: Int,
    selectedOption: Int?,
    hasSubmitted: Boolean
) {
    if (questionsList.isEmpty()) return
    val joint = questionsList[currentIndex]

    StudyModeSkeleton(
        title = "Mini-Quiz $quizNumber",
        progressText = "Question ${currentIndex + 1} of 10",
        progressPercent = (currentIndex + 1).toFloat() / 10,
        joint = joint,
        selectedOption = selectedOption,
        hasSubmitted = hasSubmitted,
        isFirst = currentIndex == 0,
        isLast = currentIndex == questionsList.size - 1,
        onSelect = { viewModel.selectOption(it) },
        onSubmit = { viewModel.submitAnswer(joint) },
        onPrev = { viewModel.goToPreviousQuestion() },
        onNext = { viewModel.goToNextQuestion(questionsList.size) },
        onFlagToggle = { viewModel.toggleFlagOnCurrent(joint.question.id, joint.isFlagged) },
        onMasterToggle = { viewModel.toggleMasteredOnCurrent(joint.question.id, joint.isMastered) },
        onExit = { viewModel.cancelActiveMode() }
    )
}

// CORE SHARED STUDY SKELETON (CLEAN REUSABLE TEMPLATE FOR DRILL LAYOUTS)
@Composable
fun StudyModeSkeleton(
    title: String,
    progressText: String,
    progressPercent: Float,
    joint: QuestionWithProgress,
    selectedOption: Int?,
    hasSubmitted: Boolean,
    isFirst: Boolean,
    isLast: Boolean,
    onSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onFlagToggle: () -> Unit,
    onMasterToggle: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("study_mode_skeleton")
    ) {
        // Nav header row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onExit) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Exit study session")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Flags/Bookmarks
            Row {
                IconButton(onClick = onFlagToggle) {
                    Icon(
                        imageVector = if (joint.isFlagged) Icons.Default.Star else Icons.Outlined.Star,
                        contentDescription = "Flag question",
                        tint = if (joint.isFlagged) MaterialTheme.colorScheme.tertiary else Color.LightGray
                    )
                }
                IconButton(onClick = onMasterToggle) {
                    Icon(
                        imageVector = if (joint.isMastered) Icons.Default.CheckCircle else Icons.Default.Check,
                        contentDescription = "Master question",
                        tint = if (joint.isMastered) CorrectGreen else Color.LightGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Linear Progress bar indicator
        LinearProgressIndicator(
            progress = { progressPercent },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Scrollable Question details sheet
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category badge
            Surface(
                modifier = Modifier.align(Alignment.Start),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = joint.question.category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Question text
            Text(
                text = joint.question.questionText,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                lineHeight = 28.sp,
                modifier = Modifier.testTag("study_question_text")
            )

            // Multiple choice buttons list
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                joint.question.options.forEachIndexed { index, option ->
                    val isSelected = selectedOption == index
                    val isCorrect = index == joint.question.correctAnswerIndex

                    val surfaceColor = when {
                        hasSubmitted && isCorrect -> CorrectGreen.copy(alpha = 0.15f)
                        hasSubmitted && isSelected && !isCorrect -> IncorrectRed.copy(alpha = 0.15f)
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val outlineColor = when {
                        hasSubmitted && isCorrect -> CorrectGreen
                        hasSubmitted && isSelected && !isCorrect -> IncorrectRed
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline
                    }

                    Card(
                        onClick = { onSelect(index) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("option_button_$index")
                            .minimumInteractiveComponentSize(),
                        colors = CardDefaults.cardColors(containerColor = surfaceColor),
                        border = BorderStroke(1.5.dp, outlineColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, outlineColor, CircleShape)
                                    .background(
                                        if (isSelected) outlineColor else Color.Transparent,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected || (hasSubmitted && isCorrect)) {
                                    Icon(
                                        imageVector = if (hasSubmitted && !isCorrect) Icons.Default.Close else Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            // Answer submission confirmation / explanation view segment
            if (selectedOption != null && !hasSubmitted) {
                Button(
                    onClick = onSubmit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_answer_button")
                        .padding(top = 10.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Check Answer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            AnimatedVisibility(
                visible = hasSubmitted,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val isAnswerCorrect = selectedOption == joint.question.correctAnswerIndex
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isAnswerCorrect) CorrectGreen.copy(alpha = 0.08f) else IncorrectRed.copy(alpha = 0.08f),
                            RoundedCornerShape(16.dp)
                        )
                        .border(
                            1.dp,
                            if (isAnswerCorrect) CorrectGreen.copy(alpha = 0.3f) else IncorrectRed.copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isAnswerCorrect) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = "Answer result check",
                            tint = if (isAnswerCorrect) CorrectGreen else IncorrectRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAnswerCorrect) "Correct! Excellent" else "Incorrect Rule",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isAnswerCorrect) CorrectGreen else IncorrectRed
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = joint.question.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Left/Right navigate controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onPrev,
                enabled = !isFirst,
                modifier = Modifier.testTag("prev_question_arrow_btn")
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous question")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Previous")
            }

            TextButton(
                onClick = onNext,
                enabled = !isLast && hasSubmitted,
                modifier = Modifier.testTag("next_question_arrow_btn")
            ) {
                Text("Next")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next question")
            }
        }
    }
}

// REAL SIMULATION TIMED MOCK EXAM VIEW SCREEN
@Composable
fun MockExamScreen(
    viewModel: NJPermitViewModel,
    examState: PracticeMode.MockExam,
    currentIndex: Int
) {
    val showCancelDialog = remember { mutableStateOf(false) }

    val activeQuestion = examState.questions[currentIndex]
    val selectedOptionIndexForCurrent = examState.answers[activeQuestion.question.id]

    // Formulate remaining minutes & seconds from countdown limit (1200 seconds is 20 minutes)
    val totalTimeSeconds = 1200
    val secondsRemaining = totalTimeSeconds - examState.elapsedSeconds
    val mins = (secondsRemaining / 60).coerceAtLeast(0)
    val secs = (secondsRemaining % 60).coerceAtLeast(0)
    val timerString = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("mock_exam_screen")
    ) {
        // Exam header view row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Timed Mock Exam",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Question ${currentIndex + 1} of 50",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (mins < 3) IncorrectRed.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Countdown clock",
                        tint = if (mins < 3) IncorrectRed else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = timerString,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = if (mins < 3) IncorrectRed else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar status
        val progressVal = (currentIndex + 1).toFloat() / 50
        LinearProgressIndicator(
            progress = { progressVal },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (!examState.isSubmitted) {
            // Main exam question view
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = activeQuestion.question.questionText,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 28.sp
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    activeQuestion.question.options.forEachIndexed { index, option ->
                        val isSelected = selectedOptionIndexForCurrent == index
                        Card(
                            onClick = { viewModel.selectOption(index) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mock_option_$index"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.selectOption(index) },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer pagination + finish exam
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    TextButton(
                        onClick = { viewModel.goToPreviousQuestion() },
                        enabled = currentIndex > 0
                    ) {
                        Text("Prev")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = { viewModel.goToNextQuestion(50) },
                        enabled = currentIndex < 49
                    ) {
                        Text("Next")
                    }
                }

                // Finish Exam Dialog trigger button
                val answeredCount = examState.answers.size
                Button(
                    onClick = { viewModel.submitMockExam() },
                    colors = ButtonDefaults.buttonColors(containerColor = CorrectGreen),
                    modifier = Modifier.testTag("finish_mock_exam_submit")
                ) {
                    Text(
                        text = "Submit ($answeredCount/50)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Close button dialog helper
            TextButton(
                onClick = { showCancelDialog.value = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag("abandon_exam_btn")
            ) {
                Text("Abandon Exam", color = IncorrectRed)
            }
        } else {
            // Exam is submitted! RENDER THE REVIEW DECK METRICS REPORT
            val correctNumber = examState.finalScore
            val passRate = correctNumber >= 40

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            if (passRate) CorrectGreen.copy(alpha = 0.12f) else IncorrectRed.copy(alpha = 0.12f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (passRate) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (passRate) CorrectGreen else IncorrectRed,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Text(
                    text = if (passRate) "Congratulations! Passed" else "Failed • Study More",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = if (passRate) CorrectGreen else IncorrectRed
                )

                Text(
                    text = "You scored $correctNumber out of 50 correct replies (${(correctNumber.toFloat() / 50 * 100).roundToInt()}%).\nPassing margin is correct in 40/50 (80%).",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Exam Answer Review Sheet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Navigate using the Question index below to view detailed correct answers and explanations for this mock attempt.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider()

                // Current question lookups inside feedback mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Review Question ${currentIndex + 1}:",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row {
                        IconButton(
                            onClick = { viewModel.goToPreviousQuestion() },
                            enabled = currentIndex > 0
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Prev question")
                        }
                        IconButton(
                            onClick = { viewModel.goToNextQuestion(50) },
                            enabled = currentIndex < 49
                        ) {
                            Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next question")
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    val userAnsIdx = examState.answers[activeQuestion.question.id]
                    val isUserSelectionCorrect = userAnsIdx == activeQuestion.question.correctAnswerIndex

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = activeQuestion.question.questionText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        activeQuestion.question.options.forEachIndexed { opIdx, opText ->
                            val isCorrectAnswer = opIdx == activeQuestion.question.correctAnswerIndex
                            val wasUserSelected = opIdx == userAnsIdx

                            val backgroundPillColor = when {
                                isCorrectAnswer -> CorrectGreen.copy(alpha = 0.15f)
                                wasUserSelected && !isCorrectAnswer -> IncorrectRed.copy(alpha = 0.15f)
                                else -> Color.Transparent
                            }

                            val borderPillColor = when {
                                isCorrectAnswer -> CorrectGreen
                                wasUserSelected && !isCorrectAnswer -> IncorrectRed
                                else -> Color.Transparent
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundPillColor, RoundedCornerShape(8.dp))
                                    .border(1.dp, borderPillColor, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isCorrectAnswer) Icons.Default.CheckCircle else if (wasUserSelected) Icons.Default.Close else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isCorrectAnswer) CorrectGreen else if (wasUserSelected) IncorrectRed else Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = opText,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = if (isCorrectAnswer || wasUserSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCorrectAnswer) CorrectGreen else if (wasUserSelected) IncorrectRed else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Explanation:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = activeQuestion.question.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { viewModel.cancelActiveMode() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exit_mock_exam_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Exit Exam Review Menu", fontWeight = FontWeight.Black)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showCancelDialog.value) {
        AlertDialog(
            onDismissRequest = { showCancelDialog.value = false },
            title = { Text("Abandon Mock Exam?") },
            text = { Text("Are you sure you want to exit and abandon this mock session? Your current scores on this attempt will not be committed to memory.") },
            confirmButton = {
                TextButton(
                    modifier = Modifier.testTag("abandon_exam_confirm"),
                    onClick = {
                        viewModel.cancelActiveMode()
                        showCancelDialog.value = false
                    }
                ) {
                    Text("Abandon", color = IncorrectRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog.value = false }) {
                    Text("Continue Exam")
                }
            }
        )
    }
}

// SHARED VIEW ELEMENT: LARGE WIDE MODE CARDS
@Composable
fun FullWidthModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    badgeText: String,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                lineHeight = 20.sp
            )
        }
    }
}

// SHARED VIEW ELEMENT: ADAPTIVE DOUBLE SQUARED DENT CARD
@Composable
fun GridModeCard(
    title: String,
    count: Int,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        onClick = { if (enabled) onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        border = BorderStroke(
            1.dp,
            if (enabled) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (enabled) iconColor.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) iconColor else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (enabled) iconColor.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        color = if (enabled) iconColor else Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                lineHeight = 16.sp
            )
        }
    }
}
