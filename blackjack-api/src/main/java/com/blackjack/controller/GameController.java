package com.blackjack.controller;

import com.blackjack.dto.CreateGameRequest;
import com.blackjack.exception.GameNotFoundException;
import com.blackjack.exception.PlayerNotFoundException;
import com.blackjack.model.Game;
import com.blackjack.service.GameService;
import com.blackjack.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "Game Management", description = "Endpoints for managing blackjack games")
public class GameController {

    private final GameService gameService;
    private final PlayerService playerService;

    @Operation(summary = "Start a new game", description = "Creates and starts a new blackjack game for a player")
    @ApiResponse(responseCode = "201", description = "Game created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data or insufficient funds")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @PostMapping
    public Mono<ResponseEntity<Game>> createGame(@Valid @RequestBody CreateGameRequest request) {
        log.info("Creating new game for player {} with bet {}", request.getPlayerId(), request.getBet());
        
        return playerService.getPlayerById(request.getPlayerId())
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(request.getPlayerId())))
                .then(gameService.startGame(request.getPlayerId(), request.getBet()))
                .map(game -> ResponseEntity.status(HttpStatus.CREATED).body(game))
                .doOnSuccess(response -> log.info("Game created successfully: {}", 
                    Optional.ofNullable(response.getBody()).map(Game::getId).orElse("unknown")));
    }

    @Operation(summary = "Hit - Draw a card", description = "Player draws an additional card in the current game")
    @ApiResponse(responseCode = "200", description = "Card drawn successfully")
    @ApiResponse(responseCode = "400", description = "Invalid game state")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @PostMapping("/{gameId}/hit")
    public Mono<ResponseEntity<Game>> hit(
            @Parameter(description = "Game ID", example = "507f1f77bcf86cd799439011") 
            @PathVariable String gameId) {
        log.info("Player hitting in game: {}", gameId);
        
        return gameService.hit(gameId)
                .map(ResponseEntity::ok)
                .onErrorMap(IllegalStateException.class, ex -> new IllegalStateException("Cannot hit: " + ex.getMessage()))
                .doOnSuccess(response -> log.info("Hit completed for game: {}", gameId));
    }

    @Operation(summary = "Stand - End turn", description = "Player ends their turn and dealer plays")
    @ApiResponse(responseCode = "200", description = "Game completed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid game state")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @PostMapping("/{gameId}/stand")
    public Mono<ResponseEntity<Game>> stand(
            @Parameter(description = "Game ID", example = "507f1f77bcf86cd799439011") 
            @PathVariable String gameId) {
        log.info("Player standing in game: {}", gameId);
        
        return gameService.stand(gameId)
                .map(ResponseEntity::ok)
                .onErrorMap(IllegalStateException.class, ex -> new IllegalStateException("Cannot stand: " + ex.getMessage()))
                .doOnSuccess(response -> log.info("Stand completed for game: {}", gameId));
    }

    @Operation(summary = "Split hand", description = "Player splits their hand if they have a pair")
    @ApiResponse(responseCode = "200", description = "Hand split successfully")
    @ApiResponse(responseCode = "400", description = "Cannot split hand or insufficient funds")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @PostMapping("/{gameId}/split")
    public Mono<ResponseEntity<Game>> split(
            @Parameter(description = "Game ID", example = "507f1f77bcf86cd799439011") 
            @PathVariable String gameId) {
        log.info("Player splitting in game: {}", gameId);
        
        return gameService.split(gameId)
                .map(ResponseEntity::ok)
                .onErrorMap(IllegalStateException.class, ex -> new IllegalStateException("Cannot split: " + ex.getMessage()))
                .doOnSuccess(response -> log.info("Split completed for game: {}", gameId));
    }

    @Operation(summary = "Take insurance", description = "Player takes insurance against dealer blackjack")
    @ApiResponse(responseCode = "200", description = "Insurance taken successfully")
    @ApiResponse(responseCode = "400", description = "Cannot take insurance or insufficient funds")
    @ApiResponse(responseCode = "404", description = "Game not found")
    @PostMapping("/{gameId}/insurance")
    public Mono<ResponseEntity<Game>> insurance(
            @Parameter(description = "Game ID", example = "507f1f77bcf86cd799439011") 
            @PathVariable String gameId) {
        log.info("Player taking insurance in game: {}", gameId);
        
        return gameService.insurance(gameId)
                .map(ResponseEntity::ok)
                .onErrorMap(IllegalStateException.class, ex -> new IllegalStateException("Cannot take insurance: " + ex.getMessage()))
                .doOnSuccess(response -> log.info("Insurance taken for game: {}", gameId));
    }

    @Operation(summary = "Get game details", description = "Retrieves detailed information about a specific game")
    @ApiResponse(responseCode = "200", description = "Game found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Game.class)))
    @ApiResponse(responseCode = "404", description = "Game not found")
    @GetMapping("/{gameId}")
    public Mono<ResponseEntity<Game>> getGame(
            @Parameter(description = "Game ID", example = "507f1f77bcf86cd799439011") 
            @PathVariable String gameId) {
        log.info("Getting game details for: {}", gameId);
        
        return gameService.getGameById(gameId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(GameNotFoundException.forGameId(gameId.toString())));
    }

    @Operation(summary = "Get player's active games", description = "Retrieves all active games for a specific player")
    @ApiResponse(responseCode = "200", description = "Active games retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @GetMapping("/player/{playerId}/active")
    public Flux<Game> getActiveGames(
            @Parameter(description = "Player ID", example = "1") 
            @PathVariable Long playerId) {
        log.info("Getting active games for player: {}", playerId);
        
        return playerService.getPlayerById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .thenMany(gameService.getActiveGames(playerId));
    }

    @Operation(summary = "Get player's game history", description = "Retrieves completed games for a player within date range")
    @ApiResponse(responseCode = "200", description = "Game history retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @GetMapping("/player/{playerId}/history")
    public Flux<Game> getGameHistory(
            @Parameter(description = "Player ID", example = "1") 
            @PathVariable Long playerId,
            @Parameter(description = "Start date", example = "2023-12-01T00:00:00")
            @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date", example = "2023-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime endDate) {
        
        // Default to last 30 days if no dates provided
        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();
        
        log.info("Getting game history for player {} from {} to {}", playerId, start, end);
        
        return playerService.getPlayerById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .thenMany(gameService.getCompletedGames(playerId, start, end));
    }

    @Operation(summary = "Get all player's games", description = "Retrieves all games (active and completed) for a player")
    @ApiResponse(responseCode = "200", description = "All games retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @GetMapping("/player/{playerId}")
    public Flux<Game> getAllPlayerGames(
            @Parameter(description = "Player ID", example = "1") 
            @PathVariable Long playerId) {
        log.info("Getting all games for player: {}", playerId);
        
        return playerService.getPlayerById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(playerId)))
                .thenMany(
                    Flux.merge(
                        gameService.getActiveGames(playerId),
                        gameService.getCompletedGames(playerId, LocalDateTime.now().minusYears(1), LocalDateTime.now())
                    )
                );
    }

    @Operation(summary = "Get high stake games", description = "Retrieves games with bets above the specified threshold")
    @ApiResponse(responseCode = "200", description = "High stake games retrieved successfully")
    @GetMapping("/high-stakes")
    public Flux<Game> getHighStakeGames(
            @Parameter(description = "Minimum bet threshold", example = "100.00")
            @RequestParam(defaultValue = "100.0") BigDecimal threshold) {
        log.info("Getting high stake games with threshold: {}", threshold);
        
        return gameService.getHighStakeGames(threshold);
    }

    @Operation(summary = "Clean up old games", description = "Removes completed games older than specified date")
    @ApiResponse(responseCode = "200", description = "Games cleaned up successfully")
    @DeleteMapping("/cleanup")
    public Mono<ResponseEntity<String>> cleanupOldGames(
            @Parameter(description = "Delete games older than this date", example = "2023-01-01T00:00:00")
            @RequestParam LocalDateTime olderThan) {
        log.info("Cleaning up games older than: {}", olderThan);
        
        return gameService.cleanupOldGames(olderThan)
                .map(count -> ResponseEntity.ok("Deleted " + count + " old games"));
    }
} 