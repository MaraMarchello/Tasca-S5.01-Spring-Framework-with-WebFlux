package com.blackjack.service;

import com.blackjack.model.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

public interface PlayerService {
    /**
     * Create a new player
     * @param player player to create
     * @return created player
     */
    Mono<Player> createPlayer(Player player);

    /**
     * Get player by ID
     * @param id player ID
     * @return player if found
     */
    Mono<Player> getPlayerById(Long id);

    /**
     * Get player by username
     * @param username player username
     * @return player if found
     */
    Mono<Player> getPlayerByUsername(String username);

    /**
     * Update player information
     * @param id player ID
     * @param player updated player data
     * @return updated player
     */
    Mono<Player> updatePlayer(Long id, Player player);

    /**
     * Delete player by ID
     * @param id player ID
     * @return void
     */
    Mono<Void> deletePlayer(Long id);

    /**
     * Update player balance
     * @param id player ID
     * @param amount amount to add (positive) or subtract (negative)
     * @return updated player
     */
    Mono<Player> updateBalance(Long id, BigDecimal amount);

    /**
     * Update player statistics after a game
     * @param id player ID
     * @param won whether the player won
     * @param amount amount won or lost
     * @return updated player
     */
    Mono<Player> updateStatistics(Long id, boolean won, BigDecimal amount);

    /**
     * Get top players by win rate
     * @param limit number of players to return
     * @return list of top players
     */
    Flux<Player> getTopPlayers(int limit);

    /**
     * Get players with balance above threshold
     * @param threshold minimum balance
     * @return list of players
     */
    Flux<Player> getPlayersWithBalanceAbove(BigDecimal threshold);

    /**
     * Get all players
     * @return list of all players
     */
    Flux<Player> getAllPlayers();

    /**
     * Reset daily statistics for all players
     * @return number of players updated
     */
    Mono<Integer> resetDailyStatistics();
} 