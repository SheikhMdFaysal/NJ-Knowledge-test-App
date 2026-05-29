package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Question schema
data class NJQuestion(
    val id: Int,
    val category: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

// Room Entity for tracking user progress on each question
@Entity(tableName = "question_progress")
data class QuestionProgress(
    @PrimaryKey val questionId: Int,
    val isFlagged: Boolean = false,
    val isMastered: Boolean = false,
    val timesCorrect: Int = 0,
    val timesIncorrect: Int = 0,
    val lastSelectedOption: Int? = null
)

// Room Entity for storing mock exam history
@Entity(tableName = "mock_exam_results")
data class MockExamResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val score: Int,
    val totalQuestions: Int = 50,
    val elapsedSeconds: Int,
    val isPass: Boolean
)

@Dao
interface QuestionProgressDao {
    @Query("SELECT * FROM question_progress")
    fun getAllProgress(): Flow<List<QuestionProgress>>

    @Query("SELECT * FROM question_progress WHERE questionId = :id")
    suspend fun getProgressForQuestion(id: Int): QuestionProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: QuestionProgress)

    @Query("UPDATE question_progress SET isFlagged = :flagged WHERE questionId = :id")
    suspend fun updateFlagged(id: Int, flagged: Boolean)

    @Query("UPDATE question_progress SET isMastered = :mastered WHERE questionId = :id")
    suspend fun updateMastered(id: Int, mastered: Boolean)

    @Query("DELETE FROM question_progress")
    suspend fun clearAllProgress()
}

@Dao
interface MockExamDao {
    @Query("SELECT * FROM mock_exam_results ORDER BY date DESC")
    fun getAllExamResults(): Flow<List<MockExamResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: MockExamResult)

    @Query("DELETE FROM mock_exam_results")
    suspend fun clearExamResults()
}

// Convert lists to strings for Room database standard types if needed,
// but since NJQuestion is static and NOT a database table directly (only progress is stored in database),
// we don't need complex Room TypeConverters for list properties! This is a super elegant design
// that avoids Room setup complexity and keeps compile times lighting fast.

@Database(entities = [QuestionProgress::class, MockExamResult::class], version = 1, exportSchema = false)
abstract class NJPermitDatabase : RoomDatabase() {
    abstract fun questionProgressDao(): QuestionProgressDao
    abstract fun mockExamDao(): MockExamDao
}
