package com.example.myapplication.presentation.score

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScoreViewModel : ViewModel() {
    private val _scoreA = MutableStateFlow(0)
    val scoreA = _scoreA.asStateFlow()

    private val _scoreB = MutableStateFlow(0)
    val scoreB = _scoreB.asStateFlow()

    private val _winner = MutableStateFlow<String?>(null)
    val winner = _winner.asStateFlow()

    fun incrementA() {
        if (_winner.value == null) {
            _scoreA.value++
            checkWinner()
        }
    }

    fun decrementA() {
        if (_scoreA.value > 0) _scoreA.value--
    }

    fun incrementB() {
        if (_winner.value == null) {
            _scoreB.value++
            checkWinner()
        }
    }

    fun decrementB() {
        if (_scoreB.value > 0) _scoreB.value--
    }

    fun resetGame() {
        _scoreA.value = 0
        _scoreB.value = 0
        _winner.value = null
    }

    private fun checkWinner() {
        if (_scoreA.value >= 21 && _scoreA.value - _scoreB.value >= 2) {
            _winner.value = "Player 1"
        } else if (_scoreB.value >= 21 && _scoreB.value - _scoreA.value >= 2) {
            _winner.value = "Player 2"
        }
    }
}
