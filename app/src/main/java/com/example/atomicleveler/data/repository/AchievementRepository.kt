package com.example.atomicleveler.data.repository

import com.example.atomicleveler.data.dao.AchievementDao
import com.example.atomicleveler.data.models.Achievement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for accessing achievement data
 */
class AchievementRepository(private val achievementDao: AchievementDao) {

    suspend fun getAllAchievements(): List<Achievement> {
        return withContext(Dispatchers.IO) {
            achievementDao.getAllAchievements()
        }
    }

    suspend fun getUnlockedAchievements(): List<Achievement> {
        return withContext(Dispatchers.IO) {
            achievementDao.getUnlockedAchievements()
        }
    }

    suspend fun insert(achievement: Achievement): Long {
        return withContext(Dispatchers.IO) {
            achievementDao.insert(achievement)
        }
    }

    suspend fun update(achievement: Achievement) {
        withContext(Dispatchers.IO) {
            achievementDao.update(achievement)
        }
    }
}