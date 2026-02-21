package com.example.myapplication.domain.model

data class BracketMatch(
    val roundIndex: Int,
    val matchIndex: Int,
    val team1: List<String>? = null,
    val team2: List<String>? = null,
    val team1Index: Int? = null,
    val team2Index: Int? = null,
    val winner: Int? = null // 1 hoặc 2
)
