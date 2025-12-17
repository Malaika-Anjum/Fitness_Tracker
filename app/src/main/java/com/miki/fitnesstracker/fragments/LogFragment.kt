package com.miki.fitnesstracker.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.room.FitnessLogEntity
import com.miki.fitnesstracker.room.FitnessViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class LogFragment : Fragment() {

    private val viewModel: FitnessViewModel by viewModels()

    private lateinit var selectedDateText: TextView
    private lateinit var waterInput: TextInputEditText
    private lateinit var caloriesInput: TextInputEditText
    private lateinit var stepsInput: TextInputEditText
    private lateinit var saveWaterBtn: Button
    private lateinit var saveCaloriesBtn: Button
    private lateinit var saveStepsBtn: Button

    private var selectedDate: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.log_fragment, container, false)

        selectedDateText = view.findViewById(R.id.text_selected_date)
        waterInput = view.findViewById(R.id.input_water)
        caloriesInput = view.findViewById(R.id.input_calories)
        stepsInput = view.findViewById(R.id.input_steps)
        saveWaterBtn = view.findViewById(R.id.btn_save_water)
        saveCaloriesBtn = view.findViewById(R.id.btn_save_calories)
        saveStepsBtn = view.findViewById(R.id.btn_save_steps)

        // Default date is today
        selectedDate = System.currentTimeMillis()
        val sdf = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        selectedDateText.text = "Logging for: ${sdf.format(Date(selectedDate))}"

        saveWaterBtn.setOnClickListener { saveData("water") }
        saveCaloriesBtn.setOnClickListener { saveData("calories") }
        saveStepsBtn.setOnClickListener { saveData("steps") }

        return view
    }

    private fun saveData(type: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingLog = viewModel.getAllLogsSync()
                .find { isSameDay(it.date, selectedDate) }

            val updatedLog = if (existingLog != null) {
                when (type) {
                    "water" -> existingLog.copy(
                        water = waterInput.text.toString().toIntOrNull() ?: existingLog.water
                    )
                    "calories" -> existingLog.copy(
                        calories = caloriesInput.text.toString().toIntOrNull() ?: existingLog.calories
                    )
                    "steps" -> existingLog.copy(
                        steps = stepsInput.text.toString().toIntOrNull() ?: existingLog.steps
                    )
                    else -> existingLog
                }
            } else {
                FitnessLogEntity(
                    date = selectedDate,
                    water = if (type == "water") waterInput.text.toString().toIntOrNull() ?: 0 else 0,
                    calories = if (type == "calories") caloriesInput.text.toString().toIntOrNull() ?: 0 else 0,
                    steps = if (type == "steps") stepsInput.text.toString().toIntOrNull() ?: 0 else 0
                )
            }

            viewModel.upsertLog(updatedLog)

            withContext(Dispatchers.Main) {
                when (type) {
                    "water" -> waterInput.text?.clear()
                    "calories" -> caloriesInput.text?.clear()
                    "steps" -> stepsInput.text?.clear()
                }
            }
        }
    }

    private fun isSameDay(time1: Long, time2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = time1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = time2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}
