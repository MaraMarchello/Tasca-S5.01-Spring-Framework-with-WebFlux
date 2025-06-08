package com.blackjack.model;

import com.blackjack.util.Rank;
import com.blackjack.util.Suit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardCreation() {
        Card card = new Card(Rank.ACE, Suit.HEARTS);
        assertEquals(Rank.ACE, card.getRank());
        assertEquals(Suit.HEARTS, card.getSuit());
        assertTrue(card.isFaceUp());
    }

    @Test
    void testCardValue() {
        Card aceCard = new Card(Rank.ACE, Suit.HEARTS);
        assertEquals(1, aceCard.getValue());

        Card kingCard = new Card(Rank.KING, Suit.SPADES);
        assertEquals(10, kingCard.getValue());

        Card fiveCard = new Card(Rank.FIVE, Suit.DIAMONDS);
        assertEquals(5, fiveCard.getValue());
    }

    @Test
    void testCardToString() {
        Card faceUpCard = new Card(Rank.ACE, Suit.HEARTS);
        assertEquals("♥A", faceUpCard.toString());

        Card faceDownCard = new Card(Rank.ACE, Suit.HEARTS, false);
        assertEquals("🂠", faceDownCard.toString());
    }
} 