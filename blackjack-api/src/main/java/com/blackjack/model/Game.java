package com.blackjack.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
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
    private Long playerId;
    private Hand playerHand;
    private Hand dealerHand;
    private BigDecimal bet;
    private BigDecimal insuranceBet;
    private GameStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private GameResult result;
    private List<GameAction> actions;

    public Game(Long playerId, BigDecimal bet) {
        this.playerId = playerId;
        this.bet = bet;
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.status = GameStatus.CREATED;
        this.startTime = LocalDateTime.now();
        this.actions = new ArrayList<>();
    }

    public enum GameStatus {
        CREATED,
        IN_PROGRESS,
        COMPLETED
    }

    public enum GameResult {
        PLAYER_WIN, DEALER_WIN, PUSH, PLAYER_BLACKJACK, PLAYER_BUST, DEALER_BUST
    }

    public enum GameAction {
        HIT, STAND, DOUBLE_DOWN, SURRENDER
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
               actions.get(actions.size() - 1) == GameAction.STAND;
    }
} 