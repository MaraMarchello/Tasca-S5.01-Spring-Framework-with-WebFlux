package com.blackjack.service;

import com.blackjack.model.Card;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DeckService {
    /**
     * Initialize a new deck or multiple decks
     * @param numberOfDecks number of decks to use
     * @return Flux of cards in the deck(s)
     */
    Flux<Card> initializeDeck(int numberOfDecks);

    /**
     * Shuffle the current deck
     * @return Flux of shuffled cards
     */
    Flux<Card> shuffle();

    /**
     * Draw a single card from the deck
     * @return Mono of the drawn card
     */
    Mono<Card> drawCard();

    /**
     * Draw multiple cards from the deck
     * @param count number of cards to draw
     * @return Flux of drawn cards
     */
    Flux<Card> drawCards(int count);

    /**
     * Get remaining cards count
     * @return Mono of remaining cards count
     */
    Mono<Integer> getRemainingCards();

    /**
     * Check if deck needs to be reshuffled (e.g., when below certain threshold)
     * @return Mono<Boolean> true if reshuffle is needed
     */
    Mono<Boolean> needsReshuffle();
} 