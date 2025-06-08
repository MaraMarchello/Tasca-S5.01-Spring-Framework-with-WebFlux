package com.blackjack.service.impl;

import com.blackjack.model.Card;
import com.blackjack.service.DeckService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DeckServiceImpl implements DeckService {
    private static final int RESHUFFLE_THRESHOLD = 20; // Reshuffle when less than 20 cards remain
    private final AtomicReference<List<Card>> currentDeck = new AtomicReference<>(new ArrayList<>());

    @Override
    public Flux<Card> initializeDeck(int numberOfDecks) {
        return Mono.fromCallable(() -> {
            List<Card> newDeck = new ArrayList<>();
            for (int i = 0; i < numberOfDecks; i++) {
                for (Card.Suit suit : Card.Suit.values()) {
                    for (Card.Rank rank : Card.Rank.values()) {
                        newDeck.add(new Card(suit, rank));
                    }
                }
            }
            currentDeck.set(newDeck);
            return newDeck;
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Card> shuffle() {
        return Mono.fromCallable(() -> {
            List<Card> deck = new ArrayList<>(currentDeck.get());
            Collections.shuffle(deck);
            currentDeck.set(deck);
            return deck;
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Card> drawCard() {
        return Mono.fromCallable(() -> {
            List<Card> deck = currentDeck.get();
            if (deck.isEmpty()) {
                throw new IllegalStateException("No cards remaining in the deck");
            }
            Card card = deck.remove(0);
            currentDeck.set(deck);
            return card;
        });
    }

    @Override
    public Flux<Card> drawCards(int count) {
        return Mono.fromCallable(() -> {
            List<Card> deck = currentDeck.get();
            if (deck.size() < count) {
                throw new IllegalStateException("Not enough cards in the deck");
            }
            List<Card> drawnCards = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                drawnCards.add(deck.remove(0));
            }
            currentDeck.set(deck);
            return drawnCards;
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Integer> getRemainingCards() {
        return Mono.fromCallable(() -> currentDeck.get().size());
    }

    @Override
    public Mono<Boolean> needsReshuffle() {
        return getRemainingCards()
                .map(remaining -> remaining < RESHUFFLE_THRESHOLD);
    }
} 