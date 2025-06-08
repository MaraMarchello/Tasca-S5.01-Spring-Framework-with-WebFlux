package com.blackjack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Game summary response")
public class GameSummaryResponse {
    
    @Schema(description = "Game ID", example = "507f1f77bcf86cd799439011")
    private String gameId;
    
    @Schema(description = "Player ID", example = "1")
    private Long playerId;
    
    @Schema(description = "Bet amount", example = "25.00")
    private BigDecimal bet;
    
    @Schema(description = "Game status", example = "COMPLETED")
    private String status;
    
    @Schema(description = "Game result", example = "PLAYER_WIN")
    private String result;
    
    @Schema(description = "Player hand value", example = "20")
    private int playerHandValue;
    
    @Schema(description = "Dealer hand value", example = "18")
    private int dealerHandValue;
    
    @Schema(description = "Game start time")
    private LocalDateTime startTime;
    
    @Schema(description = "Game end time")
    private LocalDateTime endTime;
    
    @Schema(description = "Duration in seconds", example = "45")
    private Long durationSeconds;
} 