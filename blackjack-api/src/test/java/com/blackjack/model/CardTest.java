package com.blackjack.model;

import com.blackjack.model.Card.Rank;
import com.blackjack.model.Card.Suit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardCreation() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(Rank.ACE, card.getRank());
        assertEquals(Suit.HEARTS, card.getSuit());
        assertTrue(card.isFaceUp());
    }

    @Test
    void testCardValue() {
        Card aceCard = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(1, aceCard.getValue());

        Card kingCard = new Card(Suit.SPADES, Rank.KING);
        assertEquals(10, kingCard.getValue());

        Card fiveCard = new Card(Suit.DIAMONDS, Rank.FIVE);
        assertEquals(5, fiveCard.getValue());
    }

    @Test
    void testCardToString() {
        Card faceUpCard = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals("♥A", faceUpCard.toString());

        Card faceDownCard = new Card(Suit.HEARTS, Rank.ACE, false);
        assertEquals("🂠", faceDownCard.toString());
    }
} 