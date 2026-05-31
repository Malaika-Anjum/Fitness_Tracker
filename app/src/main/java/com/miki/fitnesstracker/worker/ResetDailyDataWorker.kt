package com.miki.fitnesstracker.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.auth.FirebaseAuth
import com.miki.fitnesstracker.room.FitnessDatabase
import java.util.Calendar

class ResetDailyDataWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val dao = FitnessDatabase.getDatabase(applicationContext).fitnessDao()

        val userId = inputData.getString("userId") ?: return Result.failure()

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val todayStart = calendar.timeInMillis

        dao.resetToday(userId, todayStart)

        return Result.success()

        val work = OneTimeWorkRequestBuilder<ResetDailyDataWorker>()
            .setInputData(workDataOf("userId" to FirebaseAuth.getInstance().currentUser?.uid))
            .build()
    }
}
