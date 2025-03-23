package com.example.atomicleveler.data.dao

import androidx.room.*
import com.example.atomicleveler.data.models.Achievement

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement_table ORDER BY id ASC")
    suspend fun getAllAchievements(): List<Achievement>

    @Query("SELECT * FROM achievement_table WHERE isUnlocked = 1")
    suspend fun getUnlockedAchievements(): List<Achievement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: Achievement): Long

    @Update
    suspend fun update(achievement: Achievement)
}