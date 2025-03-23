package com.example.atomicleveler.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.atomicleveler.data.achievements.AchievementManager
import com.example.atomicleveler.data.database.AppDatabase
import com.example.atomicleveler.data.models.Achievement
import com.example.atomicleveler.data.repository.AchievementRepository
import com.example.atomicleveler.data.repository.UserProfileRepository
import kotlinx.coroutines.launch

class AchievementViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AchievementRepository
    private val userProfileRepository: UserProfileRepository
    private lateinit var achievementManager: AchievementManager

    private val _allAchievements = MutableLiveData<List<Achievement>>()
    val allAchievements: LiveData<List<Achievement>> = _allAchievements

    private val _newlyUnlockedAchievements = MutableLiveData<List<Achievement>>()
    val newlyUnlockedAchievements: LiveData<List<Achievement>> = _newlyUnlockedAchievements

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AchievementRepository(database.achievementDao())
        userProfileRepository = UserProfileRepository(database.userProfileDao())

        achievementManager = AchievementManager(
            repository,
            userProfileRepository,
            viewModelScope
        )

        // Initialize default achievements
        viewModelScope.launch {
            achievementManager.initializeAchievements()
            loadAchievements()
        }
    }

    private fun loadAchievements() {
        viewModelScope.launch {
            _allAchievements.value = repository.getAllAchievements()
        }
    }

    fun checkForNewAchievements(lastCheckTime: Long) {
        viewModelScope.launch {
            val newAchievements = achievementManager.getNewlyUnlockedAchievements(lastCheckTime)
            if (newAchievements.isNotEmpty()) {
                _newlyUnlockedAchievements.value = newAchievements
            }
        }
    }

    fun getUnlockedAchievementsCount(): Int {
        return _allAchievements.value?.count { it.isUnlocked } ?: 0
    }

    fun getTotalAchievementsCount(): Int {
        return _allAchievements.value?.size ?: 0
    }
}