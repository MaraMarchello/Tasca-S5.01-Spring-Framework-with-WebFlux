package com.blackjack.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "games")
@Schema(description = "Blackjack game entity")
public class Game {
    @Id
    @Schema(description = "Unique game identifier", example = "507f1f77bcf86cd799439011")
    private String id;
    
    @Field("player_id")
    @Schema(description = "ID of the player playing this game", example = "1")
    private Long playerId;
    
    @Field("player_hand")
    @Schema(description = "Player's hand of cards")
    private Hand playerHand = new Hand();
    
    @Field("dealer_hand")
    @Schema(description = "Dealer's hand of cards")
    private Hand dealerHand = new Hand();
    
    @Schema(description = "Bet amount for this game", example = "25.00")
    private BigDecimal bet;
    
    @Field("insurance_bet")
    @Schema(description = "Insurance bet amount", example = "12.50")
    private BigDecimal insuranceBet;
    
    @Schema(description = "Current status of the game", example = "IN_PROGRESS")
    private GameStatus status;
    
    @Field("start_time")
    @Schema(description = "When the game started", example = "2023-12-08T15:30:45")
    private LocalDateTime startTime;
    
    @Field("end_time")
    @Schema(description = "When the game ended", example = "2023-12-08T15:35:20")
    private LocalDateTime endTime;
    
    @Schema(description = "Final result of the game", example = "PLAYER_WIN")
    private GameResult result;
    
    @Schema(description = "List of actions taken during the game")
    private List<GameAction> actions = new ArrayList<>();

    public Game(Long playerId, BigDecimal bet) {
        this.playerId = playerId;
        this.bet = bet;
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.status = GameStatus.IN_PROGRESS;
        this.startTime = LocalDateTime.now();
        this.actions = new ArrayList<>();
    }

    @Schema(description = "Game status enumeration")
    public enum GameStatus {
        @Schema(description = "Game has been created but not started")
        @SuppressWarnings("unused") 
        CREATED,
        @Schema(description = "Game is currently in progress")
        IN_PROGRESS,
        @Schema(description = "Game has been completed")
        COMPLETED
    }

    @Schema(description = "Game result enumeration")
    public enum GameResult {
        @Schema(description = "Player wins the game")
        PLAYER_WIN, 
        @Schema(description = "Dealer wins the game")
        DEALER_WIN, 
        @Schema(description = "Game ends in a tie")
        @SuppressWarnings("unused") 
        PUSH, 
        @Schema(description = "Player gets blackjack")
        PLAYER_BLACKJACK, 
        @Schema(description = "Player busts (over 21)")
        @SuppressWarnings("unused") 
        PLAYER_BUST, 
        @Schema(description = "Dealer busts (over 21)")
        @SuppressWarnings("unused") 
        DEALER_BUST
    }

    @Schema(description = "Available game actions")
    public enum GameAction {
        @Schema(description = "Draw another card")
        HIT, 
        @Schema(description = "End turn with current hand")
        STAND, 
        @Schema(description = "Double the bet and take one more card")
        @SuppressWarnings("unused") 
        DOUBLE_DOWN, 
        @Schema(description = "Forfeit half the bet and end the game")
        @SuppressWarnings("unused") 
        SURRENDER
    }

    public void addAction(GameAction action) {
        this.actions.add(action);
    }

    public void completeGame(GameResult result) {
        this.status = GameStatus.COMPLETED;
        this.result = result;
        this.endTime = LocalDateTime.now();
    }

    public boolean isPlayerTurn() {
        return status == GameStatus.IN_PROGRESS && !playerHand.isBusted() && !playerHand.isBlackjack();
    }

    public boolean isDealerTurn() {
        return status == GameStatus.IN_PROGRESS && 
               !playerHand.isBusted() && 
               playerHand.getValue() <= 21 &&
               !actions.isEmpty() &&
               actions.getLast() == GameAction.STAND;
    }
} 