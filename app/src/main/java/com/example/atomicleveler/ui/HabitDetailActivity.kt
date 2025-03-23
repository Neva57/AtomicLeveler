package com.example.atomicleveler.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.atomicleveler.R
import com.example.atomicleveler.data.models.Habit
import com.example.atomicleveler.data.models.HabitFrequency
import com.example.atomicleveler.databinding.ActivityHabitDetailBinding
import com.example.atomicleveler.ui.viewmodels.HabitViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHabitDetailBinding
    private lateinit var habitViewModel: HabitViewModel
    private var habitId: Long = 0
    private var isEditMode: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize ViewModel
        habitViewModel = ViewModelProvider(this).get(HabitViewModel::class.java)

        // Check if we're editing an existing habit
        if (intent.hasExtra("HABIT_ID")) {
            habitId = intent.getLongExtra("HABIT_ID", 0)
            isEditMode = true

            // Change title to Edit Habit
            supportActionBar?.title = getString(R.string.edit_habit)

            // Show delete button
            binding.buttonDeleteHabit.visibility = View.VISIBLE

            // Load habit details
            loadHabitDetails()
        } else {
            // We're creating a new habit
            supportActionBar?.title = getString(R.string.new_habit)
            binding.buttonDeleteHabit.visibility = View.GONE
        }

        // Set up button click listeners
        setupClickListeners()
    }

    private fun loadHabitDetails() {
        habitViewModel.allHabits.observe(this) { habits ->
            val habit = habits.find { it.id == habitId }
            habit?.let {
                // Populate form fields
                binding.editTextHabitName.setText(it.title)
                binding.editTextHabitDescription.setText(it.description)

                // Set frequency radio button
                when (it.frequency) {
                    "DAILY" -> binding.radioButtonDaily.isChecked = true
                    "WEEKLY" -> binding.radioButtonWeekly.isChecked = true
                    else -> binding.radioButtonDaily.isChecked = true
                }
            }
        }
    }

    private fun setupClickListeners() {
        // Save button
        binding.buttonSaveHabit.setOnClickListener {
            saveHabit()
        }

        // Delete button
        binding.buttonDeleteHabit.setOnClickListener {
            confirmDeleteHabit()
        }
    }

    private fun saveHabit() {
        // Validate input
        val title = binding.editTextHabitName.text.toString().trim()
        val description = binding.editTextHabitDescription.text.toString().trim()

        if (title.isEmpty()) {
            binding.textInputLayoutHabitName.error = getString(R.string.error_empty_title)
            return
        } else {
            binding.textInputLayoutHabitName.error = null
        }

        // Get selected frequency
        val frequency = when {
            binding.radioButtonDaily.isChecked -> "DAILY"
            binding.radioButtonWeekly.isChecked -> "WEEKLY"
            else -> "DAILY"
        }

        if (isEditMode) {
            // Get the current habit and update its properties
            habitViewModel.allHabits.value?.find { it.id == habitId }?.let { currentHabit ->
                val updatedHabit = currentHabit.copy(
                    title = title,
                    description = description,
                    frequency = frequency.toString()
                )
                habitViewModel.update(updatedHabit)
            }
        } else {
            // Create a new habit
            val newHabit = Habit(
                title = title,
                description = description,
                frequency = frequency,
                createdDate = System.currentTimeMillis(),
                completionDatesStr = "", // Changed from completionDates to completionDatesStr
                currentStreak = 0,
                bestStreak = 0
            )
            habitViewModel.insert(newHabit)
        }

        // Close activity
        finish()
    }

    private fun confirmDeleteHabit() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage(R.string.confirm_delete_message)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteHabit()
            }
            .show()
    }

    private fun deleteHabit() {
        habitViewModel.allHabits.value?.find { it.id == habitId }?.let {
            habitViewModel.delete(it)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}