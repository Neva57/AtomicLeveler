package com.example.atomicleveler.data.achievements

import com.example.atomicleveler.R
import com.example.atomicleveler.data.models.Achievement
import com.example.atomicleveler.data.models.Habit
import com.example.atomicleveler.data.models.UserProfile
import com.example.atomicleveler.data.repository.AchievementRepository
import com.example.atomicleveler.data.repository.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AchievementManager(
    private val achievementRepository: AchievementRepository,
    private val userProfileRepository: UserProfileRepository,
    private val coroutineScope: CoroutineScope
) {
    suspend fun initializeAchievements() {
        val allAchievements = achievementRepository.getAllAchievements()
        if (allAchievements.isEmpty()) {
            // Add default achievements if none exist
            val defaultAchievements = listOf(
                Achievement(
                    title = "Getting Started",
                    description = "Create your first habit",
                    iconResId = R.drawable.ic_achievement_basic,
                    experiencePoints = 10
                ),
                Achievement(
                    title = "Consistency Champion",
                    description = "Maintain a 7-day streak on any habit",
                    iconResId = R.drawable.ic_achievement_streak,
                    experiencePoints = 50
                ),
                Achievement(
                    title = "Habit Master",
                    description = "Complete any habit 30 times",
                    iconResId = R.drawable.ic_achievement_master,
                    experiencePoints = 100
                ),
                Achievement(
                    title = "Diversifier",
                    description = "Create 5 different habits",
                    iconResId = R.drawable.ic_achievement_diversity,
                    experiencePoints = 75
                )
                // Add more achievements as needed
            )

            defaultAchievements.forEach { achievement ->
                achievementRepository.insert(achievement)
            }
        }
    }

    suspend fun checkHabitAchievements(habits: List<Habit>, userProfile: UserProfile) {
        coroutineScope.launch {
            val achievements = achievementRepository.getAllAchievements()
            val updatedAchievements = mutableListOf<Achievement>()
            var totalXpGained = 0

            // Check "Getting Started" achievement
            val gettingStarted = achievements.find { it.title == "Getting Started" }
            if (gettingStarted != null && !gettingStarted.isUnlocked && habits.isNotEmpty()) {
                updatedAchievements.add(
                    gettingStarted.copy(
                        isUnlocked = true,
                        unlockedDate = System.currentTimeMillis()
                    )
                )
                totalXpGained += gettingStarted.experiencePoints
            }

            // Check "Consistency Champion" achievement
            val streakAchievement = achievements.find { it.title == "Consistency Champion" }
            if (streakAchievement != null && !streakAchievement.isUnlocked) {
                val hasSevenDayStreak = habits.any { it.currentStreak >= 7 }
                if (hasSevenDayStreak) {
                    updatedAchievements.add(
                        streakAchievement.copy(
                            isUnlocked = true,
                            unlockedDate = System.currentTimeMillis()
                        )
                    )
                    totalXpGained += streakAchievement.experiencePoints
                }
            }

            // Check "Habit Master" achievement
            val masterAchievement = achievements.find { it.title == "Habit Master" }
            if (masterAchievement != null && !masterAchievement.isUnlocked) {
                val has30Completions = habits.any { it.completionDates.size >= 30 }
                if (has30Completions) {
                    updatedAchievements.add(
                        masterAchievement.copy(
                            isUnlocked = true,
                            unlockedDate = System.currentTimeMillis()
                        )
                    )
                    totalXpGained += masterAchievement.experiencePoints
                }
            }

            // Check "Diversifier" achievement
            val diversityAchievement = achievements.find { it.title == "Diversifier" }
            if (diversityAchievement != null && !diversityAchievement.isUnlocked) {
                if (habits.size >= 5) {
                    updatedAchievements.add(
                        diversityAchievement.copy(
                            isUnlocked = true,
                            unlockedDate = System.currentTimeMillis()
                        )
                    )
                    totalXpGained += diversityAchievement.experiencePoints
                }
            }

            // Update achievements in database
            if (updatedAchievements.isNotEmpty()) {
                updatedAchievements.forEach { achievement ->
                    achievementRepository.update(achievement)
                }

                // Award XP to the user
                if (totalXpGained > 0) {
                    val updatedProfile = userProfile.copy(
                        experience = userProfile.experience + totalXpGained
                    )
                    userProfileRepository.update(updatedProfile)
                }
            }
        }
    }

    // Method to get newly unlocked achievements since last check
    suspend fun getNewlyUnlockedAchievements(lastCheckTime: Long): List<Achievement> {
        return withContext(Dispatchers.IO) {
            achievementRepository.getUnlockedAchievements().filter {
                it.unlockedDate != null && it.unlockedDate > lastCheckTime
            }
        }
    }
}