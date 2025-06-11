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

        // Adjust for aces - convert ace from 1 to 11 when beneficial
        while (aces > 0 && value + 10 <= 21) {
            value += 10;  // Convert ace from 1 to 11
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
        if (cards.stream().noneMatch(Card::isAce)) {
            return false;  // No aces, can't be soft
        }
        
        int hardValue = cards.stream().mapToInt(Card::getValue).sum();  // All aces as 1
        int softValue = getValue();  // With ace optimization
        
        // Hand is soft if we're using at least one ace as 11
        return softValue > hardValue && softValue <= 21;
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