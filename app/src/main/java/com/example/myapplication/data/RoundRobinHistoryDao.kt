package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoundRobinHistoryDao {
    @Insert
    suspend fun insertHistory(history: RoundRobinHistoryEntity): Long

    @Query("SELECT * FROM round_robin_history ORDER BY createdAt DESC")
    fun getAllHistory(): Flow<List<RoundRobinHistoryEntity>>

    @Query("SELECT * FROM round_robin_history WHERE id = :id")
    suspend fun getHistoryById(id: Int): RoundRobinHistoryEntity?

    @Delete
    suspend fun deleteHistory(history: RoundRobinHistoryEntity)

    @Query("DELETE FROM round_robin_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Int)

    @Query("SELECT COUNT(*) FROM round_robin_history")
    fun getHistoryCount(): Flow<Int>
}
