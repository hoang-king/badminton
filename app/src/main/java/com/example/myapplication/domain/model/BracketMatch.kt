package com.example.myapplication.domain.model

data class BracketMatch(
    val roundIndex: Int,
    val matchIndex: Int,
    val team1: List<String>? = null,
    val team2: List<String>? = null,
    val team1Index: Int? = null,
    val team2Index: Int? = null,
    val winner: Int? = null,
    val score1: Int? = null,
    val score2: Int? = null,
    val isBo3: Boolean = false,
    val setScores1: List<Int?> = listOf(null, null, null),
    val setScores2: List<Int?> = listOf(null, null, null)
)
