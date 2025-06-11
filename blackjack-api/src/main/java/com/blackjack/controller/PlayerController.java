package com.blackjack.controller;

import com.blackjack.dto.CreatePlayerRequest;
import com.blackjack.dto.UpdatePlayerRequest;
import com.blackjack.dto.PlayerStatsResponse;
import com.blackjack.exception.PlayerNotFoundException;
import com.blackjack.model.Player;
import com.blackjack.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Tag(name = "Player Management", description = "Endpoints for managing players")
public class PlayerController {

    private final PlayerService playerService;

    @Operation(summary = "Create a new player", description = "Creates a new player with username and email")
    @ApiResponse(responseCode = "201", description = "Player created successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Player.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "409", description = "Player with username/email already exists")
    @PostMapping
    public Mono<ResponseEntity<Player>> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        log.info("Creating new player with username: {}", request.getUsername());
        
        Player player = new Player(request.getUsername(), request.getEmail());
        player.setBalance(BigDecimal.valueOf(100)); // Starting balance
        
        return playerService.createPlayer(player)
                .map(createdPlayer -> ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer))
                .doOnSuccess(response -> log.info("Player created successfully: {}", 
                    Optional.ofNullable(response.getBody()).map(Player::getId).orElse(-1L)));
    }

    @Operation(summary = "Get player by ID", description = "Retrieves a player by their unique ID")
    @ApiResponse(responseCode = "200", description = "Player found",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Player.class)))
    @ApiResponse(responseCode = "404", description = "Player not found")
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Player>> getPlayerById(
            @Parameter(description = "Player ID", example = "1") 
            @PathVariable Long id) {
        log.info("Getting player by ID: {}", id);
        
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(id)));
    }

    @Operation(summary = "Get player by username", description = "Retrieves a player by their username")
    @ApiResponse(responseCode = "200", description = "Player found")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @GetMapping("/username/{username}")
    public Mono<ResponseEntity<Player>> getPlayerByUsername(
            @Parameter(description = "Player username", example = "johnsmith") 
            @PathVariable String username) {
        log.info("Getting player by username: {}", username);
        
        return playerService.getPlayerByUsername(username)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("username", username)));
    }

    @Operation(summary = "Update player information", description = "Updates player's username and/or email")
    @ApiResponse(responseCode = "200", description = "Player updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Player>> updatePlayer(
            @Parameter(description = "Player ID", example = "1") 
            @PathVariable Long id,
            @Valid @RequestBody UpdatePlayerRequest request) {
        log.info("Updating player {}: {}", id, request);
        
        Player updateData = new Player();
        updateData.setUsername(request.getUsername());
        updateData.setEmail(request.getEmail());
        
        return playerService.updatePlayer(id, updateData)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(id)));
    }

    @Operation(summary = "Delete player", description = "Deletes a player by their ID")
    @ApiResponse(responseCode = "204", description = "Player deleted successfully")
    @ApiResponse(responseCode = "404", description = "Player not found")
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePlayer(
            @Parameter(description = "Player ID", example = "1") 
            @PathVariable Long id) {
        log.info("Deleting player: {}", id);
        
        return playerService.getPlayerById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(id)))
                .then(playerService.deletePlayer(id))
                .then(Mono.fromCallable(() -> ResponseEntity.noContent().<Void>build()));
    }

    @Operation(summary = "Get all players", description = "Retrieves all players (paginated in future)")
    @ApiResponse(responseCode = "200", description = "Players retrieved successfully")
    @GetMapping
    public Flux<Player> getAllPlayers() {
        log.info("Getting all players");
        
        return playerService.getAllPlayers();
    }

    @Operation(summary = "Get top players by win rate", description = "Retrieves top players ranked by win rate")
    @ApiResponse(responseCode = "200", description = "Top players retrieved successfully")
    @GetMapping("/top")
    public Flux<Player> getTopPlayers(
            @Parameter(description = "Number of players to return", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) int limit) {
        log.info("Getting top {} players", limit);
        
        return playerService.getTopPlayers(limit);
    }

    @Operation(summary = "Get wealthy players", description = "Retrieves players with balance above threshold")
    @ApiResponse(responseCode = "200", description = "Wealthy players retrieved successfully")
    @GetMapping("/wealthy")
    public Flux<Player> getWealthyPlayers(
            @Parameter(description = "Minimum balance threshold", example = "100.00")
            @RequestParam(defaultValue = "100.0") BigDecimal threshold) {
        log.info("Getting players with balance above: {}", threshold);
        
        return playerService.getPlayersWithBalanceAbove(threshold);
    }

    @Operation(summary = "Get player statistics", description = "Retrieves detailed statistics for a player")
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlayerStatsResponse.class)))
    @ApiResponse(responseCode = "404", description = "Player not found")
    @GetMapping("/{id}/stats")
    public Mono<ResponseEntity<PlayerStatsResponse>> getPlayerStats(
            @Parameter(description = "Player ID", example = "1") 
            @PathVariable Long id) {
        log.info("Getting statistics for player: {}", id);
        
        return playerService.getPlayerById(id)
                .map(this::mapToStatsResponse)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException(id)));
    }

    @Operation(summary = "Reset daily statistics", description = "Resets daily statistics for all players")
    @ApiResponse(responseCode = "200", description = "Daily statistics reset successfully")
    @PostMapping("/reset-daily-stats")
    public Mono<ResponseEntity<String>> resetDailyStats() {
        log.info("Resetting daily statistics for all players");
        
        return playerService.resetDailyStatistics()
                .map(count -> ResponseEntity.ok("Reset daily statistics for " + count + " players"));
    }

    private PlayerStatsResponse mapToStatsResponse(Player player) {
        return new PlayerStatsResponse(
                player.getId(),
                player.getUsername(),
                player.getBalance(),
                player.getGamesPlayed(),
                player.getGamesWon(),
                player.getWinRate(),
                player.getTotalWinnings(),
                player.getGamesPlayedToday(),
                player.getGamesWonToday(),
                player.getDailyWinRate()
        );
    }
} 