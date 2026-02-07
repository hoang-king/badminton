package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _playerInput = MutableStateFlow("")
    val playerInput = _playerInput.asStateFlow()

    private val _teams = MutableStateFlow<List<List<String>>>(emptyList())
    val teams = _teams.asStateFlow()

    fun setPlayerInput(input: String) {
        _playerInput.value = input
    }

    fun randomTeams() {
        val players = playerInput.value
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .shuffled()

        val groupedTeams = players.chunked(2)

        _teams.value = groupedTeams
    }

    // Hàm này dùng để lấy nội dung Share Sheet
    fun getShareMessage(): String {
        val teamsText = _teams.value.mapIndexed { index, team ->
            val playersList = team.joinToString("\n") { "  • $it" }  // thêm indent cho danh sách thành viên
            "✨ **Đội ${index + 1}**" + " (${team.size} người):\n$playersList"

        }.joinToString("\n\n")

        return """
        🎮 **KẾT QUẢ RANDOM ĐỘI** 🎮

        👉 Tổng số người chơi: **${_teams.value.sumOf { it.size }}**

        👉 Số đội: **${_teams.value.size}**

        $teamsText
    """.trimIndent()
    }

}
