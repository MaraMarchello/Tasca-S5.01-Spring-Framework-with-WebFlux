package com.blackjack.service;

import com.blackjack.model.Player;
import com.blackjack.repository.PlayerRepository;
import com.blackjack.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    private PlayerService playerService;

    private Player testPlayer;

    @BeforeEach
    void setUp() {
        playerService = new PlayerServiceImpl(playerRepository);
        
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setUsername("testUser");
        testPlayer.setEmail("test@example.com");
        testPlayer.setBalance(BigDecimal.valueOf(1000));
        testPlayer.setGamesPlayed(10);
        testPlayer.setGamesWon(5);
        testPlayer.setTotalWinnings(BigDecimal.valueOf(500));
        testPlayer.setCreatedAt(LocalDateTime.now());
        testPlayer.setUpdatedAt(LocalDateTime.now());

        // Reset mock before each test
        reset(playerRepository);
    }

    @Test
    void createPlayer_ShouldSaveAndReturnPlayer() {
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(playerService.createPlayer(testPlayer))
                .expectNext(testPlayer)
                .verifyComplete();

        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void getPlayerById_ShouldReturnPlayer_WhenExists() {
        when(playerRepository.findById(1L)).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(playerService.getPlayerById(1L))
                .expectNext(testPlayer)
                .verifyComplete();

        verify(playerRepository).findById(1L);
    }

    @Test
    void getPlayerById_ShouldReturnEmpty_WhenNotExists() {
        when(playerRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(playerService.getPlayerById(999L))
                .verifyComplete();

        verify(playerRepository).findById(999L);
    }

    @Test
    void getPlayerByUsername_ShouldReturnPlayer_WhenExists() {
        when(playerRepository.findByUsername("testUser")).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(playerService.getPlayerByUsername("testUser"))
                .expectNext(testPlayer)
                .verifyComplete();

        verify(playerRepository).findByUsername("testUser");
    }

    @Test
    void updatePlayer_ShouldUpdateAndReturnPlayer() {
        Player updatedPlayer = new Player();
        updatedPlayer.setUsername("updatedUser");
        updatedPlayer.setEmail("updated@example.com");

        when(playerRepository.findById(1L)).thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(playerService.updatePlayer(1L, updatedPlayer))
                .expectNext(testPlayer)
                .verifyComplete();

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepository).save(playerCaptor.capture());
        
        Player savedPlayer = playerCaptor.getValue();
        assertEquals("updatedUser", savedPlayer.getUsername());
        assertEquals("updated@example.com", savedPlayer.getEmail());
    }

    @Test
    void deletePlayer_ShouldCallRepository() {
        when(playerRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(playerService.deletePlayer(1L))
                .verifyComplete();

        verify(playerRepository).deleteById(1L);
    }

    @Test
    void updateBalance_ShouldUpdateAndReturnPlayer_WhenSufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(100);
        when(playerRepository.findById(1L)).thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(playerService.updateBalance(1L, amount))
                .expectNext(testPlayer)
                .verifyComplete();

        verify(playerRepository).findById(1L);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void updateBalance_ShouldThrowException_WhenInsufficientFunds() {
        BigDecimal amount = BigDecimal.valueOf(-2000);
        when(playerRepository.findById(1L)).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(playerService.updateBalance(1L, amount))
                .expectError(IllegalStateException.class)
                .verify();

        verify(playerRepository).findById(1L);
    }

    @Test
    void updateStatistics_ShouldUpdateAndReturnPlayer() {
        when(playerRepository.findById(1L)).thenReturn(Mono.just(testPlayer));
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(playerService.updateStatistics(1L, true, BigDecimal.valueOf(100)))
                .expectNext(testPlayer)
                .verifyComplete();

        verify(playerRepository).findById(1L);
        verify(playerRepository).save(any(Player.class));
    }

    @Test
    void getTopPlayers_ShouldReturnPlayers() {
        when(playerRepository.findAll()).thenReturn(Flux.just(testPlayer));

        StepVerifier.create(playerService.getTopPlayers(10))
                .expectNext(testPlayer)
                .verifyComplete();

        verify(playerRepository).findAll();
    }

    @Test
    void getPlayersWithBalanceAbove_ShouldReturnPlayers() {
        when(playerRepository.findPlayersByBalanceRange(any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(Flux.just(testPlayer));

        StepVerifier.create(playerService.getPlayersWithBalanceAbove(BigDecimal.valueOf(500)))
                .expectNext(testPlayer)
                .verifyComplete();

        verify(playerRepository).findPlayersByBalanceRange(any(BigDecimal.class), any(BigDecimal.class));
    }
}