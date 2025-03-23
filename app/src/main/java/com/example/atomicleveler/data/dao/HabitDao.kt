package com.example.atomicleveler.data.dao

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit_table ORDER BY createdDate DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)
}