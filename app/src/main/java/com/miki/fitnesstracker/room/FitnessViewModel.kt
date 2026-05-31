package com.miki.fitnesstracker.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.miki.fitnesstracker.util.TimeUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class FitnessViewModel(application: Application) : AndroidViewModel(application) {



    private val repository = FitnessRepository(application)

    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val allLogs = repository.allLogs

    init {
        viewModelScope.launch {
            ensureToday()
        }
    }

    fun upsertLog(log: FitnessLogEntity) {
        viewModelScope.launch {
            repository.upsertLog(log)
        }
    }

    suspend fun getLogForDay(date: Long): FitnessLogEntity? {
        return repository.getLogByDate(date)
    }

    private suspend fun ensureToday() {
        repository.ensureTodayRow(TimeUtils.getStartOfDay())
    }

    fun resetTodayIfNeeded() {
        viewModelScope.launch {
            repository.resetToday(TimeUtils.getStartOfDay())
        }
    }

    private fun getStartOfDay(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}