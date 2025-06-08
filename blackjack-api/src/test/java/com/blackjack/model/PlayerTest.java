package com.blackjack.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;
    private static final String USERNAME = "testPlayer";
    private static final String EMAIL = "test@example.com";
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000);

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setUsername(USERNAME);
        player.setEmail(EMAIL);
        player.setBalance(INITIAL_BALANCE);
    }

    @Test
    void testPlayerCreation() {
        assertEquals(USERNAME, player.getUsername());
        assertEquals(EMAIL, player.getEmail());
        assertEquals(INITIAL_BALANCE, player.getBalance());
        assertEquals(0, player.getGamesPlayed());
        assertEquals(0, player.getGamesWon());
        assertEquals(BigDecimal.ZERO, player.getTotalWinnings());
    }

    @Test
    void testUpdateStatistics() {
        BigDecimal winAmount = BigDecimal.valueOf(100);
        player.updateStatistics(true, winAmount);
        
        assertEquals(1, player.getGamesPlayed());
        assertEquals(1, player.getGamesWon());
        assertEquals(winAmount, player.getTotalWinnings());
        assertEquals(INITIAL_BALANCE.add(winAmount), player.getBalance());

        // Test losing game
        BigDecimal loseAmount = BigDecimal.valueOf(50);
        player.updateStatistics(false, loseAmount);
        
        assertEquals(2, player.getGamesPlayed());
        assertEquals(1, player.getGamesWon());
        assertEquals(winAmount, player.getTotalWinnings());
        assertEquals(INITIAL_BALANCE.add(winAmount).subtract(loseAmount), player.getBalance());
    }

    @Test
    void testWinRate() {
        // Win 2 games
        player.updateStatistics(true, BigDecimal.TEN);
        player.updateStatistics(true, BigDecimal.TEN);
        
        // Lose 2 games
        player.updateStatistics(false, BigDecimal.TEN);
        player.updateStatistics(false, BigDecimal.TEN);
        
        assertEquals(4, player.getGamesPlayed());
        assertEquals(2, player.getGamesWon());
        assertEquals(0.5, player.getWinRate());
    }

    @Test
    void testNegativeBalanceNotAllowed() {
        assertThrows(IllegalArgumentException.class, () -> 
            player.setBalance(BigDecimal.valueOf(-100)));
    }

    @Test
    void testToString() {
        String playerString = player.toString();
        assertTrue(playerString.contains(USERNAME));
        assertTrue(playerString.contains(EMAIL));
        assertTrue(playerString.contains(INITIAL_BALANCE.toString()));
    }
} 