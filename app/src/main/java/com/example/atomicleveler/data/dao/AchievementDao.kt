package com.example.atomicleveler.data.dao

import androidx.room.*
import com.example.atomicleveler.data.models.Achievement

/**
 * Data Access Object for the Achievement table
 */
@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievement_table ORDER BY id ASC")
    fun getAllAchievements(): List<Achievement>  // Remove suspend

    @Query("SELECT * FROM achievement_table WHERE isUnlocked = 1")
    fun getUnlockedAchievements(): List<Achievement>  // Remove suspend

    @Insert
    fun insert(achievement: Achievement): Long  // Remove suspend

    @Update
    fun update(achievement: Achievement): Int  // Remove suspend
}