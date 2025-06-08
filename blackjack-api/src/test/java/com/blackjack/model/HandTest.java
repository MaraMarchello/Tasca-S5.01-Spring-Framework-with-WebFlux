package com.blackjack.model;

import com.blackjack.util.Rank;
import com.blackjack.util.Suit;
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
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        hand.addCard(new Card(Rank.KING, Suit.SPADES));
        
        assertEquals(21, hand.getValue());
        assertTrue(hand.isBlackjack());
        assertFalse(hand.isBusted());
    }

    @Test
    void testBust() {
        hand.addCard(new Card(Rank.KING, Suit.HEARTS));
        hand.addCard(new Card(Rank.QUEEN, Suit.SPADES));
        hand.addCard(new Card(Rank.JACK, Suit.DIAMONDS));
        
        assertEquals(30, hand.getValue());
        assertTrue(hand.isBusted());
        assertFalse(hand.isBlackjack());
    }

    @Test
    void testMultipleAces() {
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        assertEquals(11, hand.getValue());

        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        assertEquals(12, hand.getValue());  // One ace should be counted as 1

        hand.addCard(new Card(Rank.NINE, Suit.DIAMONDS));
        assertEquals(21, hand.getValue());  // Perfect hand with two aces
    }

    @Test
    void testToString() {
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        hand.addCard(new Card(Rank.KING, Suit.SPADES));
        
        assertEquals("♥A ♠K (21)", hand.toString());
    }
} 