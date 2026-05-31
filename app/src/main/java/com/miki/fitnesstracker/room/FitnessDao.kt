package com.miki.fitnesstracker.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FitnessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLog(log: FitnessLogEntity)

    @Query("SELECT * FROM fitness_logs WHERE userId = :userId ORDER BY date DESC")
    fun getAllLogs(userId: String): LiveData<List<FitnessLogEntity>>

    @Query("SELECT * FROM fitness_logs WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getLogByDate(userId: String, date: Long): FitnessLogEntity?

    @Query("SELECT * FROM fitness_logs WHERE userId = :userId")
    suspend fun getAllLogsSync(userId: String): List<FitnessLogEntity>

    @Query("""
        UPDATE fitness_logs
        SET steps = 0, water = 0, calories = 0
        WHERE userId = :userId AND date = :date
    """)
    suspend fun resetToday(userId: String, date: Long)

    @Query("SELECT COUNT(*) FROM fitness_logs WHERE userId = :userId AND date = :date")
    suspend fun exists(userId: String, date: Long): Int
}