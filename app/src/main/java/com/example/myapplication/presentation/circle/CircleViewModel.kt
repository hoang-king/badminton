package com.example.myapplication.presentation.circle

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.model.Match
import com.example.myapplication.domain.usecase.GenerateRoundRobinMatchesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CircleViewModel : ViewModel() {
    
    private val generateRoundRobinMatchesUseCase = GenerateRoundRobinMatchesUseCase()
    
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _teams = MutableStateFlow<List<List<String>>>(emptyList())
    val teams = _teams.asStateFlow()

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

    fun getTotalTeams(): Int = _teams.value.size
    fun getTotalMatches(): Int = _matches.value.size
}
