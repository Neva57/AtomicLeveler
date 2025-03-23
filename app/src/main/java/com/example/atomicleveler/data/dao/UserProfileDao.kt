package com.example.atomicleveler.data.dao

import androidx.room.*
import com.example.atomicleveler.data.models.UserProfile

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userProfile: UserProfile): Long

    @Update
    suspend fun update(userProfile: UserProfile)
}