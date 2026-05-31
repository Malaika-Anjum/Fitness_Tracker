package com.miki.fitnesstracker.room

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth

class FitnessRepository(application: Application) {

    private val dao = FitnessDatabase.getDatabase(application).fitnessDao()

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // ✅ REAL-TIME DATA
    val allLogs: LiveData<List<FitnessLogEntity>>
        get() = dao.getAllLogs(userId)

    suspend fun upsertLog(log: FitnessLogEntity) {
        dao.addLog(log)
    }

    suspend fun getLogByDate(date: Long): FitnessLogEntity? {
        return dao.getLogByDate(userId, date)
    }

    suspend fun getAllLogsSync(): List<FitnessLogEntity> {
        return dao.getAllLogsSync(userId)
    }

    suspend fun ensureTodayRow(today: Long) {
        if (dao.exists(userId, today) == 0) {
            dao.addLog(
                FitnessLogEntity(
                    date = today,
                    userId = userId,
                    steps = 0,
                    water = 0,
                    calories = 0
                )
            )
        }
    }

    suspend fun resetToday(today: Long) {
        dao.resetToday(userId, today)
    }
}