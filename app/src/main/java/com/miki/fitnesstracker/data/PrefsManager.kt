package com.miki.fitnesstracker.data

import android.content.Context

class PrefsManager(context: Context) {

    private val prefs =
        context.getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_STEPS_GOAL = "steps_goal"
        private const val KEY_WATER_GOAL = "water_goal"
        private const val KEY_CURRENT_WEIGHT = "current_weight"
        private const val KEY_TARGET_WEIGHT = "target_weight"
        private const val KEY_USERNAME = "username"
    }

    fun saveStepsGoal(value: Int) {
        prefs.edit().putInt(KEY_STEPS_GOAL, value).apply()
    }

    fun getStepsGoal(): Int {
        return prefs.getInt(KEY_STEPS_GOAL, 10000)
    }

    fun saveWaterGoal(value: Int) {
        prefs.edit().putInt(KEY_WATER_GOAL, value).apply()
    }

    fun getWaterGoal(): Int {
        return prefs.getInt(KEY_WATER_GOAL, 2000)
    }

    fun saveWeight(current: Float, target: Float) {
        prefs.edit()
            .putFloat(KEY_CURRENT_WEIGHT, current)
            .putFloat(KEY_TARGET_WEIGHT, target)
            .apply()
    }

    fun getCurrentWeight(): Float {
        return prefs.getFloat(KEY_CURRENT_WEIGHT, 75.0f)
    }

    fun getTargetWeight(): Float {
        return prefs.getFloat(KEY_TARGET_WEIGHT, 70.0f)
    }

    fun saveUsername(name: String) {
        prefs.edit().putString(KEY_USERNAME, name).apply()
    }

    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "Guest User") ?: "Guest User"
    }
}
