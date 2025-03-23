package com.example.atomicleveler.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.atomicleveler.data.database.AppDatabase
import com.example.atomicleveler.data.models.UserProfile
import com.example.atomicleveler.data.repository.UserProfileRepository
import com.example.atomicleveler.utils.Event
import kotlinx.coroutines.launch

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserProfileRepository
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    // Event for level up
    private val _levelUpEvent = MutableLiveData<Event<Int>>()
    val levelUpEvent: LiveData<Event<Int>> = _levelUpEvent

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
                _userProfile.value = profile!!
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
            val newXp = currentProfile.experience + xpAmount
            val xpForNextLevel = calculateXpForNextLevel(currentProfile.level)

            if (newXp >= xpForNextLevel) {
                // Level up
                val newLevel = currentProfile.level + 1
                val remainingXp = newXp - xpForNextLevel

                val updatedProfile = currentProfile.copy(
                    level = newLevel,
                    experience = remainingXp
                )

                viewModelScope.launch {
                    repository.update(updatedProfile)
                    _userProfile.value = updatedProfile

                    // Trigger level up event
                    _levelUpEvent.value = Event(newLevel)
                }
            } else {
                // Just add XP, no level up
                val updatedProfile = currentProfile.copy(
                    experience = newXp
                )

                viewModelScope.launch {
                    repository.update(updatedProfile)
                    _userProfile.value = updatedProfile
                }
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
}