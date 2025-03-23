package com.example.atomicleveler.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.atomicleveler.R
import com.example.atomicleveler.data.models.Achievement

class AchievementAdapter(
    private val onItemClick: (Achievement) -> Unit
) : ListAdapter<Achievement, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_achievement, parent, false)
        return AchievementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        val achievement = getItem(position)
        holder.bind(achievement, onItemClick)
    }

    class AchievementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ImageView = itemView.findViewById(R.id.imageView_achievement_icon)
        private val titleTextView: TextView = itemView.findViewById(R.id.textView_achievement_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textView_achievement_description)
        private val xpTextView: TextView = itemView.findViewById(R.id.textView_achievement_xp)

        fun bind(achievement: Achievement, onItemClick: (Achievement) -> Unit) {
            // Set icon
            iconImageView.setImageResource(achievement.iconResId)

            // Set title and description
            titleTextView.text = achievement.title
            descriptionTextView.text = achievement.description

            // Set XP reward
            xpTextView.text = itemView.context.getString(R.string.xp_format, achievement.experiencePoints)

            // Adjust UI based on unlock status
            if (achievement.isUnlocked) {
                // Make the whole item look enabled
                itemView.alpha = 1.0f
            } else {
                // Make it look disabled/locked
                itemView.alpha = 0.6f
            }

            // Set click listener
            itemView.setOnClickListener {
                onItemClick(achievement)
            }
        }
    }

    private class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}