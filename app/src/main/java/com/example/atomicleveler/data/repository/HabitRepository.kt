package com.example.atomicleveler.data.repository

import com.example.atomicleveler.data.dao.HabitDao
import com.example.atomicleveler.data.models.Habit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for accessing habit data
 */
class HabitRepository(private val habitDao: HabitDao) {
    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun insert(habit: Habit): Long {
        return withContext(Dispatchers.IO) {
            habitDao.insert(habit)
        }
    }

    suspend fun update(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.update(habit)
        }
    }

    suspend fun delete(habit: Habit) {
        withContext(Dispatchers.IO) {
            habitDao.delete(habit)
        }
    }
}