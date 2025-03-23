package com.example.atomicleveler.data.models

data class Achievement(
    val id: Long,
    val title: String,
    val description: String,
    val iconResId: Int,
    val experiencePoints: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null
)