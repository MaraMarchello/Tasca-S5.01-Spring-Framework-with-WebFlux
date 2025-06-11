package com.blackjack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new game")
public class CreateGameRequest {
    
    @NotNull(message = "Player ID is required")
    @Schema(description = "ID of the player starting the game", example = "1")
    private Long playerId;
    
    @NotNull(message = "Bet amount is required")
    @DecimalMin(value = "1.0", message = "Bet amount must be at least 1.00")
    @Schema(description = "Initial bet amount", example = "10.00")
    private BigDecimal bet;
} 