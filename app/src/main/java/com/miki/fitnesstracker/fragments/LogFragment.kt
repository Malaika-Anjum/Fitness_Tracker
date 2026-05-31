package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.miki.fitnesstracker.util.TimeUtils
import com.google.android.material.textfield.TextInputEditText
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.room.FitnessLogEntity
import com.miki.fitnesstracker.room.FitnessViewModel
import kotlinx.coroutines.launch

class LogFragment : Fragment(R.layout.log_fragment) {

    private val viewModel: FitnessViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val water = view.findViewById<TextInputEditText>(R.id.input_water)
        val calories = view.findViewById<TextInputEditText>(R.id.input_calories)
        val steps = view.findViewById<TextInputEditText>(R.id.input_steps)

        val today = TimeUtils.getStartOfDay()

        fun save(update: (FitnessLogEntity) -> FitnessLogEntity) {
            lifecycleScope.launch {
                val existing = viewModel.getLogForDay(today)

                val base = existing ?: FitnessLogEntity(today, toString(), 0, 0, 0)
                val updated = update(base)

                viewModel.upsertLog(updated)
            }
        }

        view.findViewById<Button>(R.id.btn_save_water).setOnClickListener {
            save { it.copy(water = water.text.toString().toIntOrNull() ?: it.water) }
        }

        view.findViewById<Button>(R.id.btn_save_calories).setOnClickListener {
            save { it.copy(calories = calories.text.toString().toIntOrNull() ?: it.calories) }
        }

        view.findViewById<Button>(R.id.btn_save_steps).setOnClickListener {
            save { it.copy(steps = steps.text.toString().toIntOrNull() ?: it.steps) }
        }
    }
}