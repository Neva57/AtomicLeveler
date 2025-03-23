package com.example.atomicleveler.data.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Frequency options for habits
 */
enum class HabitFrequency {
    DAILY, WEEKLY
}

/**
 * Entity representing a habit
 */
@Entity(tableName = "habit_table")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val frequency: String, // Stored as string instead of enum for Room compatibility
    val createdDate: Long = System.currentTimeMillis(),
    val completionDatesStr: String = "", // Store as string for Room compatibility
    val currentStreak: Int = 0,
    val bestStreak: Int = 0
) {
    // This computed property is for convenience when working with the dates list
    @get:Ignore
    val completionDates: List<Long>
        get() = if (completionDatesStr.isEmpty()) {
            emptyList()
        } else {
            completionDatesStr.split(",").mapNotNull { it.toLongOrNull() }
        }
}