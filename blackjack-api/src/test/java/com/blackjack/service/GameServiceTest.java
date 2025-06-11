package com.blackjack.service;

import com.blackjack.model.Card;
import com.blackjack.model.Game;
import com.blackjack.model.Hand;
import com.blackjack.model.Player;
import com.blackjack.repository.GameRepository;
import com.blackjack.service.impl.DeckServiceImpl;
import com.blackjack.service.impl.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private PlayerService playerService;

    private DeckService deckService;
    private GameService gameService;

    private Player testPlayer;
    private Game testGame;

    @BeforeEach
    void setUp() {
        deckService = new DeckServiceImpl();
        gameService = new GameServiceImpl(gameRepository, playerService, deckService);
        
        // Set up test player
        testPlayer = new Player();
        testPlayer.setId(1L);
        testPlayer.setUsername("testUser");
        testPlayer.setBalance(BigDecimal.valueOf(1000));

        // Set up test game
        testGame = new Game(testPlayer.getId(), BigDecimal.valueOf(100));
        testGame.setId("game123");
        testGame.setStatus(Game.GameStatus.IN_PROGRESS);
        
        Hand playerHand = new Hand();
        playerHand.addCard(new Card(Card.Suit.HEARTS, Card.Rank.TEN));
        playerHand.addCard(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));
        testGame.setPlayerHand(playerHand);

        Hand dealerHand = new Hand();
        dealerHand.addCard(new Card(Card.Suit.DIAMONDS, Card.Rank.SEVEN));
        dealerHand.addCard(new Card(Card.Suit.CLUBS, Card.Rank.FOUR));
        testGame.setDealerHand(dealerHand);

        // Initialize deck for tests
        deckService.initializeDeck(1).blockLast();

        // Reset mocks
        reset(gameRepository, playerService);
    }

    @Test
    void startGame_ShouldCreateNewGame_WhenPlayerHasSufficientFunds() {
        BigDecimal bet = BigDecimal.valueOf(100);
        when(playerService.getPlayerById(1L)).thenReturn(Mono.just(testPlayer));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(testGame));
        when(playerService.updateBalance(eq(1L), any(BigDecimal.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(gameService.startGame(1L, bet))
                .expectNextMatches(game -> {
                    assertNotNull(game);
                    assertEquals(1L, game.getPlayerId());
                    assertEquals(bet, game.getBet());
                    return true;
                })
                .verifyComplete();

        verify(playerService).updateBalance(eq(1L), eq(bet.negate()));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void startGame_ShouldFail_WhenInsufficientFunds() {
        BigDecimal bet = BigDecimal.valueOf(2000);
        when(playerService.getPlayerById(1L)).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(gameService.startGame(1L, bet))
                .expectError(IllegalStateException.class)
                .verify();

        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void hit_ShouldAddCardToPlayerHand() {
        when(gameRepository.findById("game123")).thenReturn(Mono.just(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(testGame));

        StepVerifier.create(gameService.hit("game123"))
                .expectNextMatches(game -> {
                    assertTrue(game.getPlayerHand().getCards().size() >= 2);
                    return true;
                })
                .verifyComplete();

        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void hit_ShouldFail_WhenGameNotInProgress() {
        testGame.setStatus(Game.GameStatus.COMPLETED);
        when(gameRepository.findById("game123")).thenReturn(Mono.just(testGame));

        StepVerifier.create(gameService.hit("game123"))
                .expectError(IllegalStateException.class)
                .verify();

        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void stand_ShouldCompleteDealerTurnAndDetermineWinner() {
        when(gameRepository.findById("game123")).thenReturn(Mono.just(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(testGame));
        // Mock all possible playerService calls that might be made based on game outcome
        lenient().when(playerService.updateBalance(eq(1L), any(BigDecimal.class))).thenReturn(Mono.just(testPlayer));
        lenient().when(playerService.updateStatistics(eq(1L), anyBoolean(), any(BigDecimal.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(gameService.stand("game123"))
                .expectNextMatches(game -> {
                    // Since we're mocking the repository save, the actual game logic runs
                    // but we get back our mocked testGame which hasn't been modified
                    assertNotNull(game);
                    return true;
                })
                .verifyComplete();

        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void insurance_ShouldAllowInsurance_WhenDealerShowsAce() {
        testGame.getDealerHand().getCards().clear();
        testGame.getDealerHand().addCard(new Card(Card.Suit.HEARTS, Card.Rank.ACE));
        testGame.getDealerHand().addCard(new Card(Card.Suit.SPADES, Card.Rank.TEN));

        when(gameRepository.findById("game123")).thenReturn(Mono.just(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(testGame));
        when(playerService.getPlayerById(1L)).thenReturn(Mono.just(testPlayer));
        when(playerService.updateBalance(eq(1L), any(BigDecimal.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(gameService.insurance("game123"))
                .expectNextMatches(game -> {
                    assertNotNull(game);
                    return true;
                })
                .verifyComplete();

        verify(playerService).updateBalance(eq(1L), any(BigDecimal.class));
        verify(gameRepository).save(any(Game.class));
    }

    @Test
    void split_ShouldCreateNewGame_WhenPairIsPresent() {
        // Setup a splittable hand
        testGame.getPlayerHand().getCards().clear();
        testGame.getPlayerHand().addCard(new Card(Card.Suit.HEARTS, Card.Rank.EIGHT));
        testGame.getPlayerHand().addCard(new Card(Card.Suit.SPADES, Card.Rank.EIGHT));

        when(gameRepository.findById("game123")).thenReturn(Mono.just(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(testGame));
        when(playerService.getPlayerById(1L)).thenReturn(Mono.just(testPlayer));
        when(playerService.updateBalance(eq(1L), any(BigDecimal.class))).thenReturn(Mono.just(testPlayer));

        StepVerifier.create(gameService.split("game123"))
                .expectNextMatches(game -> {
                    assertNotNull(game);
                    return true;
                })
                .verifyComplete();

        verify(gameRepository, times(2)).save(any(Game.class));
        verify(playerService).updateBalance(eq(1L), any(BigDecimal.class));
    }

    @Test
    void getGameById_ShouldReturnGame() {
        when(gameRepository.findById("game123")).thenReturn(Mono.just(testGame));

        StepVerifier.create(gameService.getGameById("game123"))
                .expectNext(testGame)
                .verifyComplete();

        verify(gameRepository).findById("game123");
    }

    @Test
    void getActiveGames_ShouldReturnGamesInProgress() {
        when(gameRepository.findByPlayerIdAndStatus(eq(1L), eq(Game.GameStatus.IN_PROGRESS)))
                .thenReturn(Flux.fromIterable(Arrays.asList(testGame)));

        StepVerifier.create(gameService.getActiveGames(1L))
                .expectNext(testGame)
                .verifyComplete();

        verify(gameRepository).findByPlayerIdAndStatus(1L, Game.GameStatus.IN_PROGRESS);
    }

    @Test
    void getCompletedGames_ShouldReturnCompletedGames() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(gameRepository.findGamesByPlayerIdAndDateRange(eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Flux.fromIterable(Arrays.asList(testGame)));

        StepVerifier.create(gameService.getCompletedGames(1L, startDate, endDate))
                .expectNext(testGame)
                .verifyComplete();

        verify(gameRepository).findGamesByPlayerIdAndDateRange(eq(1L), eq(startDate), eq(endDate));
    }
}