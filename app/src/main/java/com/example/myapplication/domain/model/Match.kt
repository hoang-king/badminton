package com.example.myapplication.domain.model

data class Match(
    val matchNumber: Int,
    val team1Index: Int,
    val team2Index: Int,
    val team1: List<String>,
    val team2: List<String>,
    val winnerIndex: Int? = null // null = chưa có kết quả, 0 = đội 1 thắng, 1 = đội 2 thắng
)
