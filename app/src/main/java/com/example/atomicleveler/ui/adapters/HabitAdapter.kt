package com.example.atomicleveler.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.atomicleveler.R
import com.example.atomicleveler.data.models.Habit
import com.example.atomicleveler.data.models.HabitFrequency
import java.text.SimpleDateFormat
import java.util.*

class HabitAdapter(
    private val onItemClick: (Habit) -> Unit,
    private val onCompleteClick: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = getItem(position)
        holder.bind(habit, onItemClick, onCompleteClick)
    }

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textView_habit_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textView_habit_description)
        private val streakTextView: TextView = itemView.findViewById(R.id.textView_streak)
        private val completeButton: Button = itemView.findViewById(R.id.button_complete_habit)

        fun bind(
            habit: Habit,
            onItemClick: (Habit) -> Unit,
            onCompleteClick: (Habit) -> Unit
        ) {
            // Set basic info
            titleTextView.text = habit.title
            descriptionTextView.text = habit.description

            // Set streak text
            val streakText = when (habit.frequency) {
                HabitFrequency.DAILY -> itemView.context.getString(R.string.streak_days, habit.currentStreak)
                HabitFrequency.WEEKLY -> itemView.context.getString(R.string.streak_weeks, habit.currentStreak)
                else -> itemView.context.getString(R.string.streak_days, habit.currentStreak)
            }
            streakTextView.text = streakText

            // Set up complete button
            setupCompleteButton(habit, onCompleteClick)

            // Set item click listener
            itemView.setOnClickListener {
                onItemClick(habit)
            }
        }

        private fun setupCompleteButton(habit: Habit, onCompleteClick: (Habit) -> Unit) {
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Check if habit is already completed today
            val isCompletedToday = habit.completionDates.contains(today)

            if (isCompletedToday) {
                // Habit already completed today
                completeButton.isEnabled = false
                completeButton.text = itemView.context.getString(R.string.completed)
            } else {
                // Habit not completed today
                completeButton.isEnabled = true
                completeButton.text = itemView.context.getString(R.string.complete)

                // Set click listener for completion
                completeButton.setOnClickListener {
                    onCompleteClick(habit)
                    // UI will update when adapter receives new data
                }
            }
        }
    }

    private class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}