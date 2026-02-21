package com.example.myapplication.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RandomTeamsUseCaseTest {

    private val useCase = RandomTeamsUseCase()

    @Test
    fun `invoke with newline should split players correctly`() {
        val input = """
            Player 1
            Player 2
            Player 3
            Player 4
        """.trimIndent()
        val result = useCase(input)
        
        assertEquals(2, result.size)
        assertEquals(4, result.flatten().size)
    }

    @Test
    fun `invoke with comma should split players correctly`() {
        val input = "Player 1, Player 2, Player 3, Player 4"
        val result = useCase(input)
        
        assertEquals(2, result.size)
        assertEquals(4, result.flatten().size)
        assertTrue(result.flatten().contains("Player 1"))
    }

    @Test
    fun `invoke with semicolon and spaces should clean names`() {
        val input = " Player 1 ; Player 2 ;Player 3;  Player 4  "
        val result = useCase(input)
        
        assertEquals(2, result.size)
        assertEquals(4, result.flatten().size)
        assertTrue(result.flatten().contains("Player 1"))
        assertTrue(result.flatten().contains("Player 4"))
    }

    @Test
    fun `invoke with empty input should return empty list`() {
        val input = ""
        val result = useCase(input)
        
        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke with odd number of players should have one smaller team`() {
        val input = "P1, P2, P3"
        val result = useCase(input)
        
        assertEquals(2, result.size)
        assertEquals(2, result[0].size)
        assertEquals(1, result[1].size)
    }
}
