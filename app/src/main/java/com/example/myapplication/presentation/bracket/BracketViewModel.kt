package com.example.myapplication.presentation.bracket

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.RoundRobinHistoryEntity
import com.example.myapplication.data.repository.RoundRobinHistoryRepository
import com.example.myapplication.domain.model.BracketMatch
import com.example.myapplication.domain.usecase.GenerateBracketUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * ViewModel quản lý Knockout Bracket theo mô hình cây nhị phân
 */
class BracketViewModel : ViewModel() {

    private val generateBracketUseCase = GenerateBracketUseCase()

    private val _matches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _teams = MutableStateFlow<List<List<String>>>(emptyList())
    val teams = _teams.asStateFlow()

    private val _totalRounds = MutableStateFlow(0)
    val totalRounds = _totalRounds.asStateFlow()

    private val _isFinalWon = MutableStateFlow(false)
    val isFinalWon = _isFinalWon.asStateFlow()

    private val _showSaveDialog = MutableStateFlow(false)
    val showSaveDialog = _showSaveDialog.asStateFlow()

    /**
     * Khởi tạo bracket với danh sách đội
     */
    fun setTeams(teams: List<List<String>>) {
        // Chỉ tạo mới nếu danh sách đội thực sự thay đổi hoặc chưa có matches
        if (_matches.value.isEmpty() || _teams.value != teams) {
            _teams.value = teams
            generateBracket()
        }
    }

    /**
     * Sinh bracket theo cấu trúc cây nhị phân
     */
    private fun generateBracket() {
        if (_teams.value.isEmpty()) return
        
        val (matches, rounds) = generateBracketUseCase(_teams.value)
        _matches.value = matches
        _totalRounds.value = rounds
        
        if (matches.isNotEmpty()) {
            val mutableMatches = matches.toMutableList()
            propagateWinners(mutableMatches)
            _matches.value = mutableMatches
        }
    }

    /**
     * Chọn đội thắng cho một trận đấu
     * Logic: Cập nhật winner → Clear downstream → Propagate
     */
    fun selectWinner(roundIndex: Int, matchIndex: Int, winner: Int) {
        val matches = _matches.value.toMutableList()
        val idx = matches.indexOfFirst {
            it.roundIndex == roundIndex && it.matchIndex == matchIndex
        }

        if (idx == -1) return

        val match = matches[idx]
        val winningTeam = if (winner == 1) match.team1 else match.team2
        val winningIndex = if (winner == 1) match.team1Index else match.team2Index

        if (winningTeam == null) return

        // 1. Cập nhật winner
        matches[idx] = match.copy(winner = winner)

        // 2. Clear tất cả matches downstream
        clearDownstream(matches, roundIndex, matchIndex)

        // 3. Propagate winners lên toàn bộ tree
        propagateWinners(matches)

        _matches.value = matches

        // 4. Check if final match has a winner
        val finalMatch = matches.find {
            it.roundIndex == _totalRounds.value - 1 && it.matchIndex == 0
        }
        val isWon = finalMatch?.winner != null
        _isFinalWon.value = isWon
        if (isWon) {
            _showSaveDialog.value = true
        }
    }

    fun closeSaveDialog() {
        _showSaveDialog.value = false
    }

    fun saveToHistory(context: Context) {
        val champion = getChampion()
        val championIndex = getChampionIndex()
        val teams = _teams.value
        val matches = _matches.value

        if (champion == null) return

        viewModelScope.launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val repository = RoundRobinHistoryRepository(db.roundRobinHistoryDao())
                
                val winnerTeamName = champion.joinToString(", ")
                
                // Encode structure for history
                // We use the same format as Circle for compatibility, 
                // but knockout structure is different. For now, we save teams and champion.
                val history = RoundRobinHistoryEntity(
                    teams = Json.encodeToString(teams),
                    results = Json.encodeToString(matches.map { it.winner }),
                    winnerTeam = winnerTeamName,
                    winnerTeamIndex = championIndex,
                    notes = "Knockout"
                )
                
                repository.saveHistory(history)
                _showSaveDialog.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getChampionIndex(): Int? {
        if (_totalRounds.value == 0) return null

        val finalMatch = _matches.value.find {
            it.roundIndex == _totalRounds.value - 1 && it.matchIndex == 0
        }

        return when (finalMatch?.winner) {
            1 -> finalMatch.team1Index
            2 -> finalMatch.team2Index
            else -> null
        }
    }

    /**
     * Xóa tất cả matches ở các vòng sau khi có thay đổi
     * Sử dụng binary tree navigation: parent = child / 2
     */
    private fun clearDownstream(
        matches: MutableList<BracketMatch>,
        fromRound: Int,
        fromMatch: Int
    ) {
        var currentRound = fromRound + 1
        var currentMatch = fromMatch / 2

        while (currentRound < _totalRounds.value) {
            val idx = matches.indexOfFirst {
                it.roundIndex == currentRound && it.matchIndex == currentMatch
            }

            if (idx != -1) {
                val match = matches[idx]
                val isLeftChild = fromMatch % 2 == 0

                matches[idx] = if (isLeftChild) {
                    match.copy(team1 = null, team1Index = null, winner = null)
                } else {
                    match.copy(team2 = null, team2Index = null, winner = null)
                }
            }

            currentRound++
            currentMatch /= 2
        }
    }

    /**
     * Propagate tất cả winners từ các vòng thấp lên cao
     * Binary tree: left child → parent.team1, right child → parent.team2
     */
    private fun propagateWinners(matches: MutableList<BracketMatch>) {
        for (round in 0 until _totalRounds.value - 1) {
            val roundMatches = matches.filter {
                it.roundIndex == round && it.winner != null
            }.toList()

            for (match in roundMatches) {
                val winningTeam = if (match.winner == 1) match.team1 else match.team2
                val winningIndex = if (match.winner == 1) match.team1Index else match.team2Index

                if (winningTeam != null) {
                    val nextRound = round + 1
                    val nextMatchIndex = match.matchIndex / 2

                    val nextIdx = matches.indexOfFirst {
                        it.roundIndex == nextRound && it.matchIndex == nextMatchIndex
                    }

                    if (nextIdx != -1) {
                        val nextMatch = matches[nextIdx]
                        val isLeftChild = match.matchIndex % 2 == 0

                        matches[nextIdx] = if (isLeftChild) {
                            nextMatch.copy(team1 = winningTeam, team1Index = winningIndex)
                        } else {
                            nextMatch.copy(team2 = winningTeam, team2Index = winningIndex)
                        }
                    }
                }
            }
        }
    }

    /**
     * Lấy danh sách trận đấu theo vòng
     */
    fun getMatchesForRound(roundIndex: Int): List<BracketMatch> {
        return _matches.value
            .filter { it.roundIndex == roundIndex }
            .sortedBy { it.matchIndex }
    }

    /**
     * Lấy đội vô địch (winner của final match)
     */
    fun getChampion(): List<String>? {
        if (_totalRounds.value == 0) return null

        val finalMatch = _matches.value.find {
            it.roundIndex == _totalRounds.value - 1 && it.matchIndex == 0
        }

        return when (finalMatch?.winner) {
            1 -> finalMatch.team1
            2 -> finalMatch.team2
            else -> null
        }
    }

    /**
     * Reset bracket (shuffle và tạo lại)
     */
    fun resetBracket() {
        _isFinalWon.value = false
        generateBracket()
    }
}
