package com.example.atomicleveler

import android.app.Application
import com.example.atomicleveler.data.database.AppDatabase
import com.example.atomicleveler.data.models.UserProfile
import com.example.atomicleveler.data.repository.UserProfileRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application class for initializing global components
 */
class AtomicLevelerApplication : Application() {

    // Application scope for coroutines
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Lazy database instance
    val database by lazy {
        AppDatabase.getDatabase(this)
    }

    // Repositories
    val habitRepository by lazy { com.example.atomicleveler.data.repository.HabitRepository(database.habitDao()) }
    val achievementRepository by lazy { com.example.atomicleveler.data.repository.AchievementRepository(database.achievementDao()) }
    val userProfileRepository by lazy { UserProfileRepository(database.userProfileDao()) }

    override fun onCreate() {
        super.onCreate()

        // Initialize user profile if it doesn't exist
        applicationScope.launch {
            val existingProfile = userProfileRepository.getUserProfile()

            if (existingProfile == null) {
                // Create default profile
                val defaultProfile = UserProfile(
                    name = "User",
                    level = 1,
                    experience = 0,
                    totalHabitsCompleted = 0
                )
                userProfileRepository.insert(defaultProfile)
            }
        }
    }
}