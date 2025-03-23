package com.example.atomicleveler.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.atomicleveler.data.achievements.AchievementManager
import com.example.atomicleveler.data.database.AppDatabase
import com.example.atomicleveler.data.models.Habit
import com.example.atomicleveler.data.models.HabitFrequency
import com.example.atomicleveler.data.models.UserProfile
import com.example.atomicleveler.data.repository.AchievementRepository
import com.example.atomicleveler.data.repository.HabitRepository
import com.example.atomicleveler.data.repository.UserProfileRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HabitRepository
    private val achievementRepository: AchievementRepository
    private val userProfileRepository: UserProfileRepository
    private lateinit var achievementManager: AchievementManager

    val allHabits: LiveData<List<Habit>>

    init {
        val database = AppDatabase.getDatabase(application)
        val habitDao = database.habitDao()
        repository = HabitRepository(habitDao)
        achievementRepository = AchievementRepository(database.achievementDao())
        userProfileRepository = UserProfileRepository(database.userProfileDao())
        allHabits = repository.allHabits.asLiveData()

        achievementManager = AchievementManager(
            achievementRepository,
            userProfileRepository,
            viewModelScope
        )
    }

    fun insert(habit: Habit) = viewModelScope.launch {
        repository.insert(habit)
    }

    fun update(habit: Habit) = viewModelScope.launch {
        repository.update(habit)
    }

    fun delete(habit: Habit) = viewModelScope.launch {
        repository.delete(habit)
    }

    fun completeHabitToday(habit: Habit) = viewModelScope.launch {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // Get the current completion dates and add today
        val currentCompletionDates = habit.completionDatesStr.let {
            if (it.isEmpty()) emptyList() else it.split(",").mapNotNull { date -> date.toLongOrNull() }
        }
        val updatedCompletionDates = currentCompletionDates + today

        // Convert the list back to a comma-separated string
        val updatedCompletionDatesStr = updatedCompletionDates.joinToString(",")

        // Calculate streak using the list
        val updatedStreak = calculateStreak(updatedCompletionDates, habit.frequency)
        val updatedBestStreak = maxOf(updatedStreak, habit.bestStreak)

        repository.update(habit.copy(
            completionDatesStr = updatedCompletionDatesStr,
            currentStreak = updatedStreak,
            bestStreak = updatedBestStreak
        ))
    }

    fun checkAchievements(habits: List<Habit>, userProfile: UserProfile) {
        viewModelScope.launch {
            achievementManager.checkHabitAchievements(habits, userProfile)
        }
    }

    private fun calculateStreak(completionDates: List<Long>, frequencyStr: String): Int {
        if (completionDates.isEmpty()) return 0

        // Convert string to enum safely
        val frequency = try {
            HabitFrequency.valueOf(frequencyStr)
        } catch (e: Exception) {
            HabitFrequency.DAILY // Default to daily if conversion fails
        }

        // Sort dates in ascending order
        val sortedDates = completionDates.sorted()

        // Get today's date (start of day)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Check if the most recent completion is from today
        val mostRecentDate = sortedDates.last()
        val mostRecentCalendar = Calendar.getInstance().apply {
            timeInMillis = mostRecentDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (mostRecentCalendar.timeInMillis != today.timeInMillis) {
            // Most recent date is not today, check if it was yesterday
            today.add(Calendar.DAY_OF_YEAR, -1)
            if (mostRecentCalendar.timeInMillis != today.timeInMillis) {
                // Most recent completion was before yesterday - streak is broken
                return 0
            }
        }

        // Count streak from most recent day backwards
        var streak = 1 // Start with 1 for the most recent date
        var currentDate = mostRecentCalendar.clone() as Calendar

        val interval = when (frequency) {
            HabitFrequency.DAILY -> Calendar.DAY_OF_YEAR
            HabitFrequency.WEEKLY -> Calendar.WEEK_OF_YEAR
        }

        // Go backwards through the days/weeks
        while (true) {
            currentDate.add(interval, -1)

            // Check if we have a completion for the previous day/week
            val previousDayMillis = currentDate.timeInMillis
            val hasPreviousDayCompletion = sortedDates.any { date ->
                val cal = Calendar.getInstance().apply {
                    timeInMillis = date
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                cal.timeInMillis == previousDayMillis
            }

            if (hasPreviousDayCompletion) {
                streak++
            } else {
                // Streak is broken
                break
            }
        }

        return streak
    }
}