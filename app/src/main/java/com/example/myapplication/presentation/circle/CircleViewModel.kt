package com.example.myapplication.presentation.circle

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.RoundRobinHistoryEntity
import com.example.myapplication.data.repository.RoundRobinHistoryRepository
import com.example.myapplication.domain.model.Match
import com.example.myapplication.domain.usecase.GenerateRoundRobinMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CircleViewModel : ViewModel() {
    
    private val generateRoundRobinMatchesUseCase = GenerateRoundRobinMatchesUseCase()
    
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _teams = MutableStateFlow<List<List<String>>>(emptyList())
    val teams = _teams.asStateFlow()

    private val _showSaveDialog = MutableStateFlow(false)
    val showSaveDialog = _showSaveDialog.asStateFlow()

    fun setTeams(teams: List<List<String>>) {
        if (_matches.value.isEmpty() || _teams.value != teams) {
            _teams.value = teams
            generateRoundRobinMatches()
        }
    }

    private fun generateRoundRobinMatches() {
        if (_teams.value.isEmpty()) return
        _matches.value = generateRoundRobinMatchesUseCase(_teams.value)
    }

    fun setMatchWinner(matchNumber: Int, winnerIndex: Int?) {
        _matches.value = _matches.value.map { match ->
            if (match.matchNumber == matchNumber) {
                match.copy(winnerIndex = winnerIndex)
            } else {
                match
            }
        }
        
        checkAllMatchesCompleted()
    }

    private fun checkAllMatchesCompleted() {
        val allCompleted = _matches.value.isNotEmpty() && _matches.value.all { it.winnerIndex != null }
        _showSaveDialog.value = allCompleted
    }

    fun closeSaveDialog() {
        _showSaveDialog.value = false
    }

    fun saveToHistory(context: Context, matches: List<Match>, teams: List<List<String>>) {
        viewModelScope.launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val repository = RoundRobinHistoryRepository(db.roundRobinHistoryDao())
                
                val results = matches.mapNotNull { it.winnerIndex }
                val winnerIndex = findWinner(matches, teams)
                val winnerTeam = if (winnerIndex >= 0 && winnerIndex < teams.size) {
                    teams[winnerIndex].joinToString(", ")
                } else {
                    "N/A"
                }
                
                val history = RoundRobinHistoryEntity(
                    teams = Json.encodeToString(teams),
                    results = Json.encodeToString(results),
                    winnerTeam = winnerTeam,
                    winnerTeamIndex = if (winnerIndex >= 0) winnerIndex else null,
                    notes = "Vòng tròn"
                )
                
                repository.saveHistory(history)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun findWinner(matches: List<Match>, teams: List<List<String>>): Int {
        val wins = IntArray(teams.size)
        matches.forEach { match ->
            if (match.winnerIndex != null) {
                val winnerIndex = if (match.winnerIndex == 0) match.team1Index else match.team2Index
                wins[winnerIndex]++
            }
        }
        return wins.indices.maxByOrNull { wins[it] } ?: 0
    }

    fun getTotalTeams(): Int = _teams.value.size
    fun getTotalMatches(): Int = _matches.value.size
}
