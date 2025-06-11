package com.blackjack.service.impl;

import com.blackjack.model.Player;
import com.blackjack.repository.PlayerRepository;
import com.blackjack.service.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<Player> createPlayer(Player player) {
        log.info("Creating new player: {}", player);
        player.setCreatedAt(LocalDateTime.now());
        player.setUpdatedAt(LocalDateTime.now());
        player.setBalance(BigDecimal.valueOf(100)); // Starting balance
        return playerRepository.save(player)
            .doOnSuccess(savedPlayer -> log.info("Successfully created player: {}", savedPlayer))
            .doOnError(error -> log.error("Error creating player: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Player> getPlayerById(Long id) {
        log.info("Fetching player by ID: {}", id);
        return playerRepository.findById(id)
            .doOnSuccess(player -> log.info("Found player: {}", player))
            .doOnError(error -> log.error("Error fetching player by ID {}: {}", id, error.getMessage(), error))
            .switchIfEmpty(Mono.<Player>empty().doOnSubscribe(subscription -> {
                log.warn("Player not found with ID: {}", id);
            }));
    }

    @Override
    public Mono<Player> getPlayerByUsername(String username) {
        log.info("Fetching player by username: {}", username);
        return playerRepository.findByUsername(username)
            .doOnSuccess(player -> log.info("Found player: {}", player))
            .doOnError(error -> log.error("Error fetching player by username {}: {}", username, error.getMessage(), error))
            .switchIfEmpty(Mono.<Player>empty().doOnSubscribe(subscription -> {
                log.warn("Player not found with username: {}", username);
            }));
    }

    @Override
    public Mono<Player> updatePlayer(Long id, Player player) {
        log.info("Updating player {}: {}", id, player);
        return playerRepository.findById(id)
            .flatMap(existingPlayer -> {
                existingPlayer.setUsername(player.getUsername());
                existingPlayer.setEmail(player.getEmail());
                existingPlayer.setUpdatedAt(LocalDateTime.now());
                return playerRepository.save(existingPlayer);
            })
            .doOnSuccess(updatedPlayer -> log.info("Successfully updated player: {}", updatedPlayer))
            .doOnError(error -> log.error("Error updating player {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<Void> deletePlayer(Long id) {
        log.info("Deleting player: {}", id);
        return playerRepository.deleteById(id)
            .doOnSuccess(v -> log.info("Successfully deleted player: {}", id))
            .doOnError(error -> log.error("Error deleting player {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<Player> updateBalance(Long id, BigDecimal amount) {
        log.info("Updating balance for player {}: {}", id, amount);
        return playerRepository.findById(id)
            .flatMap(player -> {
                try {
                    player.updateBalance(amount);
                    return playerRepository.save(player);
                } catch (IllegalArgumentException e) {
                    log.error("Insufficient funds for player {}: {}", id, e.getMessage());
                    return Mono.error(new IllegalStateException("Insufficient funds"));
                }
            })
            .doOnSuccess(player -> log.info("Successfully updated balance for player {}: {}", id, player.getBalance()))
            .doOnError(error -> log.error("Error updating balance for player {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Mono<Player> updateStatistics(Long id, boolean won, BigDecimal amount) {
        log.info("Updating statistics for player {}: won={}, amount={}", id, won, amount);
        return playerRepository.findById(id)
            .flatMap(player -> {
                player.updateStatistics(won, amount);
                return playerRepository.save(player);
            })
            .doOnSuccess(player -> log.info("Successfully updated statistics for player: {}", player))
            .doOnError(error -> log.error("Error updating statistics for player {}: {}", id, error.getMessage(), error));
    }

    @Override
    public Flux<Player> getTopPlayers(int limit) {
        log.info("Fetching top {} players", limit);
        return playerRepository.findAll()
            .filter(player -> player.getGamesPlayed() >= 10)
            .sort((p1, p2) -> Double.compare(p2.getWinRate(), p1.getWinRate()))
            .take(limit)
            .doOnComplete(() -> log.info("Successfully fetched top players"))
            .doOnError(error -> log.error("Error fetching top players: {}", error.getMessage(), error));
    }

    @Override
    public Flux<Player> getPlayersWithBalanceAbove(BigDecimal threshold) {
        log.info("Fetching players with balance above: {}", threshold);
        return playerRepository.findPlayersByBalanceRange(threshold, new BigDecimal("999999999.99"))
            .doOnComplete(() -> log.info("Successfully fetched players with balance above {}", threshold))
            .doOnError(error -> log.error("Error fetching players with balance above {}: {}", threshold, error.getMessage(), error));
    }

    @Override
    public Flux<Player> getAllPlayers() {
        log.info("Fetching all players");
        return playerRepository.findAll()
            .doOnComplete(() -> log.info("Successfully fetched all players"))
            .doOnError(error -> log.error("Error fetching all players: {}", error.getMessage(), error));
    }

    @Override
    public Mono<Integer> resetDailyStatistics() {
        log.info("Resetting daily statistics for all players");
        return playerRepository.findAll()
            .flatMap(player -> {
                player.resetDailyStatistics();
                return playerRepository.save(player);
            })
            .count()
            .map(Long::intValue)
            .doOnSuccess(count -> log.info("Successfully reset daily statistics for {} players", count))
            .doOnError(error -> log.error("Error resetting daily statistics: {}", error.getMessage(), error));
    }
} 