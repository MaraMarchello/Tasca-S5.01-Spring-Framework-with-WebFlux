package com.blackjack.repository;

import com.blackjack.model.Game;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import java.util.List;

/**
 * Custom repository interface for complex Game queries in MongoDB.
 * Provides specialized reactive operations for game data analysis.
 */
@Validated
public interface CustomGameRepository {

    /**
     * Find games for a player within a date range
     * @param playerId the ID of the player
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return a Flux of games within the date range
     */
    Flux<Game> findGamesByPlayerIdAndDateRange(
            @NotNull Long playerId,
            @NotNull LocalDateTime startDate,
            @NotNull LocalDateTime endDate);

    /**
     * Find the last unfinished game for a player
     * @param playerId the ID of the player
     * @return a Mono with the last unfinished game, if any
     */
    Mono<Game> findLastUnfinishedGameByPlayerId(@NotNull Long playerId);

    /**
     * Find top winning games
     * @param limit maximum number of games to return
     * @return a Flux of top winning games
     */
    Flux<Game> findTopWinningGames(@Min(1) int limit);

    /**
     * Find games by player and result
     * @param playerId the ID of the player
     * @param result the game result to filter by
     * @return a Flux of games matching the criteria
     */
    Flux<Game> findGamesByPlayerIdAndResult(
            @NotNull Long playerId,
            @NotNull Game.GameResult result);

    /**
     * Find games with bet amount in range
     * @param minBet minimum bet amount
     * @param maxBet maximum bet amount
     * @param pageable pagination information
     * @return a Flux of games with bets in range
     */
    Flux<Game> findGamesByBetRange(
            @NotNull @Min(0) BigDecimal minBet,
            @NotNull BigDecimal maxBet,
            Pageable pageable);

    /**
     * Calculate total winnings for a player in a date range
     * @param playerId the ID of the player
     * @param startDate start of the date range
     * @param endDate end of the date range
     * @return total winnings as Mono<BigDecimal>
     */
    Mono<BigDecimal> calculateTotalWinnings(
            @NotNull Long playerId,
            @NotNull LocalDateTime startDate,
            @NotNull LocalDateTime endDate);

    /**
     * Find games with specific sequence of actions
     * @param actions sequence of game actions to match
     * @param limit maximum number of games to return
     * @return a Flux of games with matching action sequence
     */
    Flux<Game> findGamesByActionSequence(
            @NotNull List<Game.GameAction> actions,
            @Min(1) int limit);

    /**
     * Find high stake games (bet amount above threshold)
     * @param threshold minimum bet amount
     * @return a Flux of high stake games
     */
    Flux<Game> findHighStakeGames(BigDecimal threshold);
}