package com.miki.fitnesstracker.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.miki.fitnesstracker.R
import com.miki.fitnesstracker.data.PrefsManager
import com.miki.fitnesstracker.room.FitnessLogEntity
import com.miki.fitnesstracker.room.FitnessRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class StepCounterService : Service(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null
    private lateinit var prefs: PrefsManager
    private var userId: String? = null

    private var baseSteps = -1

    override fun onCreate() {
        super.onCreate()

        prefs = PrefsManager(this)

        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            stopSelf()
            return
        }

        loadBaseSteps()

        startForegroundService()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        stepSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    // ---------------- FIX 1: persist step baseline ----------------
    private fun loadBaseSteps() {
        val sp = getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
        baseSteps = sp.getInt("base_steps", -1)
    }

    private fun saveBaseSteps(value: Int) {
        getSharedPreferences("step_prefs", Context.MODE_PRIVATE)
            .edit()
            .putInt("base_steps", value)
            .apply()
    }

    // ---------------- FOREGROUND SERVICE ----------------
    private fun startForegroundService() {
        val channelId = "step_counter_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Step Counter",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Fitness Tracker")
            .setContentText("Tracking your steps in real time")
            .setSmallIcon(R.drawable.ic_steps)
            .setOngoing(true)
            .build()

        startForeground(1, notification)
    }

    // ---------------- SENSOR ----------------
    override fun onSensorChanged(event: SensorEvent) {

        val totalSteps = event.values[0].toInt()

        // FIRST RUN → set baseline
        if (baseSteps == -1) {
            baseSteps = totalSteps
            saveBaseSteps(baseSteps)
        }

        val todaySteps = totalSteps - baseSteps
        if (todaySteps < 0) return

        saveSteps(todaySteps)
    }

    // ---------------- SAVE TO ROOM ----------------
    private fun saveSteps(steps: Int) {

        val repo = FitnessRepository(application)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val today = calendar.timeInMillis

        val weight = prefs.getCurrentWeight().toDouble()
        val height = prefs.getHeight().toDouble()

        val stepLength = (height * 0.415) / 100

        val calories = (steps * stepLength * weight * 0.035).toInt()

        CoroutineScope(Dispatchers.IO).launch {

            val uid = userId ?: return@launch

            repo.upsertLog(
                FitnessLogEntity(
                    date = today,
                    userId = uid,
                    steps = steps,
                    water = 0,
                    calories = calories
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}