package com.blackjack.model;

import com.blackjack.util.Rank;
import com.blackjack.util.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private Rank rank;
    private Suit suit;
    private boolean faceUp = true;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getValue() {
        return rank.getValue();
    }

    @Override
    public String toString() {
        if (!faceUp) {
            return "🂠";  // Card back symbol
        }
        return suit.getSymbol() + rank.getSymbol();
    }
} 