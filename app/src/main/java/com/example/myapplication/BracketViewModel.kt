package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.log2

/**
 * Data class cho một trận đấu trong bracket
 */
data class BracketMatch(
    val roundIndex: Int,
    val matchIndex: Int,
    val team1: List<String>? = null,
    val team2: List<String>? = null,
    val team1Index: Int? = null,
    val team2Index: Int? = null,
    val winner: Int? = null // 1 hoặc 2
)

/**
 * ViewModel quản lý Knockout Bracket theo mô hình cây nhị phân
 */
class BracketViewModel : ViewModel() {

    private val _matches = MutableStateFlow<List<BracketMatch>>(emptyList())
    val matches = _matches.asStateFlow()

    private val _teams = MutableStateFlow<List<List<String>>>(emptyList())
    val teams = _teams.asStateFlow()

    private val _totalRounds = MutableStateFlow(0)
    val totalRounds = _totalRounds.asStateFlow()

    /**
     * Khởi tạo bracket với danh sách đội
     */
    fun setTeams(teams: List<List<String>>) {
        _teams.value = teams.shuffled()
        generateBracket()
    }

    /**
     * Sinh bracket theo cấu trúc cây nhị phân
     */
    private fun generateBracket() {
        val teamList = _teams.value
        if (teamList.isEmpty()) {
            _matches.value = emptyList()
            _totalRounds.value = 0
            return
        }

        val numTeams = teamList.size

        // Tìm kích thước bracket (lũy thừa 2 gần nhất)
        var totalSlots = 1
        while (totalSlots < numTeams) totalSlots *= 2

        val rounds = log2(totalSlots.toDouble()).toInt()
        _totalRounds.value = rounds

        val matches = mutableListOf<BracketMatch>()

        // Tạo Round 1 (leaf nodes)
        val numMatchesR1 = totalSlots / 2
        for (i in 0 until numMatchesR1) {
            val team1 = teamList.getOrNull(i * 2)
            val team2 = teamList.getOrNull(i * 2 + 1)

            matches.add(
                BracketMatch(
                    roundIndex = 0,
                    matchIndex = i,
                    team1 = team1,
                    team2 = team2,
                    team1Index = if (team1 != null) i * 2 else null,
                    team2Index = if (team2 != null) i * 2 + 1 else null,
                    winner = if (team1 != null && team2 == null) 1 else null
                )
            )
        }

        // Tạo các round còn lại (internal nodes)
        for (round in 1 until rounds) {
            val matchesInRound = totalSlots / (1 shl (round + 1))
            for (m in 0 until matchesInRound) {
                matches.add(
                    BracketMatch(
                        roundIndex = round,
                        matchIndex = m
                    )
                )
            }
        }

        // Tự động propagate winners (đặc biệt cho bye matches)
        propagateWinners(matches)

        _matches.value = matches.sortedWith(compareBy({ it.roundIndex }, { it.matchIndex }))
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
            }

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
        generateBracket()
    }
}