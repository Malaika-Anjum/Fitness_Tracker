package com.miki.fitnesstracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.data.PrefsManager

class GoalsFragment : Fragment(R.layout.goals_fragment) {

    private lateinit var prefs: PrefsManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = PrefsManager(requireContext())

        val stepsInput =
            view.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.input_steps_goal
            )
        val waterInput =
            view.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.input_water_goal
            )
        val currentWeightInput =
            view.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.input_current_weight
            )
        val targetWeightInput =
            view.findViewById<com.google.android.material.textfield.TextInputEditText>(
                R.id.input_target_weight
            )

        view.findViewById<Button>(R.id.btn_update_steps_goal).setOnClickListener {
            val value = stepsInput.text.toString().replace(",", "").toIntOrNull()
            value?.let {
                prefs.saveStepsGoal(it)
                toast("Steps goal updated")
            }
        }

        view.findViewById<Button>(R.id.btn_update_water_goal).setOnClickListener {
            val value = waterInput.text.toString().replace(",", "").toIntOrNull()
            value?.let {
                prefs.saveWaterGoal(it)
                toast("Water goal updated")
            }
        }

        view.findViewById<Button>(R.id.btn_update_weight_goal).setOnClickListener {
            val current = currentWeightInput.text.toString().toFloatOrNull()
            val target = targetWeightInput.text.toString().toFloatOrNull()
            if (current != null && target != null) {
                prefs.saveWeight(current, target)
                toast("Weight goal saved")
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
