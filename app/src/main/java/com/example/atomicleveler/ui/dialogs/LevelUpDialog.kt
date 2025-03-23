package com.example.atomicleveler.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.atomicleveler.R
import com.example.atomicleveler.databinding.DialogLevelUpBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LevelUpDialog : DialogFragment() {

    private var _binding: DialogLevelUpBinding? = null
    private val binding get() = _binding!!

    private var newLevel: Int = 0

    companion object {
        private const val ARG_NEW_LEVEL = "arg_new_level"

        fun newInstance(newLevel: Int): LevelUpDialog {
            val fragment = LevelUpDialog()
            fragment.arguments = Bundle().apply {
                putInt(ARG_NEW_LEVEL, newLevel)
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            newLevel = it.getInt(ARG_NEW_LEVEL)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogLevelUpBinding.inflate(LayoutInflater.from(context))

        // Set new level text
        binding.textViewNewLevel.text = getString(R.string.new_level_format, newLevel)

        // Set button click listener
        binding.buttonContinue.setOnClickListener {
            dismiss()
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