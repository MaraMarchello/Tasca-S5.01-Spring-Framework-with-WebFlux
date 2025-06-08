package com.blackjack.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    private Deck deck;

    @BeforeEach
    void setUp() {
        deck = new Deck();
    }

    @Test
    void testNewDeckHas52Cards() {
        assertEquals(52, deck.remainingCards());
    }

    @Test
    void testDrawCard() {
        Card card = deck.drawCard();
        assertNotNull(card);
        assertEquals(51, deck.remainingCards());
    }

    @Test
    void testDrawAllCards() {
        Set<String> drawnCards = new HashSet<>();
        
        // Draw all 52 cards
        for (int i = 0; i < 52; i++) {
            Card card = deck.drawCard();
            String cardString = card.toString();
            // Ensure no duplicate cards
            assertTrue(drawnCards.add(cardString), "Duplicate card found: " + cardString);
        }
        
        assertEquals(52, drawnCards.size());
        assertEquals(0, deck.remainingCards());
    }

    @Test
    void testAutoReshuffle() {
        // Draw all cards
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }
        assertEquals(0, deck.remainingCards());

        // Next draw should trigger reshuffle
        Card card = deck.drawCard();
        assertNotNull(card);
        assertEquals(51, deck.remainingCards());
    }

    @Test
    void testManualShuffle() {
        // Draw some cards to remember
        Card firstCard = deck.drawCard();
        Card secondCard = deck.drawCard();
        
        // Reset and shuffle
        deck.initialize();
        
        // Draw cards again - they should be different (most likely)
        Card newFirstCard = deck.drawCard();
        Card newSecondCard = deck.drawCard();
        
        // This test might occasionally fail due to random chance
        assertTrue(
            !firstCard.toString().equals(newFirstCard.toString()) ||
            !secondCard.toString().equals(newSecondCard.toString()),
            "Shuffle didn't change card order"
        );
    }
} 