package com.example.myapplication.domain.usecase

/**
 * UseCase để chia đội ngẫu nhiên từ danh sách tên người chơi.
 */
class RandomTeamsUseCase {
    /**
     * @param playerInput Chuỗi chứa danh sách người chơi, phân tách bằng dấu phẩy, chấm phẩy hoặc xuống dòng.
     * @param teamSize Số lượng người chơi tối đa trong một đội (mặc định là 2 cho đánh đôi).
     * @return Danh sách các đội, mỗi đội là một danh sách tên người chơi.
     */
    operator fun invoke(playerInput: String, teamSize: Int = 2): List<List<String>> {
        // Tách chuỗi dựa trên các ký tự phân tách phổ biến
        val players = playerInput
            .split(Regex("[\n,;]+"))
            .map { 
                it.trim()
                    .removePrefix("-")
                    .removePrefix("•")
                    .removePrefix("*")
                    .trim() 
            }
            .filter { it.isNotBlank() }
            .shuffled() // Trộn ngẫu nhiên

        if (players.isEmpty()) return emptyList()

        // Chia nhóm theo kích thước đội yêu cầu
        return players.chunked(teamSize)
    }
}
