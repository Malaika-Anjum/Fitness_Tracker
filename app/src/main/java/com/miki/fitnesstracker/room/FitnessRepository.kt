package com.miki.fitnesstracker.room

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import java.util.Calendar

class FitnessRepository(application: Application) {

    private val dao: FitnessDao = FitnessDatabase.getDatabase(application).fitnessDao()

    val allLogs: LiveData<List<FitnessLogEntity>> = liveData {
        emit(dao.getAllLogs())
    }

    suspend fun getAllLogsSync(): List<FitnessLogEntity> {
        return dao.getAllLogs()
    }

    // Upsert: if a log for the same day exists, update it; else insert new
    suspend fun upsertLog(log: FitnessLogEntity) {
        val startOfDay = Calendar.getInstance().apply {
            timeInMillis = log.date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val existingLog = dao.getAllLogs().find {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            val logCal = Calendar.getInstance().apply { timeInMillis = startOfDay }
            cal.get(Calendar.YEAR) == logCal.get(Calendar.YEAR) &&
                    cal.get(Calendar.DAY_OF_YEAR) == logCal.get(Calendar.DAY_OF_YEAR)
        }

        if (existingLog != null) {
            val updatedLog = existingLog.copy(
                water = if (log.water > 0) log.water else existingLog.water,
                calories = if (log.calories > 0) log.calories else existingLog.calories,
                steps = if (log.steps > 0) log.steps else existingLog.steps
            )
            dao.addLog(updatedLog) // replace old log
        } else {
            dao.addLog(log)
        }
    }
}