package com.blackjack.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;
    private static final Long PLAYER_ID = 1L;
    private static final BigDecimal BET_AMOUNT = BigDecimal.valueOf(100);

    @BeforeEach
    void setUp() {
        game = new Game(PLAYER_ID, BET_AMOUNT);
    }

    @Test
    void testNewGameInitialization() {
        assertEquals(PLAYER_ID, game.getPlayerId());
        assertEquals(BET_AMOUNT, game.getBet());
        assertEquals(Game.GameStatus.IN_PROGRESS, game.getStatus());
        assertNotNull(game.getPlayerHand());
        assertNotNull(game.getDealerHand());
        assertNotNull(game.getStartTime());
        assertTrue(game.getActions().isEmpty());
    }

    @Test
    void testGameActions() {
        game.addAction(Game.GameAction.HIT);
        game.addAction(Game.GameAction.STAND);
        
        assertEquals(2, game.getActions().size());
        assertEquals(Game.GameAction.HIT, game.getActions().get(0));
        assertEquals(Game.GameAction.STAND, game.getActions().get(1));
    }

    @Test
    void testCompleteGame() {
        game.completeGame(Game.GameResult.PLAYER_WIN);
        
        assertEquals(Game.GameStatus.COMPLETED, game.getStatus());
        assertEquals(Game.GameResult.PLAYER_WIN, game.getResult());
        assertNotNull(game.getEndTime());
    }

    @Test
    void testPlayerTurn() {
        assertTrue(game.isPlayerTurn());  // New game starts with player's turn

        // Simulate blackjack
        game.getPlayerHand().addCard(new Card(com.blackjack.model.Card.Suit.HEARTS, com.blackjack.model.Card.Rank.ACE));
        game.getPlayerHand().addCard(new Card(com.blackjack.model.Card.Suit.SPADES, com.blackjack.model.Card.Rank.KING));
        
        assertFalse(game.isPlayerTurn());  // Player's turn ends with blackjack
    }

    @Test
    void testDealerTurn() {
        // Initially, it's player's turn
        assertFalse(game.isDealerTurn());

        // After player stands with valid hand
        game.getPlayerHand().addCard(new Card(com.blackjack.model.Card.Suit.HEARTS, com.blackjack.model.Card.Rank.TEN));
        game.getPlayerHand().addCard(new Card(com.blackjack.model.Card.Suit.SPADES, com.blackjack.model.Card.Rank.EIGHT));
        game.addAction(Game.GameAction.STAND);
        
        assertTrue(game.isDealerTurn());

        // After player busts
        game.getPlayerHand().addCard(new Card(com.blackjack.model.Card.Suit.DIAMONDS, com.blackjack.model.Card.Rank.KING));
        assertFalse(game.isDealerTurn());
    }
} 