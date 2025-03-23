package com.example.atomicleveler.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.atomicleveler.R
import com.example.atomicleveler.databinding.ActivityMainBinding
import com.example.atomicleveler.ui.fragments.AchievementsFragment
import com.example.atomicleveler.ui.fragments.HabitListFragment
import com.example.atomicleveler.ui.fragments.ProfileFragment
import com.example.atomicleveler.ui.viewmodels.HabitViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var habitViewModel: HabitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        habitViewModel = ViewModelProvider(this).get(HabitViewModel::class.java)

        // Set up bottom navigation
        setupBottomNavigation()

        // Set default fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HabitListFragment())
                .commit()
        }

        // FAB for adding new habits
        binding.fabAddHabit.setOnClickListener {
            val intent = Intent(this, HabitDetailActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HabitListFragment())
                        .commit()
                    true
                }
                R.id.nav_achievements -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, AchievementsFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}