package com.blackjack.service.impl;

import com.blackjack.model.Player;
import com.blackjack.repository.PlayerRepository;
import com.blackjack.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<Player> createPlayer(Player player) {
        player.setCreatedAt(LocalDateTime.now());
        player.setUpdatedAt(LocalDateTime.now());
        return playerRepository.save(player);
    }

    @Override
    public Mono<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    @Override
    public Mono<Player> getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    @Override
    public Mono<Player> updatePlayer(Long id, Player player) {
        return playerRepository.findById(id)
                .flatMap(existingPlayer -> {
                    existingPlayer.setUsername(player.getUsername());
                    existingPlayer.setEmail(player.getEmail());
                    existingPlayer.setUpdatedAt(LocalDateTime.now());
                    return playerRepository.save(existingPlayer);
                });
    }

    @Override
    public Mono<Void> deletePlayer(Long id) {
        return playerRepository.deleteById(id);
    }

    @Override
    public Mono<Player> updateBalance(Long id, BigDecimal amount) {
        return playerRepository.findById(id)
                .flatMap(player -> {
                    try {
                        player.updateBalance(amount);
                        return playerRepository.save(player);
                    } catch (IllegalArgumentException e) {
                        return Mono.error(new IllegalStateException("Insufficient funds"));
                    }
                });
    }

    @Override
    public Mono<Player> updateStatistics(Long id, boolean won, BigDecimal amount) {
        return playerRepository.findById(id)
                .flatMap(player -> {
                    player.updateStatistics(won, amount);
                    return playerRepository.save(player);
                });
    }

    @Override
    public Flux<Player> getTopPlayers(int limit) {
        // For now, return all players and limit in service layer
        // Later we can add custom queries for this
        return playerRepository.findAll()
                .filter(player -> player.getGamesPlayed() >= 10)
                .sort((p1, p2) -> Double.compare(p2.getWinRate(), p1.getWinRate()))
                .take(limit);
    }

    @Override
    public Flux<Player> getPlayersWithBalanceAbove(BigDecimal threshold) {
        return playerRepository.findPlayersByBalanceRange(threshold, new BigDecimal("999999999.99"));
    }

    @Override
    public Flux<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Mono<Integer> resetDailyStatistics() {
        return playerRepository.findAll()
                .flatMap(player -> {
                    player.resetDailyStatistics();
                    return playerRepository.save(player);
                })
                .count()
                .map(Long::intValue);
    }
} 