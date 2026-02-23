package com.example.myapplication.data.repository

import com.example.myapplication.data.RoundRobinHistoryDao
import com.example.myapplication.data.RoundRobinHistoryEntity
import kotlinx.coroutines.flow.Flow

class RoundRobinHistoryRepository(private val dao: RoundRobinHistoryDao) {
    
    fun getAllHistory(): Flow<List<RoundRobinHistoryEntity>> = dao.getAllHistory()
    
    suspend fun saveHistory(history: RoundRobinHistoryEntity): Long {
        return dao.insertHistory(history)
    }
    
    suspend fun getHistoryById(id: Int): RoundRobinHistoryEntity? {
        return dao.getHistoryById(id)
    }
    
    suspend fun deleteHistory(id: Int) {
        dao.deleteHistoryById(id)
    }
    
    fun getHistoryCount(): Flow<Int> = dao.getHistoryCount()
}
