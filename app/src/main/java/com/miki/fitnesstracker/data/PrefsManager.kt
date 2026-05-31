package com.miki.fitnesstracker.data

import android.content.Context

class PrefsManager(context: Context) {
    private val prefs = context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_STEPS_GOAL = "steps_goal"
        private const val KEY_WATER_GOAL = "water_goal"
        private const val KEY_CURRENT_WEIGHT = "current_weight"
        private const val KEY_TARGET_WEIGHT = "target_weight"
        private const val KEY_USERNAME = "username"
        private const val KEY_AGE = "age"
        private const val KEY_HEIGHT = "height"
        private const val KEY_WATER_REMINDER = "water_reminder"
    }

    // Steps goal
    fun saveStepsGoal(value: Int) = prefs.edit().putInt(KEY_STEPS_GOAL, value).apply()
    fun getStepsGoal(): Int = prefs.getInt(KEY_STEPS_GOAL, 10000)

    // Water goal
    fun saveWaterGoal(value: Int) = prefs.edit().putInt(KEY_WATER_GOAL, value).apply()
    fun getWaterGoal(): Int = prefs.getInt(KEY_WATER_GOAL, 2000)

    // Current/target weight
    fun setCurrentWeight(weight: Float) = prefs.edit().putFloat(KEY_CURRENT_WEIGHT, weight).apply()
    fun getCurrentWeight(): Float = prefs.getFloat(KEY_CURRENT_WEIGHT, 75.0f)

    fun setTargetWeight(weight: Float) = prefs.edit().putFloat(KEY_TARGET_WEIGHT, weight).apply()
    fun getTargetWeight(): Float = prefs.getFloat(KEY_TARGET_WEIGHT, 70.0f)

    // Username
    fun setUsername(name: String) = prefs.edit().putString(KEY_USERNAME, name).apply()
    fun getUsername(): String = prefs.getString(KEY_USERNAME, "Guest User") ?: "Guest User"

    // Age
    fun setAge(age: Int) = prefs.edit().putInt(KEY_AGE, age).apply()
    fun getAge(): Int = prefs.getInt(KEY_AGE, 30)

    // Height
    fun setHeight(height: Int) = prefs.edit().putInt(KEY_HEIGHT, height).apply()
    fun getHeight(): Int = prefs.getInt(KEY_HEIGHT, 175)

    // Water reminder toggle
    fun setWaterReminderEnabled(enabled: Boolean) = prefs.edit().putBoolean(KEY_WATER_REMINDER, enabled).apply()
    fun isWaterReminderEnabled(): Boolean = prefs.getBoolean(KEY_WATER_REMINDER, true)
}
