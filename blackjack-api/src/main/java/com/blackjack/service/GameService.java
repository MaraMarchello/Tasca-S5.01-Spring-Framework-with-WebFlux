package com.blackjack.service;

import com.blackjack.model.Game;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface GameService {
    /**
     * Start a new game for a player
     * @param playerId player ID
     * @param bet initial bet amount
     * @return the created game
     */
    Mono<Game> startGame(Long playerId, BigDecimal bet);

    /**
     * Player hits (draws a card)
     * @param gameId game ID
     * @return updated game state
     */
    Mono<Game> hit(String gameId);

    /**
     * Player stands (ends their turn)
     * @param gameId game ID
     * @return final game state
     */
    Mono<Game> stand(String gameId);

    /**
     * Player splits their hand
     * @param gameId game ID
     * @return updated game state
     */
    Mono<Game> split(String gameId);

    /**
     * Player takes insurance
     * @param gameId game ID
     * @return updated game state
     */
    Mono<Game> insurance(String gameId);

    /**
     * Get game by ID
     * @param gameId game ID
     * @return game if found
     */
    Mono<Game> getGameById(String gameId);

    /**
     * Get active games for a player
     * @param playerId player ID
     * @return list of active games
     */
    Flux<Game> getActiveGames(Long playerId);

    /**
     * Get completed games for a player within a date range
     * @param playerId player ID
     * @param startDate start date
     * @param endDate end date
     * @return list of completed games
     */
    Flux<Game> getCompletedGames(Long playerId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get high stake games (games with bets above threshold)
     * @param threshold minimum bet amount
     * @return list of high stake games
     */
    Flux<Game> getHighStakeGames(BigDecimal threshold);

    /**
     * Clean up old completed games
     * @param olderThan date threshold
     * @return number of games deleted
     */
    Mono<Long> cleanupOldGames(LocalDateTime olderThan);
} 