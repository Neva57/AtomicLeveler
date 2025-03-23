package com.example.atomicleveler.data.repository

import com.example.atomicleveler.data.dao.UserProfileDao
import com.example.atomicleveler.data.models.UserProfile

class UserProfileRepository(private val userProfileDao: UserProfileDao) {

    suspend fun getUserProfile(): UserProfile? {
        return userProfileDao.getUserProfile()
    }

    suspend fun insert(userProfile: UserProfile): Long {
        return userProfileDao.insert(userProfile)
    }

    suspend fun update(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }
}