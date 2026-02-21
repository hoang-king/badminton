package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Match

class GenerateRoundRobinMatchesUseCase {
    operator fun invoke(teamsList: List<List<String>>): List<Match> {
        if (teamsList.isEmpty()) {
            return emptyList()
        }

        // Tạo tất cả các cặp đấu có thể
        val allPairs = mutableListOf<Pair<Int, Int>>()
        for (i in teamsList.indices) {
            for (j in i + 1 until teamsList.size) {
                allPairs.add(Pair(i, j))
            }
        }

        // Sắp xếp các trận đấu để đội vừa đấu không đấu liên tiếp
        val scheduledMatches = scheduleMatchesWithRest(allPairs, teamsList.size)

        // Chuyển đổi thành Match objects
        return scheduledMatches.mapIndexed { index, pair ->
            Match(
                matchNumber = index + 1,
                team1Index = pair.first,
                team2Index = pair.second,
                team1 = teamsList[pair.first],
                team2 = teamsList[pair.second]
            )
        }
    }

    private fun scheduleMatchesWithRest(
        allPairs: List<Pair<Int, Int>>,
        numTeams: Int
    ): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val remaining = allPairs.toMutableList()
        val lastPlayedRound = mutableSetOf<Int>() // Đội đã đấu ở trận trước

        while (remaining.isNotEmpty()) {
            var foundMatch = false

            // Tìm trận đấu mà cả 2 đội đều không nằm trong lastPlayedRound
            for (pair in remaining) {
                if (pair.first !in lastPlayedRound && pair.second !in lastPlayedRound) {
                    result.add(pair)
                    remaining.remove(pair)

                    // Cập nhật các đội vừa đấu
                    lastPlayedRound.clear()
                    lastPlayedRound.add(pair.first)
                    lastPlayedRound.add(pair.second)

                    foundMatch = true
                    break
                }
            }

            // Nếu không tìm được trận nào (tất cả đội còn lại đều vừa đấu)
            if (!foundMatch) {
                // Tìm trận có ít nhất 1 đội chưa đấu ở trận trước
                val nextMatch = remaining.firstOrNull { pair ->
                    pair.first !in lastPlayedRound || pair.second !in lastPlayedRound
                }

                if (nextMatch != null) {
                    result.add(nextMatch)
                    remaining.remove(nextMatch)

                    lastPlayedRound.clear()
                    lastPlayedRound.add(nextMatch.first)
                    lastPlayedRound.add(nextMatch.second)
                } else {
                    // Trường hợp cuối cùng: chọn trận đầu tiên còn lại
                    val fallbackMatch = remaining.removeAt(0)
                    result.add(fallbackMatch)

                    lastPlayedRound.clear()
                    lastPlayedRound.add(fallbackMatch.first)
                    lastPlayedRound.add(fallbackMatch.second)
                }
            }
        }

        return result
    }
}
