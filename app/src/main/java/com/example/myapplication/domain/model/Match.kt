package com.example.myapplication.domain.model

data class Match(
    val matchNumber: Int,
    val team1Index: Int,
    val team2Index: Int,
    val team1: List<String>,
    val team2: List<String>
)
