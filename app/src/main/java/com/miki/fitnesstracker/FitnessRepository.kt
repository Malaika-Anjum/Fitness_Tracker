package com.fitnesstracker.repository

import androidx.lifecycle.LiveData
import com.fitnesstracker.model.TrackingEntry

/**
 * Interface for the Fitness Data Repository.
 * This contract allows the ViewModel to interact with data without knowing
 * the underlying implementation (e.g., Firebase, local database, mock).
 */
interface FitnessRepository {

    /**
     * Retrieves all tracking entries as LiveData for real-time updates.
     */
    fun getAllEntries(): LiveData<List<TrackingEntry>>

    /**
     * Adds a new tracking entry to the data source.
     */
    suspend fun addEntry(entry: TrackingEntry)

    /**
     * Deletes a specific tracking entry.
     */
    suspend fun deleteEntry(entry: TrackingEntry)
}