package com.example.myapplication.presentation.circle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.RoundRobinHistoryEntity
import com.example.myapplication.data.repository.RoundRobinHistoryRepository
import com.example.myapplication.domain.model.Match
import com.example.myapplication.domain.usecase.GenerateRoundRobinMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CircleViewModel @Inject constructor(
    private val generateRoundRobinMatchesUseCase: GenerateRoundRobinMatchesUseCase,
    private val repository: RoundRobinHistoryRepository
) : ViewModel() {
    
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

    private val _selectedMatchForScore = MutableStateFlow<Match?>(null)
    val selectedMatchForScore = _selectedMatchForScore.asStateFlow()

    fun openScoreDialog(match: Match) {
        _selectedMatchForScore.value = match
    }

    fun closeScoreDialog() {
        _selectedMatchForScore.value = null
    }

    fun updateMatchResult(
        matchNumber: Int, 
        score1: Int?, 
        score2: Int?, 
        isBo3: Boolean,
        setScores1: List<Int?>,
        setScores2: List<Int?>
    ) {
        _matches.value = _matches.value.map { match ->
            if (match.matchNumber == matchNumber) {
                val winnerIndex = when {
                    score1 == null || score2 == null -> null
                    score1 > score2 -> 0
                    score2 > score1 -> 1
                    else -> null
                }
                match.copy(
                    score1 = score1, 
                    score2 = score2, 
                    winnerIndex = winnerIndex,
                    isBo3 = isBo3,
                    setScores1 = setScores1,
                    setScores2 = setScores2
                )
            } else {
                match
            }
        }
        checkAllMatchesCompleted()
    }

    fun updateMatchScore(matchNumber: Int, score1: Int?, score2: Int?) {
        _matches.value = _matches.value.map { match ->
            if (match.matchNumber == matchNumber) {
                val winnerIndex = when {
                    score1 == null || score2 == null -> null
                    score1 > score2 -> 0
                    score2 > score1 -> 1
                    else -> null // Hoà (trong cầu lông thường không hoà, nhưng để null nếu bằng nhau)
                }
                match.copy(score1 = score1, score2 = score2, winnerIndex = winnerIndex)
            } else {
                match
            }
        }
        checkAllMatchesCompleted()
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

    fun saveToHistory(matches: List<Match>, teams: List<List<String>>) {
        viewModelScope.launch {
            try {
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
