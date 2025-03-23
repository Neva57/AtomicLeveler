package com.example.atomicleveler.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.atomicleveler.data.converters.Converters
import com.example.atomicleveler.data.dao.AchievementDao
import com.example.atomicleveler.data.dao.HabitDao
import com.example.atomicleveler.data.dao.UserProfileDao
import com.example.atomicleveler.data.models.Achievement
import com.example.atomicleveler.data.models.Habit
import com.example.atomicleveler.data.models.UserProfile

/**
 * Main database class for the application
 */
@Database(
    entities = [Habit::class, Achievement::class, UserProfile::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "atomic_leveler_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}