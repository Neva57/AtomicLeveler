package com.example.atomicleveler.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an achievement
 */
@Entity(tableName = "achievement_table")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val iconResId: Int,
    val experiencePoints: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null
)