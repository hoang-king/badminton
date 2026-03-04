package com.example.myapplication.domain.model

data class Match(
    val matchNumber: Int,
    val team1Index: Int,
    val team2Index: Int,
    val team1: List<String>,
    val team2: List<String>,
    val winnerIndex: Int? = null,
    val score1: Int? = null,
    val score2: Int? = null,
    val isBo3: Boolean = false,
    val setScores1: List<Int?> = listOf(null, null, null),
    val setScores2: List<Int?> = listOf(null, null, null)
)
