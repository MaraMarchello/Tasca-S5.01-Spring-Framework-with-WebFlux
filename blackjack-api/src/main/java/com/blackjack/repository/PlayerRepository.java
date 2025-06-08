package com.blackjack.repository;

import com.blackjack.model.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

/**
 * Repository interface for managing Player entities in MySQL using R2DBC.
 * Provides operations for player data management.
 */
@Repository
public interface PlayerRepository extends R2dbcRepository<Player, Long> {

    Mono<Player> findByUsername(String username);
    Mono<Boolean> existsByUsername(String username);
    
    @Query("SELECT * FROM player WHERE balance >= :minBalance AND balance <= :maxBalance")
    Flux<Player> findPlayersByBalanceRange(BigDecimal minBalance, BigDecimal maxBalance);
}