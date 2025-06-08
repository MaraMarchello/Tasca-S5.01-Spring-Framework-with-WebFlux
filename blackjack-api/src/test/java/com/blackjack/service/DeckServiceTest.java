package com.blackjack.service;

import com.blackjack.model.Card;
import com.blackjack.service.impl.DeckServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeckServiceTest {

    private DeckService deckService;

    @BeforeEach
    void setUp() {
        deckService = new DeckServiceImpl();
        // Initialize a fresh deck before each test
        deckService.initializeDeck(1).blockLast();
    }

    @Test
    void initializeDeck_ShouldCreateCorrectNumberOfCards() {
        // Test with single deck
        StepVerifier.create(deckService.initializeDeck(1).collectList())
                .consumeNextWith(cards -> {
                    assertEquals(52, cards.size());
                    assertAllCardsUnique(cards);
                })
                .verifyComplete();

        // Test with multiple decks
        StepVerifier.create(deckService.initializeDeck(2).collectList())
                .consumeNextWith(cards -> {
                    assertEquals(104, cards.size());
                })
                .verifyComplete();
    }

    @Test
    void shuffle_ShouldChangeCardOrder() {
        // Get initial deck order
        List<Card> initialOrder = deckService.initializeDeck(1).collectList().block();
        assertNotNull(initialOrder);

        // Shuffle and get new order
        List<Card> shuffledOrder = deckService.shuffle().collectList().block();
        assertNotNull(shuffledOrder);

        // Verify orders are different (note: there's a tiny chance this could fail randomly)
        assertFalse(initialOrder.equals(shuffledOrder));
    }

    @Test
    void drawCard_ShouldReturnValidCard() {
        StepVerifier.create(deckService.drawCard())
                .consumeNextWith(card -> {
                    assertNotNull(card);
                    assertNotNull(card.getSuit());
                    assertNotNull(card.getRank());
                })
                .verifyComplete();
    }

    @Test
    void drawCards_ShouldReturnRequestedNumberOfCards() {
        int cardsToDraw = 5;
        StepVerifier.create(deckService.drawCards(cardsToDraw).collectList())
                .consumeNextWith(cards -> {
                    assertEquals(cardsToDraw, cards.size());
                    assertAllCardsUnique(cards);
                })
                .verifyComplete();
    }

    @Test
    void drawCards_ShouldThrowException_WhenNotEnoughCards() {
        // Draw all cards first
        deckService.drawCards(52).blockLast();

        // Try to draw one more card
        StepVerifier.create(deckService.drawCard())
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void getRemainingCards_ShouldReturnCorrectCount() {
        // Initial count should be 52
        StepVerifier.create(deckService.getRemainingCards())
                .expectNext(52)
                .verifyComplete();

        // Draw some cards and check remaining
        deckService.drawCards(10).blockLast();
        StepVerifier.create(deckService.getRemainingCards())
                .expectNext(42)
                .verifyComplete();
    }

    @Test
    void needsReshuffle_ShouldReturnTrue_WhenBelowThreshold() {
        // Draw cards until near threshold
        deckService.drawCards(35).blockLast(); // Leaves 17 cards (below threshold of 20)

        StepVerifier.create(deckService.needsReshuffle())
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void needsReshuffle_ShouldReturnFalse_WhenAboveThreshold() {
        // Draw just a few cards
        deckService.drawCards(5).blockLast(); // Leaves 47 cards (above threshold of 20)

        StepVerifier.create(deckService.needsReshuffle())
                .expectNext(false)
                .verifyComplete();
    }

    private void assertAllCardsUnique(List<Card> cards) {
        Set<String> uniqueCards = new HashSet<>();
        for (Card card : cards) {
            String cardKey = card.getSuit() + "-" + card.getRank();
            assertTrue(uniqueCards.add(cardKey), "Duplicate card found: " + cardKey);
        }
    }
}