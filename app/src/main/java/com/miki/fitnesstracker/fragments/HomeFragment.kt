package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.data.PrefsManager
import com.miki.fitnesstracker.room.FitnessViewModel
import java.util.*

class HomeFragment : Fragment(R.layout.home_fragment) {

    private lateinit var prefs: PrefsManager
    private val viewModel: FitnessViewModel by viewModels()

    private lateinit var goalText: TextView
    private lateinit var stepsCountText: TextView
    private lateinit var waterCountText: TextView
    private lateinit var caloriesText: TextView
    private lateinit var waterProgress: ProgressBar
    private lateinit var logMealBtn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())

        goalText = view.findViewById(R.id.goal_steps)
        stepsCountText = view.findViewById(R.id.text_steps_count)
        waterCountText = view.findViewById(R.id.text_water_count)
        caloriesText = view.findViewById(R.id.text_calories_burned)
        waterProgress = view.findViewById(R.id.progress_water)
        logMealBtn = view.findViewById(R.id.button_log_meal)

        goalText.text = "Goal: ${prefs.getStepsGoal()}"
        waterProgress.max = prefs.getWaterGoal()

        logMealBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_logFragment)
        }

        observeTodayTotals()
    }

    private fun observeTodayTotals() {
        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->
            val today = Calendar.getInstance()

            val todayLog = logs.find { log ->
                val logCal = Calendar.getInstance().apply { timeInMillis = log.date }
                logCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                        logCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            }

            val steps = todayLog?.steps ?: 0
            val water = todayLog?.water ?: 0

            // Calculate calories burned from steps
            val caloriesPerStep = 0.04  // Adjust if you want
            val calories = (steps * caloriesPerStep).toInt()

            stepsCountText.text = steps.toString()
            waterCountText.text = "$water ml"
            caloriesText.text = "$calories kcal"
            waterProgress.progress = water
        }
    }

}
