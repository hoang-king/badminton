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

    @Test
    fun `invoke with SINGLES mode should create teams of size 1`() {
        val input = "P1, P2, P3"
        val result = useCase(input, mode = RandomTeamsUseCase.GameMode.SINGLES)
        
        assertEquals(3, result.size)
        assertTrue(result.all { it.size == 1 })
    }

    @Test
    fun `invoke with MIXED_DOUBLES mode should pair males and females`() {
        val males = "M1, M2"
        val females = "F1, F2"
        val result = useCase(males, mode = RandomTeamsUseCase.GameMode.MIXED_DOUBLES, femaleInput = females)
        
        assertEquals(2, result.size)
        result.forEach { team ->
            assertEquals(2, team.size)
            // One male and one female in each team
            assertTrue(team.any { it.startsWith("M") })
            assertTrue(team.any { it.startsWith("F") })
        }
    }

    @Test
    fun `invoke with MIXED_DOUBLES mode and unequal counts should handle remainder`() {
        val males = "M1, M2"
        val females = "F1"
        val result = useCase(males, mode = RandomTeamsUseCase.GameMode.MIXED_DOUBLES, femaleInput = females)
        
        assertEquals(2, result.size)
        // One team should have M and F, the other just M
        val mixedTeam = result.find { it.size == 2 }
        val singleTeam = result.find { it.size == 1 }
        
        assertTrue(mixedTeam != null)
        assertTrue(singleTeam != null)
        assertTrue(mixedTeam!!.contains("F1"))
        assertTrue(singleTeam!![0].startsWith("M"))
    }
}
