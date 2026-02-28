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

    private val _femaleInput = MutableStateFlow(TextFieldValue(""))
    val femaleInput = _femaleInput.asStateFlow()

    private val _gameMode = MutableStateFlow(RandomTeamsUseCase.GameMode.DOUBLES)
    val gameMode = _gameMode.asStateFlow()

    private val _teams = MutableStateFlow<List<List<String>>>(emptyList())
    val teams = _teams.asStateFlow()

    fun setGameMode(mode: RandomTeamsUseCase.GameMode) {
        _gameMode.value = mode
    }

    private fun formatInput(oldText: String, newValue: TextFieldValue): TextFieldValue {
        val newText = newValue.text

        // 1. Nếu bắt đầu nhập ký tự đầu tiên
        if (oldText.isEmpty() && newText.isNotEmpty() && !newText.startsWith("-")) {
            val formatted = "- $newText"
            return TextFieldValue(
                text = formatted,
                selection = TextRange(formatted.length)
            )
        }

        // 2. Nếu nhấn Enter xuống dòng
        if (newText.length > oldText.length && newText.endsWith("\n")) {
            val formatted = "$newText- "
            return TextFieldValue(
                text = formatted,
                selection = TextRange(formatted.length)
            )
        }

        return newValue
    }

    fun setPlayerInput(newValue: TextFieldValue) {
        _playerInput.value = formatInput(_playerInput.value.text, newValue)
    }

    fun setFemaleInput(newValue: TextFieldValue) {
        _femaleInput.value = formatInput(_femaleInput.value.text, newValue)
    }

    fun randomTeams() {
        _teams.value = randomTeamsUseCase(
            playerInput = playerInput.value.text,
            mode = gameMode.value,
            femaleInput = femaleInput.value.text
        )
    }

    fun sortTeams() {
        _teams.value = _teams.value.sortedWith(
            compareByDescending<List<String>> { it.size }
                .thenBy { it.firstOrNull() ?: "" }
        )
    }

    fun resetAll() {
        _playerInput.value = TextFieldValue("")
        _femaleInput.value = TextFieldValue("")
        _teams.value = emptyList()
    }

    // Hàm này dùng để lấy nội dung Share Sheet
    fun getShareMessage(): String {
        val modeText = when (gameMode.value) {
            RandomTeamsUseCase.GameMode.SINGLES -> "Đánh Đơn"
            RandomTeamsUseCase.GameMode.DOUBLES -> "Đánh Đôi"
            RandomTeamsUseCase.GameMode.MIXED_DOUBLES -> "Đôi Nam Nữ"
        }

        val teamsText = _teams.value.mapIndexed { index, team ->
            val playersList = team.joinToString("\n") { "  - $it" }
            "✨ **Đội ${index + 1}**" + " (${team.size} người):\n$playersList"

        }.joinToString("\n\n")

        return """
        🎮 **KẾT QUẢ RANDOM ĐỘI** 🎮

        👉 Chế độ: **$modeText**
        👉 Tổng số người chơi: **${_teams.value.sumOf { it.size }}**
        👉 Số đội: **${_teams.value.size}**

        $teamsText
    """.trimIndent()
    }
}
