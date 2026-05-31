package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.data.PrefsManager
import com.miki.fitnesstracker.room.FitnessViewModel
import com.miki.fitnesstracker.util.TimeUtils

class HomeFragment : Fragment(R.layout.home_fragment) {

    private val viewModel: FitnessViewModel by viewModels()
    private lateinit var prefs: PrefsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        prefs = PrefsManager(requireContext())

        val goalText = view.findViewById<TextView>(R.id.goal_steps)
        val stepsText = view.findViewById<TextView>(R.id.text_steps_count)
        val waterText = view.findViewById<TextView>(R.id.text_water_count)
        val caloriesText = view.findViewById<TextView>(R.id.text_calories_burned)
        val waterProgress = view.findViewById<ProgressBar>(R.id.progress_water)
        val logBtn = view.findViewById<Button>(R.id.button_log_meal)

        goalText.text = "Goal: ${prefs.getStepsGoal()}"
        waterProgress.max = prefs.getWaterGoal()

        logBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_logFragment)
        }

        viewModel.allLogs.observe(viewLifecycleOwner) { logs ->


            val todayStart = TimeUtils.getStartOfDay()

            val today = logs.find { it.date == todayStart }

            val steps = today?.steps ?: 0
            val water = today?.water ?: 0
            val calories = (steps * 0.04).toInt()

            stepsText.text = steps.toString()
            waterText.text = "$water ml"
            caloriesText.text = "$calories kcal"
            waterProgress.progress = water
        }
    }
}