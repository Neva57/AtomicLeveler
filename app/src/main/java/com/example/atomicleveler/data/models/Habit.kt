package com.example.atomicleveler.data.models

data class Habit(
    val id: Long = 0,
    val title: String,
    val description: String,
    val frequency: HabitFrequency,
    val createdDate: Long = System.currentTimeMillis(),
    val completionDates: List<Long> = emptyList(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
)
enum class HabitFrequency {
    DAILY, WEEKLY, CUSTOM
}