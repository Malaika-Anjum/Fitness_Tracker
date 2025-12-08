package com.miki.fitnessapp.ui

import androidx.lifecycle.ViewModel
import com.miki.fitnessapp.data.FitnessDao // Needs the DAO to access database
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Mocking the Repository and DAO for standalone code demonstration
// In a real project, these dependencies would be injected (e.g., using Hilt or Koin)
interface FitnessDao {
    fun getTodaySummary(todayDate: String): kotlinx.coroutines.flow.Flow<DailySummary?>
    suspend fun update(summary: DailySummary)
}

class Repository(private val dao: FitnessDao) {
    fun getTodaySummaryFlow(date: String) = dao.getTodaySummary(date)
    suspend fun updateSummary(summary: DailySummary) { dao.update(summary) }
}
// End Mock

class HomeViewModel(
    private val repository: Repository
) : ViewModel() {

    // --- Goal Constants ---
    val STEP_GOAL = 10000
    val WATER_GOAL_ML = 2500

    private val todayDateKey: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    /**
     * StateFlow exposed to the UI. It maps the raw database Flow into a cleaned-up DailySummary
     * and guarantees a non-null initial state (zeroed metrics).
     */
    val dailySummary: StateFlow<DailySummary> = repository
        .getTodaySummaryFlow(todayDateKey)
        .map { summary ->
            // If the database returns null (no entry for today yet), return a zeroed DailySummary
            summary ?: DailySummary(
                dateKey = todayDateKey,
                steps = 0,
                waterMl = 0,
                caloriesBurned = 0,
                timestamp = System.currentTimeMillis()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Keep active for 5 seconds after last collector
            initialValue = DailySummary(todayDateKey, 0, 0, 0, System.currentTimeMillis())
        )

    /**
     * Function to update water intake via user action.
     * This triggers an update in the database, which in turn causes 'dailySummary' to emit a new value,
     * finally updating the Activity automatically.
     */
    fun logWaterIntake(amountMl: Int) {
        val current = dailySummary.value
        val updatedSummary = current.copy(
            waterMl = current.waterMl + amountMl,
            timestamp = System.currentTimeMillis()
        )
        // Launch a coroutine to update the database
        viewModelScope.launch {
            repository.updateSummary(updatedSummary)
        }
    }
}