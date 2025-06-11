package com.blackjack.service.impl;

import com.blackjack.model.Card;
import com.blackjack.service.DeckService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DeckServiceImpl implements DeckService {
    private static final int RESHUFFLE_THRESHOLD = 20; // Reshuffle when less than 20 cards remain
    private final AtomicReference<Deque<Card>> currentDeck = new AtomicReference<>(new ArrayDeque<>());

    @Override
    public Flux<Card> initializeDeck(int numberOfDecks) {
        return Mono.fromCallable(() -> {
            ArrayList<Card> tempDeck = new ArrayList<>();
            for (int i = 0; i < numberOfDecks; i++) {
                for (Card.Suit suit : Card.Suit.values()) {
                    for (Card.Rank rank : Card.Rank.values()) {
                        tempDeck.add(new Card(suit, rank));
                    }
                }
            }
            Collections.shuffle(tempDeck);
            ArrayDeque<Card> newDeck = new ArrayDeque<>(tempDeck);
            currentDeck.set(newDeck);
            return tempDeck;
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<Card> shuffle() {
        return Mono.fromCallable(() -> {
            ArrayList<Card> tempDeck = new ArrayList<>(currentDeck.get());
            Collections.shuffle(tempDeck);
            ArrayDeque<Card> shuffledDeck = new ArrayDeque<>(tempDeck);
            currentDeck.set(shuffledDeck);
            return tempDeck;
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Card> drawCard() {
        return Mono.fromCallable(() -> {
            Deque<Card> deck = currentDeck.get();
            if (deck.isEmpty()) {
                throw new IllegalStateException("No cards remaining in the deck");
            }
            Card card = deck.removeFirst();
            currentDeck.set(deck);
            return card;
        });
    }

    @Override
    public Flux<Card> drawCards(int count) {
        return Mono.fromCallable(() -> {
            Deque<Card> deck = currentDeck.get();
            if (deck.size() < count) {
                throw new IllegalStateException("Not enough cards in the deck");
            }
            ArrayList<Card> drawnCards = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                drawnCards.add(deck.removeFirst());
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