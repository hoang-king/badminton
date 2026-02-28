package com.example.myapplication.domain.usecase

/**
 * UseCase để chia đội ngẫu nhiên từ danh sách tên người chơi.
 */
class RandomTeamsUseCase {
    enum class GameMode {
        SINGLES,
        DOUBLES,
        MIXED_DOUBLES
    }

    /**
     * @param playerInput Chuỗi chứa danh sách người chơi, phân tách bằng dấu phẩy, chấm phẩy hoặc xuống dòng.
     * @param mode Chế độ chơi (Đơn, Đôi, Đôi nam nữ).
     * @param femaleInput Chuỗi chứa danh sách người chơi nữ (chỉ dùng cho MIXED_DOUBLES).
     * @return Danh sách các đội, mỗi đội là một danh sách tên người chơi.
     */
    operator fun invoke(
        playerInput: String,
        mode: GameMode = GameMode.DOUBLES,
        femaleInput: String = ""
    ): List<List<String>> {
        val players = parsePlayers(playerInput)
        
        return when (mode) {
            GameMode.SINGLES -> players.shuffled().chunked(1)
            GameMode.DOUBLES -> players.shuffled().chunked(2)
            GameMode.MIXED_DOUBLES -> {
                val males = players.shuffled().toMutableList()
                val females = parsePlayers(femaleInput).shuffled().toMutableList()
                
                val teams = mutableListOf<List<String>>()
                
                // Ghép cặp nam và nữ
                while (males.isNotEmpty() && females.isNotEmpty()) {
                    teams.add(listOf(males.removeAt(0), females.removeAt(0)))
                }
                
                // Xử lý những người còn dư (nếu có)
                males.forEach { teams.add(listOf(it)) }
                females.forEach { teams.add(listOf(it)) }
                
                teams
            }
        }
    }

    private fun parsePlayers(input: String): List<String> {
        return input
            .split(Regex("[\n,;]+"))
            .map { 
                it.trim()
                    .removePrefix("-")
                    .removePrefix("•")
                    .removePrefix("*")
                    .trim() 
            }
            .filter { it.isNotBlank() }
    }
}
