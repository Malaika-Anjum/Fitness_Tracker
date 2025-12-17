package com.miki.fitnesstracker.room



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FitnessDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLog(log: FitnessLogEntity)

    @Query("SELECT * FROM fitness_logs ORDER BY date DESC")
    suspend fun getAllLogs(): List<FitnessLogEntity>

    @Query("SELECT * FROM fitness_logs WHERE date = :date LIMIT 1")
    suspend fun getLogByDate(date: Long): FitnessLogEntity?
}

