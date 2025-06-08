package com.blackjack.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Hand {
    private List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getValue() {
        int value = 0;
        int aces = 0;

        for (Card card : cards) {
            if (card.isAce()) {
                aces++;
            }
            value += card.getValue();
        }

        // Adjust for aces
        while (value > 21 && aces > 0) {
            value -= 10;
            aces--;
        }

        return value;
    }

    public boolean isBusted() {
        return getValue() > 21;
    }

    public boolean isBlackjack() {
        return cards.size() == 2 && getValue() == 21;
    }

    public boolean isSoft() {
        int value = 0;
        int aces = 0;

        for (Card card : cards) {
            if (card.isAce()) {
                aces++;
            }
            value += card.getValue();
        }

        // If we have an ace and reducing its value by 10 still gives us a valid hand
        return aces > 0 && (value - 10) <= 21;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card.toString()).append(" ");
        }
        return sb.toString().trim() + " (" + getValue() + ")";
    }
} 