package com.miki.fitnesstracker.room


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fitness_logs")
data class FitnessLogEntity(
    @PrimaryKey val date: Long, // use date as primary key
    val water: Int,
    val calories: Int,
    val steps: Int
)
