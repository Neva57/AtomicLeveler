package com.example.atomicleveler.data.repository

import com.example.atomicleveler.data.dao.AchievementDao
import com.example.atomicleveler.data.models.Achievement

class AchievementRepository(private val achievementDao: AchievementDao) {

    suspend fun getAllAchievements(): List<Achievement> {
        return achievementDao.getAllAchievements()
    }

    suspend fun getUnlockedAchievements(): List<Achievement> {
        return achievementDao.getUnlockedAchievements()
    }

    suspend fun insert(achievement: Achievement): Long {
        return achievementDao.insert(achievement)
    }

    suspend fun update(achievement: Achievement) {
        achievementDao.update(achievement)
    }
}