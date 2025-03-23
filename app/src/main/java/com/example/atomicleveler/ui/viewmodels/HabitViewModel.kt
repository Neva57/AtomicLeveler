package com.example.atomicleveler.ui.viewmodels

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: HabitRepository
    val allHabits: LiveData<List<Habit>>

    init {
        val habitDao = AppDatabase.getDatabase(application).habitDao()
        repository = HabitRepository(habitDao)
        allHabits = repository.allHabits.asLiveData()
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

        val updatedCompletionDates = habit.completionDates + today
        val updatedStreak = calculateStreak(updatedCompletionDates)
        val updatedBestStreak = maxOf(updatedStreak, habit.bestStreak)

        repository.update(habit.copy(
            completionDates = updatedCompletionDates,
            currentStreak = updatedStreak,
            bestStreak = updatedBestStreak
        ))
    }

    // This would be added to HabitViewModel.kt to replace the placeholder function

    private fun calculateStreak(completionDates: List<Long>): Int {
        if (completionDates.isEmpty()) return 0

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

        // Go backwards through the days
        while (true) {
            currentDate.add(Calendar.DAY_OF_YEAR, -1)

            // Check if we have a completion for the previous day
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