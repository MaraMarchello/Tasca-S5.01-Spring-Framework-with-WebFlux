package com.blackjack.exception;

public class PlayerNotFoundException extends RuntimeException {
    
    public PlayerNotFoundException(String message) {
        super(message);
    }
    
    public PlayerNotFoundException(Long playerId) {
        super("Player not found with ID: " + playerId);
    }
    
    public PlayerNotFoundException(String field, String value) {
        super("Player not found with " + field + ": " + value);
    }
} 