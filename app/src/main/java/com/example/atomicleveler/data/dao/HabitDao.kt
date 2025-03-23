package com.example.atomicleveler.data.dao

import androidx.room.*
import com.example.atomicleveler.data.models.Habit
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Habit table
 */
@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_table ORDER BY createdDate DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert
    fun insert(habit: Habit): Long  // Remove suspend

    @Update
    fun update(habit: Habit): Int   // Remove suspend

    @Delete
    fun delete(habit: Habit): Int   // Remove suspend
}