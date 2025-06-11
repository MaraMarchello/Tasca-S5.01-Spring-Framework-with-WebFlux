package com.blackjack.repository;

import com.blackjack.R2dbcTestConfiguration;
import com.blackjack.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@DataR2dbcTest
@Import({R2dbcTestConfiguration.class})
@ActiveProfiles("test")
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        // Clean setup for R2DBC reactive tests
        testPlayer = new Player("testUser", "test@example.com");
        testPlayer.setBalance(BigDecimal.valueOf(1000));
        testPlayer.setCreatedAt(LocalDateTime.now());
        testPlayer.setUpdatedAt(LocalDateTime.now());
        
        // Clean the database before each test
        playerRepository.deleteAll().block();
    }

    @Test
    void findByUsername_ShouldReturnPlayer() {
        // Given
        Mono<Player> savedPlayer = playerRepository.save(testPlayer);
        
        // When & Then
        StepVerifier.create(savedPlayer)
                .expectNextMatches(player -> player.getUsername().equals("testUser"))
                .verifyComplete();
        
        StepVerifier.create(playerRepository.findByUsername("testUser"))
                .expectNextMatches(player -> 
                    player.getUsername().equals("testUser") && 
                    player.getEmail().equals("test@example.com"))
                .verifyComplete();
    }

    @Test
    void findByUsername_ShouldReturnEmptyForNonExistentUser() {
        // When & Then
        StepVerifier.create(playerRepository.findByUsername("nonexistent"))
                .verifyComplete();
    }

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // Given
        Mono<Player> saveOperation = playerRepository.save(testPlayer);
        
        // When & Then
        StepVerifier.create(saveOperation)
                .expectNextCount(1)
                .verifyComplete();
        
        StepVerifier.create(playerRepository.existsByUsername("testUser"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        // When & Then
        StepVerifier.create(playerRepository.existsByUsername("nonexistent"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findPlayersByBalanceRange_ShouldReturnPlayersInRange() {
        // Given
        Player player1 = new Player("player1", "player1@example.com");
        player1.setBalance(BigDecimal.valueOf(500));
        
        Player player2 = new Player("player2", "player2@example.com");
        player2.setBalance(BigDecimal.valueOf(1500));
        
        Player player3 = new Player("player3", "player3@example.com");
        player3.setBalance(BigDecimal.valueOf(2500));

        Flux<Player> saveOperations = Flux.just(player1, player2, player3)
                .flatMap(playerRepository::save);
        
        // When & Then
        StepVerifier.create(saveOperations)
                .expectNextCount(3)
                .verifyComplete();
        
        StepVerifier.create(playerRepository.findPlayersByBalanceRange(
                BigDecimal.valueOf(600), BigDecimal.valueOf(2000)))
                .expectNextMatches(player -> player.getUsername().equals("player2"))
                .verifyComplete();
    }

    @Test
    void findAll_ShouldReturnAllPlayers() {
        // Given
        Player player1 = new Player("player1", "player1@example.com");
        Player player2 = new Player("player2", "player2@example.com");
        
        Flux<Player> saveOperations = Flux.just(player1, player2)
                .flatMap(playerRepository::save);
        
        // When & Then
        StepVerifier.create(saveOperations)
                .expectNextCount(2)
                .verifyComplete();
        
        StepVerifier.create(playerRepository.findAll())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void save_ShouldPersistPlayer() {
        // When & Then
        StepVerifier.create(playerRepository.save(testPlayer))
                .expectNextMatches(savedPlayer -> 
                    savedPlayer.getId() != null &&
                    savedPlayer.getUsername().equals("testUser") &&
                    savedPlayer.getEmail().equals("test@example.com") &&
                    savedPlayer.getBalance().equals(BigDecimal.valueOf(1000)))
                .verifyComplete();
    }

    @Test
    void deleteById_ShouldRemovePlayer() {
        // Given - save the player first
        Mono<Player> savedPlayerMono = playerRepository.save(testPlayer);
        
        // When & Then - chain operations reactively
        StepVerifier.create(savedPlayerMono
                .flatMap(savedPlayer -> 
                    playerRepository.deleteById(savedPlayer.getId())
                        .then(playerRepository.findById(savedPlayer.getId()))
                ))
                .verifyComplete();
    }

    @Test
    void update_ShouldModifyPlayerData() {
        // Given - save the player first
        Mono<Player> savedPlayerMono = playerRepository.save(testPlayer);
        
        // When & Then - chain the operations to avoid blocking issues
        StepVerifier.create(savedPlayerMono
                .flatMap(savedPlayer -> {
                    savedPlayer.setBalance(BigDecimal.valueOf(2000));
                    savedPlayer.setUpdatedAt(LocalDateTime.now());
                    return playerRepository.save(savedPlayer);
                }))
                .expectNextMatches(updated -> 
                    updated.getBalance().equals(BigDecimal.valueOf(2000)) &&
                    updated.getUsername().equals("testUser"))
                .verifyComplete();
    }

    @Test
    void findPlayersByBalanceRange_ShouldReturnEmpty_WhenNoPlayersInRange() {
        // Given
        playerRepository.save(testPlayer).block();
        
        // When & Then
        StepVerifier.create(playerRepository.findPlayersByBalanceRange(
                BigDecimal.valueOf(5000), BigDecimal.valueOf(10000)))
                .verifyComplete();
    }
} 