package com.example.atomicleveler.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.atomicleveler.data.database.AppDatabase
import com.example.atomicleveler.data.models.UserProfile
import com.example.atomicleveler.data.repository.UserProfileRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserProfileRepository
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    init {
        val userProfileDao = AppDatabase.getDatabase(application).userProfileDao()
        repository = UserProfileRepository(userProfileDao)

        // Load user profile
        viewModelScope.launch {
            val profile = repository.getUserProfile()

            if (profile == null) {
                // Create default profile if doesn't exist
                val defaultProfile = UserProfile(name = "User")
                val id = repository.insert(defaultProfile)
                _userProfile.value = defaultProfile.copy(id = id)
            } else {
                _userProfile.value = profile
            }
        }
    }

    fun updateUserName(newName: String) {
        userProfile.value?.let { currentProfile ->
            val updatedProfile = currentProfile.copy(name = newName)

            viewModelScope.launch {
                repository.update(updatedProfile)
                _userProfile.value = updatedProfile
            }
        }
    }

    fun addExperience(xpAmount: Int) {
        userProfile.value?.let { currentProfile ->
            var xpToAdd = xpAmount
            var newLevel = currentProfile.level
            var newXp = currentProfile.experience + xpAmount

            // Check if user has leveled up
            val xpForNextLevel = calculateXpForNextLevel(currentProfile.level)

            if (newXp >= xpForNextLevel) {
                // Level up
                newXp -= xpForNextLevel
                newLevel++

                // Show level up dialog
                showLevelUpDialog(newLevel)
            }

            val updatedProfile = currentProfile.copy(
                level = newLevel,
                experience = newXp
            )

            viewModelScope.launch {
                repository.update(updatedProfile)
                _userProfile.value = updatedProfile
            }
        }
    }

    fun incrementCompletedHabits() {
        userProfile.value?.let { currentProfile ->
            val updatedProfile = currentProfile.copy(
                totalHabitsCompleted = currentProfile.totalHabitsCompleted + 1
            )

            viewModelScope.launch {
                repository.update(updatedProfile)
                _userProfile.value = updatedProfile
            }
        }
    }

    private fun calculateXpForNextLevel(currentLevel: Int): Int {
        // Simple formula: 100 * level
        return 100 * currentLevel
    }

    private fun showLevelUpDialog(newLevel: Int) {
        // This would be implemented to show a level up dialog
        // using a Material Dialog, but can't be done directly in ViewModel
        // Would need to expose an event that the UI can observe
    }
}