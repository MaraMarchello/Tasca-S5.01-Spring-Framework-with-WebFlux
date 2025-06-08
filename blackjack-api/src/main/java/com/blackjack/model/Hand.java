package com.blackjack.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Hand {
    private List<Card> cards = new ArrayList<>();
    private boolean isBusted = false;
    private boolean isBlackjack = false;

    public void addCard(Card card) {
        cards.add(card);
        updateStatus();
    }

    public int getValue() {
        int value = 0;
        int aces = 0;

        for (Card card : cards) {
            if (card.getRank() == com.blackjack.util.Rank.ACE) {
                aces++;
            }
            value += card.getValue();
        }

        // Handle aces
        while (value <= 11 && aces > 0) {
            value += 10;
            aces--;
        }

        return value;
    }

    private void updateStatus() {
        int value = getValue();
        isBusted = value > 21;
        isBlackjack = value == 21 && cards.size() == 2;
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