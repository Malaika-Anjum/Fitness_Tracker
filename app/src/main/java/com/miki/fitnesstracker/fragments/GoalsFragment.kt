package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.data.PrefsManager

class GoalsFragment : Fragment(R.layout.goals_fragment) {

    private lateinit var prefs: PrefsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        prefs = PrefsManager(requireContext())

        val steps = view.findViewById<TextInputEditText>(R.id.input_steps_goal)
        val water = view.findViewById<TextInputEditText>(R.id.input_water_goal)

        val currentSteps = prefs.getStepsGoal()
        val currentWater = prefs.getWaterGoal()

        steps.setText(currentSteps.toString())
        water.setText(currentWater.toString())

        view.findViewById<Button>(R.id.btn_update_steps_goal).setOnClickListener {
            prefs.saveStepsGoal(steps.text.toString().toIntOrNull() ?: currentSteps)
            Toast.makeText(requireContext(), "Steps goal updated", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<Button>(R.id.btn_update_water_goal).setOnClickListener {
            prefs.saveWaterGoal(water.text.toString().toIntOrNull() ?: currentWater)
            Toast.makeText(requireContext(), "Water goal updated", Toast.LENGTH_SHORT).show()
        }
    }
}