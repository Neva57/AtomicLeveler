package com.example.atomicleveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.atomicleveler.R
import com.example.atomicleveler.data.models.UserProfile
import com.example.atomicleveler.databinding.FragmentProfileBinding
import com.example.atomicleveler.ui.viewmodels.AchievementViewModel
import com.example.atomicleveler.ui.viewmodels.HabitViewModel
import com.example.atomicleveler.ui.viewmodels.UserProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitViewModel: HabitViewModel
    private lateinit var profileViewModel: UserProfileViewModel
    private lateinit var achievementViewModel: AchievementViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModels
        habitViewModel = ViewModelProvider(requireActivity()).get(HabitViewModel::class.java)
        profileViewModel = ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)
        achievementViewModel = ViewModelProvider(requireActivity()).get(AchievementViewModel::class.java)

        // Observe user profile
        profileViewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            updateProfileUI(profile)
        }

        // Observe habits for stats
        habitViewModel.allHabits.observe(viewLifecycleOwner) { habits ->
            updateStats(habits.size)
        }

        // Observe achievements
        achievementViewModel.allAchievements.observe(viewLifecycleOwner) { achievements ->
            val unlockedCount = achievements.count { it.isUnlocked }
            val totalCount = achievements.size
            binding.textViewAchievementsUnlocked.text =
                getString(R.string.achievements_count_format, unlockedCount, totalCount)
        }

        // Set up button click listeners
        binding.buttonEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun updateProfileUI(profile: UserProfile?) {
        if (profile == null) return

        // Update UI with profile data
        binding.textViewUserName.text = profile.name
        binding.textViewLevel.text = getString(R.string.level_format, profile.level)

        // Calculate XP progress
        val xpToNextLevel = calculateXpToNextLevel(profile.level)
        val progress = (profile.experience * 100) / xpToNextLevel

        binding.progressBarExperience.progress = progress
        binding.textViewExperience.text = getString(
            R.string.xp_to_next_level,
            profile.experience,
            xpToNextLevel
        )

        // Update stats
        binding.textViewTotalCompletions.text = profile.totalHabitsCompleted.toString()
    }

    private fun updateStats(totalHabits: Int) {
        binding.textViewTotalHabits.text = totalHabits.toString()

        // Set best streak from habits
        var bestStreak = 0
        habitViewModel.allHabits.value?.forEach { habit ->
            if (habit.bestStreak > bestStreak) {
                bestStreak = habit.bestStreak
            }
        }

        binding.textViewBestStreak.text = getString(R.string.days_format, bestStreak)
    }

    private fun calculateXpToNextLevel(level: Int): Int {
        // Simple formula: 100 * level for next level
        return 100 * level
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_edit_profile, null)

        val nameInput = dialogView.findViewById<TextInputEditText>(R.id.editText_user_name)

        // Pre-fill with current name
        profileViewModel.userProfile.value?.let {
            nameInput.setText(it.name)
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_profile)
            .setView(dialogView)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.save) { _, _ ->
                val newName = nameInput.text.toString().trim()
                if (newName.isNotEmpty()) {
                    profileViewModel.updateUserName(newName)
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}