package com.example.atomicleveler.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.atomicleveler.R
import com.example.atomicleveler.databinding.DialogAchievementUnlockedBinding
import com.example.atomicleveler.ui.viewmodels.AchievementViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AchievementUnlockedDialog : DialogFragment() {

    private var _binding: DialogAchievementUnlockedBinding? = null
    private val binding get() = _binding!!

    private lateinit var achievementViewModel: AchievementViewModel
    private var achievementId: Long = 0

    companion object {
        private const val ARG_ACHIEVEMENT_ID = "arg_achievement_id"

        fun newInstance(achievementId: Long): AchievementUnlockedDialog {
            val fragment = AchievementUnlockedDialog()
            fragment.arguments = Bundle().apply {
                putLong(ARG_ACHIEVEMENT_ID, achievementId)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            achievementId = it.getLong(ARG_ACHIEVEMENT_ID)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAchievementUnlockedBinding.inflate(LayoutInflater.from(context))

        // Initialize ViewModel
        achievementViewModel = ViewModelProvider(requireActivity()).get(AchievementViewModel::class.java)

        // Set button click listener
        binding.buttonContinue.setOnClickListener {
            dismiss()
        }

        // Load achievement details
        achievementViewModel.allAchievements.observe(this) { achievements ->
            val achievement = achievements.find { it.id == achievementId }
            achievement?.let {
                binding.imageViewAchievementIcon.setImageResource(it.iconResId)
                binding.textViewAchievementTitle.text = it.title
                binding.textViewAchievementDescription.text = it.description
                binding.textViewXpAwarded.text = getString(R.string.xp_format, it.experiencePoints)
            }
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}