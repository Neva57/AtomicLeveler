package com.example.atomicleveler.data.dao

import androidx.room.*
import com.example.atomicleveler.data.models.UserProfile

/**
 * Data Access Object for the UserProfile table
 */
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): UserProfile?  // Remove suspend

    @Insert
    fun insert(userProfile: UserProfile): Long  // Remove suspend

    @Update
    fun update(userProfile: UserProfile): Int  // Remove suspend
}