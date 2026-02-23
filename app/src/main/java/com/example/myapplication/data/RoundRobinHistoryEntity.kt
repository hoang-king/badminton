package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "round_robin_history")
@Serializable
data class RoundRobinHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val teams: String, // JSON string của danh sách đội
    val results: String, // JSON string của kết quả các trận
    val winnerTeam: String?, // Tên đội thắng chung cuộc (sau bracket)
    val winnerTeamIndex: Int? = null, // Index của đội thắng
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String? = null
)
