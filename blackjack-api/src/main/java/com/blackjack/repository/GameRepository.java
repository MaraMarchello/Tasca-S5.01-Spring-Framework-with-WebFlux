package com.blackjack.repository;

import com.blackjack.model.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * Repository interface for managing Game entities in MongoDB.
 * Provides reactive operations for game data management.
 */
@Repository
@Validated
public interface GameRepository extends ReactiveMongoRepository<Game, String>, CustomGameRepository {

    /**
     * Find all games for a specific player
     * @param playerId the ID of the player
     * @return a Flux of games
     */
    Flux<Game> findByPlayerId(@NotNull Long playerId);

    /**
     * Find games for a player with specific status
     * @param playerId the ID of the player
     * @param status the status of the games to find
     * @return a Flux of games
     */
    Flux<Game> findByPlayerIdAndStatus(@NotNull Long playerId, @NotNull Game.GameStatus status);

    /**
     * Find games for a player with pagination
     * @param playerId the ID of the player
     * @param pageable pagination information
     * @return a Flux of games
     */
    Flux<Game> findByPlayerId(@NotNull Long playerId, Pageable pageable);

    /**
     * Count active games for a player
     * @param playerId the ID of the player
     * @return number of active games
     */
    @Query(value = "{ 'playerId': ?0, 'status': 'IN_PROGRESS' }", count = true)
    Mono<Long> countActiveGamesByPlayerId(@NotNull Long playerId);

    /**
     * Delete completed games older than specified date
     * @param date the cutoff date
     * @return number of games deleted
     */
    @Query(value = "{ 'status': 'COMPLETED', 'endTime': { $lt: ?0 } }", delete = true)
    Mono<Long> deleteCompletedGamesOlderThan(java.time.LocalDateTime date);
}