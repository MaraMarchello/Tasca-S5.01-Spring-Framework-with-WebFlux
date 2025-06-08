package com.blackjack.model;

import com.blackjack.model.Card.Rank;
import com.blackjack.model.Card.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    private Hand hand;

    @BeforeEach
    void setUp() {
        hand = new Hand();
    }

    @Test
    void testEmptyHand() {
        assertEquals(0, hand.getValue());
        assertFalse(hand.isBusted());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void testBlackjack() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        assertEquals(21, hand.getValue());
        assertTrue(hand.isBlackjack());
        assertFalse(hand.isBusted());
    }

    @Test
    void testBust() {
        hand.addCard(new Card(Suit.HEARTS, Rank.KING));
        hand.addCard(new Card(Suit.SPADES, Rank.QUEEN));
        hand.addCard(new Card(Suit.DIAMONDS, Rank.JACK));
        
        assertEquals(30, hand.getValue());
        assertTrue(hand.isBusted());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void testMultipleAces() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        assertEquals(11, hand.getValue());

        hand.addCard(new Card(Suit.SPADES, Rank.ACE));
        assertEquals(12, hand.getValue());  // One ace should be counted as 1

        hand.addCard(new Card(Suit.DIAMONDS, Rank.NINE));
        assertEquals(21, hand.getValue());  // Perfect hand with two aces
    }

    @Test
    void testToString() {
        hand.addCard(new Card(Suit.HEARTS, Rank.ACE));
        hand.addCard(new Card(Suit.SPADES, Rank.KING));
        
        assertEquals("♥A ♠K (21)", hand.toString());
    }
} 