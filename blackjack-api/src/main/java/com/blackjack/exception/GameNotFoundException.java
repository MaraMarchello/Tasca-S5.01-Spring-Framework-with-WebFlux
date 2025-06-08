package com.blackjack.exception;

public class GameNotFoundException extends RuntimeException {
    
    public GameNotFoundException(String message) {
        super(message);
    }
    
    public static GameNotFoundException forGameId(String gameId) {
        return new GameNotFoundException("Game not found with ID: " + gameId);
    }
} 