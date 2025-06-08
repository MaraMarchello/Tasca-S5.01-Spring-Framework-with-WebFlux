package com.blackjack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Player statistics response")
public class PlayerStatsResponse {
    
    @Schema(description = "Player ID", example = "1")
    private Long playerId;
    
    @Schema(description = "Player username", example = "johnsmith")
    private String username;
    
    @Schema(description = "Current balance", example = "150.75")
    private BigDecimal balance;
    
    @Schema(description = "Total games played", example = "45")
    private int gamesPlayed;
    
    @Schema(description = "Total games won", example = "23")
    private int gamesWon;
    
    @Schema(description = "Overall win rate", example = "0.51")
    private double winRate;
    
    @Schema(description = "Total winnings", example = "275.50")
    private BigDecimal totalWinnings;
    
    @Schema(description = "Games played today", example = "5")
    private int gamesPlayedToday;
    
    @Schema(description = "Games won today", example = "3")
    private int gamesWonToday;
    
    @Schema(description = "Daily win rate", example = "0.60")
    private double dailyWinRate;
} 