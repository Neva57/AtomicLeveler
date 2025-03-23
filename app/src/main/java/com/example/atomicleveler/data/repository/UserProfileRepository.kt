package com.example.atomicleveler.data.repository

import com.example.atomicleveler.data.dao.UserProfileDao
import com.example.atomicleveler.data.models.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository for accessing user profile data
 */
class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    suspend fun getUserProfile(): UserProfile? {
        return withContext(Dispatchers.IO) {
            userProfileDao.getUserProfile()
        }
    }

    suspend fun insert(userProfile: UserProfile): Long {
        return withContext(Dispatchers.IO) {
            userProfileDao.insert(userProfile)
        }
    }

    suspend fun update(userProfile: UserProfile) {
        withContext(Dispatchers.IO) {
            userProfileDao.update(userProfile)
        }
    }
}