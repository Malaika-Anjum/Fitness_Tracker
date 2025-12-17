package com.miki.fitnesstracker.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FitnessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FitnessRepository(application)

    val allLogs: LiveData<List<FitnessLogEntity>> = repository.allLogs

    // Use this for LogFragment to upsert
    fun upsertLog(log: FitnessLogEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.upsertLog(log)
        }
    }

    suspend fun getAllLogsSync(): List<FitnessLogEntity> {
        return repository.getAllLogsSync()
    }
}
