package com.example.atomicleveler.data.models

data class UserProfile(
    val id: Long = 1, // Single user for now
    val name: String,
    val level: Int = 1,
    val experience: Int = 0,
    val totalHabitsCompleted: Int = 0,
    val joinDate: Long = System.currentTimeMillis()
)