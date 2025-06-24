package com.blackjack.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A playing card")
public class Card {
    @Schema(description = "Card suit", example = "HEARTS")
    private Suit suit;
    
    @Schema(description = "Card rank", example = "KING")
    private Rank rank;
    
    @Schema(description = "Whether the card is face up", example = "true")
    private boolean faceUp = true;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    @Schema(description = "Card suit enumeration")
    public enum Suit {
        @Schema(description = "Hearts suit")
        HEARTS("♥"),
        @Schema(description = "Diamonds suit")
        DIAMONDS("♦"),
        @Schema(description = "Clubs suit")
        CLUBS("♣"),
        @Schema(description = "Spades suit")
        SPADES("♠");

        @Getter
        private final String symbol;

        Suit(String symbol) {
            this.symbol = symbol;
        }
    }

    @Schema(description = "Card rank enumeration")
    public enum Rank {
        @Schema(description = "Ace (value 1 or 11)")
        ACE(1, "A"),
        @Schema(description = "Two")
        TWO(2, "2"),
        @Schema(description = "Three")
        THREE(3, "3"),
        @Schema(description = "Four")
        FOUR(4, "4"),
        @Schema(description = "Five")
        FIVE(5, "5"),
        @Schema(description = "Six")
        SIX(6, "6"),
        @Schema(description = "Seven")
        SEVEN(7, "7"),
        @Schema(description = "Eight")
        EIGHT(8, "8"),
        @Schema(description = "Nine")
        NINE(9, "9"),
        @Schema(description = "Ten")
        TEN(10, "10"),
        @Schema(description = "Jack (value 10)")
        JACK(10, "J"),
        @Schema(description = "Queen (value 10)")
        QUEEN(10, "Q"),
        @Schema(description = "King (value 10)")
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

    @Schema(description = "Numeric value of the card", example = "10")
    public int getValue() {
        return rank.getValue();
    }

    @Schema(description = "Whether the card is an ace", example = "false")
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