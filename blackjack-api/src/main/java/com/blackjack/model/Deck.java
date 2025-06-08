package com.blackjack.model;

import com.blackjack.model.Card.Rank;
import com.blackjack.model.Card.Suit;
import lombok.Data;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Deck {
    private List<Card> cards = new ArrayList<>();

    public Deck() {
        initialize();
    }

    public void initialize() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            initialize();
        }
        return cards.remove(cards.size() - 1);
    }

    public int remainingCards() {
        return cards.size();
    }
} 