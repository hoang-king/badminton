package com.example.myapplication.presentation.game

import androidx.lifecycle.ViewModel
import com.example.myapplication.domain.usecase.RandomTeamsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange

class GameViewModel : ViewModel() {

    private val randomTeamsUseCase = RandomTeamsUseCase()

    private val _playerInput = MutableStateFlow(TextFieldValue(""))
    val playerInput = _playerInput.asStateFlow()

    private val _teams = MutableStateFlow<List<List<String>>>(emptyList())
    val teams = _teams.asStateFlow()

    fun setPlayerInput(newValue: TextFieldValue) {
        val oldText = _playerInput.value.text
        val newText = newValue.text

        // 1. Nếu bắt đầu nhập ký tự đầu tiên
        if (oldText.isEmpty() && newText.isNotEmpty() && !newText.startsWith("-")) {
            val formatted = "- $newText"
            _playerInput.value = TextFieldValue(
                text = formatted,
                selection = TextRange(formatted.length)
            )
            return
        }

        // 2. Nếu nhấn Enter xuống dòng
        if (newText.length > oldText.length && newText.endsWith("\n")) {
            val formatted = "$newText- "
            _playerInput.value = TextFieldValue(
                text = formatted,
                selection = TextRange(formatted.length)
            )
            return
        }

        _playerInput.value = newValue
    }

    fun randomTeams() {
        _teams.value = randomTeamsUseCase(playerInput.value.text)
    }

    // Hàm này dùng để lấy nội dung Share Sheet
    fun getShareMessage(): String {
        val teamsText = _teams.value.mapIndexed { index, team ->
            val playersList = team.joinToString("\n") { "  - $it" }
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
