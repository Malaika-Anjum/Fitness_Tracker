package com.fitnesstracker.viewmodel

import com.fitnesstracker.model.TrackingEntry
import com.fitnesstracker.repository.FitnessRepository
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for preparing and managing data for the UI.
 * It uses the FitnessRepository to handle data persistence.
 */
class TrackingViewModel (private val repository: FitnessRepository) : ViewModel() {

    // Expose the list of all entries as LiveData (fetched from the repository)
    val allEntries: LiveData<List<TrackingEntry>> = repository.getAllEntries()

    // Expose summary statistics as LiveData
    val totalCaloriesBurned: LiveData<Int> = allEntries.map { entries ->
        entries.sumOf { it.caloriesBurned }
    }

    // Function to add a new tracking entry
    fun addEntry(activity: String, duration: Int, calories: Int) {
        val newEntry = TrackingEntry(
            activityType = activity,
            durationMinutes = duration,
            caloriesBurned = calories
        )
        // Launch a coroutine to execute the suspend function on the repository
        viewModelScope.launch {
            repository.addEntry(newEntry)
        }
    }

    // Function to delete an existing tracking entry
    fun deleteEntry(entry: TrackingEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}

/**
 * Factory for creating an instance of TrackingViewModel with the required repository dependency.
 * This is crucial for proper MVVM and dependency injection setup without Hilt/Koin.
 */
class TrackingViewModelFactory(private val repository: FitnessRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrackingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}