package com.blackjack.model;

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
public class Game {
    @Id
    private String id;
    
    @Field("player_id")
    private Long playerId;
    
    @Field("player_hand")
    private Hand playerHand = new Hand();
    
    @Field("dealer_hand")
    private Hand dealerHand = new Hand();
    
    private BigDecimal bet;
    
    @Field("insurance_bet")
    private BigDecimal insuranceBet;
    
    private GameStatus status;
    
    @Field("start_time")
    private LocalDateTime startTime;
    
    @Field("end_time")
    private LocalDateTime endTime;
    
    private GameResult result;
    
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

    public enum GameStatus {
        @SuppressWarnings("unused") // Reserved for future use
        CREATED,
        IN_PROGRESS,
        COMPLETED
    }

    public enum GameResult {
        PLAYER_WIN, 
        DEALER_WIN, 
        @SuppressWarnings("unused") // Reserved for future use
        PUSH, 
        PLAYER_BLACKJACK, 
        @SuppressWarnings("unused") // Reserved for future use
        PLAYER_BUST, 
        @SuppressWarnings("unused") // Reserved for future use
        DEALER_BUST
    }

    public enum GameAction {
        HIT, 
        STAND, 
        @SuppressWarnings("unused") // Reserved for future use
        DOUBLE_DOWN, 
        @SuppressWarnings("unused") // Reserved for future use
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