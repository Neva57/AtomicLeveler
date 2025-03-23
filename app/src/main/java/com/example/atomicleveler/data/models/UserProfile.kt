package com.example.atomicleveler.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a user profile
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1, // Single user for now
    val name: String,
    val level: Int = 1,
    val experience: Int = 0,
    val totalHabitsCompleted: Int = 0,
    val joinDate: Long = System.currentTimeMillis()
)