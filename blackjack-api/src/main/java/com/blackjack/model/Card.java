package com.blackjack.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private Suit suit;
    private Rank rank;
    private boolean faceUp = true;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public enum Suit {
        HEARTS("♥"),
        DIAMONDS("♦"),
        CLUBS("♣"),
        SPADES("♠");

        @Getter
        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }
    }

    public enum Rank {
        ACE(1, "A"),
        TWO(2, "2"),
        THREE(3, "3"),
        FOUR(4, "4"),
        FIVE(5, "5"),
        SIX(6, "6"),
        SEVEN(7, "7"),
        EIGHT(8, "8"),
        NINE(9, "9"),
        TEN(10, "10"),
        JACK(10, "J"),
        QUEEN(10, "Q"),
        KING(10, "K");

        @Getter
        private final int value;
        @Getter
        private final String symbol;

        Rank(int value, String symbol) {
            this.value = value;
            this.symbol = symbol;
        }
    }

    public int getValue() {
        return rank.getValue();
    }

    public boolean isAce() {
        return rank == Rank.ACE;
    }

    public void flip() {
        this.faceUp = !this.faceUp;
    }

    @Override
    public String toString() {
        if (!faceUp) {
            return "🂠";  // Card back symbol
        }
        return suit.getSymbol() + rank.getSymbol();
    }
} 