package com.example.atomicleveler.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atomicleveler.R
import com.example.atomicleveler.data.models.Achievement
import com.example.atomicleveler.data.models.Habit
import com.example.atomicleveler.databinding.FragmentHabitListBinding
import com.example.atomicleveler.ui.HabitDetailActivity
import com.example.atomicleveler.ui.adapters.HabitAdapter
import com.example.atomicleveler.ui.dialogs.AchievementUnlockedDialog
import com.example.atomicleveler.ui.dialogs.LevelUpDialog
import com.example.atomicleveler.ui.viewmodels.AchievementViewModel
import com.example.atomicleveler.ui.viewmodels.HabitViewModel
import com.example.atomicleveler.ui.viewmodels.UserProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HabitListFragment : Fragment() {
    private var _binding: FragmentHabitListBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitViewModel: HabitViewModel
    private lateinit var achievementViewModel: AchievementViewModel
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var adapter: HabitAdapter

    private var lastCheckTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModels
        habitViewModel = ViewModelProvider(requireActivity()).get(HabitViewModel::class.java)
        achievementViewModel = ViewModelProvider(requireActivity()).get(AchievementViewModel::class.java)
        userProfileViewModel = ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)

        // Display a random quote
        displayRandomQuote()

        // Set up RecyclerView
        adapter = HabitAdapter(
            onItemClick = { habit ->
                val intent = Intent(requireContext(), HabitDetailActivity::class.java)
                intent.putExtra("HABIT_ID", habit.id)
                startActivity(intent)
            },
            onCompleteClick = { habit ->
                completeHabit(habit)
            }
        )

        binding.recyclerViewHabits.adapter = adapter
        binding.recyclerViewHabits.layoutManager = LinearLayoutManager(requireContext())

        // Observe habits
        habitViewModel.allHabits.observe(viewLifecycleOwner) { habits ->
            adapter.submitList(habits)
            binding.emptyView.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE

            // Check for achievements
            userProfileViewModel.userProfile.value?.let { profile ->
                habitViewModel.checkAchievements(habits, profile)
            }
        }

        // Observe achievement unlocks
        achievementViewModel.newlyUnlockedAchievements.observe(viewLifecycleOwner) { achievements ->
            if (achievements.isNotEmpty()) {
                showAchievementUnlockedDialog(achievements.first())
            }
        }

        // Observe level up events
        userProfileViewModel.levelUpEvent.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { newLevel ->
                showLevelUpDialog(newLevel)
            }
        }

        // Set last check time for new achievements
        lastCheckTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the quote each time the fragment becomes visible
        displayRandomQuote()
    }

    private fun displayRandomQuote() {
        val quotes = resources.getStringArray(R.array.atomic_quotes)
        val randomQuote = quotes.random()
        binding.textViewQuote.text = "\"${randomQuote}\""
    }

    private fun completeHabit(habit: Habit) {
        habitViewModel.completeHabitToday(habit)

        // Increment total completions in user profile
        userProfileViewModel.incrementCompletedHabits()

        // Award XP for completing a habit
        userProfileViewModel.addExperience(5) // 5 XP per completion

        showCompletionDialog(habit)

        // Check for new achievements since last check
        achievementViewModel.checkForNewAchievements(lastCheckTime)
        lastCheckTime = System.currentTimeMillis()
    }

    private fun showCompletionDialog(habit: Habit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.habit_completed)
            .setMessage(getString(R.string.habit_completion_message, habit.title))
            .setPositiveButton(R.string.continue_growing) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showAchievementUnlockedDialog(achievement: Achievement) {
        AchievementUnlockedDialog.newInstance(achievement.id)
            .show(parentFragmentManager, "achievement_unlocked")
    }

    private fun showLevelUpDialog(newLevel: Int) {
        LevelUpDialog.newInstance(newLevel)
            .show(parentFragmentManager, "level_up")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}