package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.BracketMatch
import kotlin.math.log2

class GenerateBracketUseCase {
    operator fun invoke(teams: List<List<String>>): Pair<List<BracketMatch>, Int> {
        if (teams.isEmpty()) {
            return Pair(emptyList(), 0)
        }

        val numTeams = teams.size

        // Tìm kích thước bracket (lũy thừa 2 gần nhất)
        var totalSlots = 1
        while (totalSlots < numTeams) totalSlots *= 2

        val rounds = log2(totalSlots.toDouble()).toInt()
        val matches = mutableListOf<BracketMatch>()

        // Tạo Round 1 (leaf nodes)
        val numMatchesR1 = totalSlots / 2
        for (i in 0 until numMatchesR1) {
            val team1 = teams.getOrNull(i * 2)
            val team2 = teams.getOrNull(i * 2 + 1)

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

        return Pair(matches.sortedWith(compareBy({ it.roundIndex }, { it.matchIndex })), rounds)
    }
}
