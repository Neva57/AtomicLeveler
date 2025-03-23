package com.example.atomicleveler.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atomicleveler.data.models.Achievement
import com.example.atomicleveler.databinding.FragmentAchievementsBinding
import com.example.atomicleveler.ui.adapters.AchievementAdapter
import com.example.atomicleveler.ui.viewmodels.AchievementViewModel

class AchievementsFragment : Fragment() {

    private var _binding: FragmentAchievementsBinding? = null
    private val binding get() = _binding!!

    private lateinit var achievementViewModel: AchievementViewModel
    private lateinit var adapter: AchievementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievementsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        achievementViewModel = ViewModelProvider(requireActivity()).get(AchievementViewModel::class.java)

        // Set up RecyclerView
        setupRecyclerView()

        // Observe achievements
        achievementViewModel.allAchievements.observe(viewLifecycleOwner) { achievements ->
            adapter.submitList(achievements)
        }
    }

    private fun setupRecyclerView() {
        adapter = AchievementAdapter { achievement ->
            // Show achievement details dialog if needed
            if (!achievement.isUnlocked) {
                showAchievementRequirementsDialog(achievement)
            }
        }

        binding.recyclerViewAchievements.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AchievementsFragment.adapter
        }
    }

    private fun showAchievementRequirementsDialog(achievement: Achievement) {
        // Show a dialog with details on how to unlock this achievement
        // This would be implemented with a MaterialAlertDialogBuilder
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}