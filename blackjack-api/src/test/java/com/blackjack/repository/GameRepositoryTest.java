package com.blackjack.repository;

import com.blackjack.MongoTestConfiguration;
import com.blackjack.model.Game;
import com.blackjack.model.Hand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@DataMongoTest
@Import(MongoTestConfiguration.class)
class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    private Game testGame;
    private final Long testPlayerId = 1L;

    @BeforeEach
    void setUp() {
        testGame = new Game(testPlayerId, BigDecimal.valueOf(100));
        testGame.setStartTime(LocalDateTime.now());
        testGame.setEndTime(LocalDateTime.now());
        testGame.setPlayerHand(new Hand());
        testGame.setDealerHand(new Hand());
        testGame.setStatus(Game.GameStatus.IN_PROGRESS);

        // Clean up and insert test data
        gameRepository.deleteAll()
                .then(gameRepository.save(testGame))
                .block();
    }

    @Test
    void findByPlayerId_ShouldReturnGames() {
        StepVerifier.create(gameRepository.findByPlayerId(testPlayerId))
                .expectNextMatches(game -> 
                    game.getPlayerId().equals(testPlayerId) &&
                    game.getBet().equals(BigDecimal.valueOf(100)))
                .verifyComplete();
    }

    @Test
    void findByPlayerIdAndStatus_ShouldReturnFilteredGames() {
        StepVerifier.create(gameRepository.findByPlayerIdAndStatus(testPlayerId, Game.GameStatus.IN_PROGRESS))
                .expectNextMatches(game -> 
                    game.getPlayerId().equals(testPlayerId) &&
                    game.getStatus().equals(Game.GameStatus.IN_PROGRESS))
                .verifyComplete();
    }

    @Test
    void findByPlayerId_WithPagination_ShouldReturnPagedGames() {
        // Create additional games
        Game game1 = new Game(testPlayerId, BigDecimal.valueOf(200));
        Game game2 = new Game(testPlayerId, BigDecimal.valueOf(300));

        StepVerifier.create(
                gameRepository.deleteAll()
                    .then(Mono.when(
                        gameRepository.save(testGame),
                        gameRepository.save(game1),
                        gameRepository.save(game2)))
                    .thenMany(gameRepository.findByPlayerIdOrderByStartTimeDesc(
                        testPlayerId, 
                        PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "startTime")))))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void countActiveGamesByPlayerId_ShouldReturnCorrectCount() {
        StepVerifier.create(gameRepository.countByPlayerIdAndStatus(testPlayerId, Game.GameStatus.IN_PROGRESS))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void findHighStakeGames_ShouldReturnGamesAboveThreshold() {
        // Given
        Game highStakeGame = new Game(testPlayerId, BigDecimal.valueOf(1000));
        highStakeGame.setStatus(Game.GameStatus.IN_PROGRESS);
        
        // When & Then
        StepVerifier.create(
                gameRepository.deleteAll()
                    .then(gameRepository.save(highStakeGame))
                    .thenMany(gameRepository.findHighStakeGames(BigDecimal.valueOf(500))))
                .expectNextMatches(game -> 
                    game.getBet().compareTo(BigDecimal.valueOf(500)) > 0 &&
                    game.getStatus() == Game.GameStatus.IN_PROGRESS)
                .verifyComplete();
    }

    @Test
    void deleteCompletedGamesOlderThan_ShouldRemoveOldGames() {
        Game oldGame = new Game(testPlayerId, BigDecimal.valueOf(100));
        oldGame.setStatus(Game.GameStatus.COMPLETED);
        oldGame.setEndTime(LocalDateTime.now().minusDays(7));

        Game recentGame = new Game(testPlayerId, BigDecimal.valueOf(100));
        recentGame.setStatus(Game.GameStatus.COMPLETED);
        recentGame.setEndTime(LocalDateTime.now());

        StepVerifier.create(
                gameRepository.deleteAll()
                    .then(Mono.when(
                        gameRepository.save(oldGame),
                        gameRepository.save(recentGame)))
                    .then(gameRepository.deleteByStatusAndEndTimeBefore(
                        Game.GameStatus.COMPLETED, 
                        LocalDateTime.now().minusDays(1))))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void findGamesByPlayerIdAndDateRange_ShouldReturnFilteredGames() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusDays(1);

        Game game = new Game(testPlayerId, BigDecimal.valueOf(100));
        game.setStartTime(now);
        game.setEndTime(now);

        StepVerifier.create(
                gameRepository.deleteAll()
                    .then(gameRepository.save(game))
                    .thenMany(gameRepository.findGamesByPlayerIdAndDateRange(testPlayerId, startDate, endDate)))
                .expectNextMatches(g -> 
                    g.getPlayerId().equals(testPlayerId) &&
                    !g.getStartTime().isBefore(startDate) &&
                    !g.getStartTime().isAfter(endDate))
                .verifyComplete();
    }

    @Test
    void findLastUnfinishedGameByPlayerId_ShouldReturnGame() {
        StepVerifier.create(gameRepository.findLastUnfinishedGameByPlayerId(testPlayerId))
                .expectNextMatches(game -> 
                    game.getPlayerId().equals(testPlayerId) &&
                    game.getStatus().equals(Game.GameStatus.IN_PROGRESS))
                .verifyComplete();
    }

    @Test
    void findTopWinningGames_ShouldReturnOrderedGames() {
        Game winningGame1 = new Game(testPlayerId, BigDecimal.valueOf(500));
        winningGame1.setStatus(Game.GameStatus.COMPLETED);
        winningGame1.setResult(Game.GameResult.PLAYER_WIN);

        Game winningGame2 = new Game(testPlayerId, BigDecimal.valueOf(300));
        winningGame2.setStatus(Game.GameStatus.COMPLETED);
        winningGame2.setResult(Game.GameResult.PLAYER_BLACKJACK);

        StepVerifier.create(
                gameRepository.deleteAll()
                    .then(Mono.when(
                        gameRepository.save(winningGame1),
                        gameRepository.save(winningGame2)))
                    .thenMany(gameRepository.findTopWinningGames(2)))
                .expectNextMatches(game -> game.getBet().equals(BigDecimal.valueOf(500)))
                .expectNextMatches(game -> game.getBet().equals(BigDecimal.valueOf(300)))
                .verifyComplete();
    }

    @Test
    void findGamesByActionSequence_ShouldReturnMatchingGames() {
        Game gameWithActions = new Game(testPlayerId, BigDecimal.valueOf(100));
        List<Game.GameAction> actions = Arrays.asList(
            Game.GameAction.HIT,
            Game.GameAction.STAND
        );
        actions.forEach(gameWithActions::addAction);

        StepVerifier.create(
                gameRepository.save(gameWithActions)
                    .then(Mono.from(gameRepository.findGamesByActionSequence(actions, 1))))
                .expectNextMatches(game -> 
                    game.getActions().equals(actions))
                .verifyComplete();
    }

    @Test
    void calculateTotalWinnings_ShouldReturnCorrectAmount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusDays(1);
        LocalDateTime endDate = now.plusDays(1);

        Game winGame = new Game(testPlayerId, BigDecimal.valueOf(200));
        winGame.setStatus(Game.GameStatus.COMPLETED);
        winGame.setResult(Game.GameResult.PLAYER_WIN);
        winGame.setStartTime(now);
        winGame.setEndTime(now);

        Game loseGame = new Game(testPlayerId, BigDecimal.valueOf(100));
        loseGame.setStatus(Game.GameStatus.COMPLETED);
        loseGame.setResult(Game.GameResult.DEALER_WIN);
        loseGame.setStartTime(now);
        loseGame.setEndTime(now);

        StepVerifier.create(
                gameRepository.deleteAll()
                    .then(gameRepository.save(winGame))  // Save games one by one
                    .then(gameRepository.save(loseGame))
                    .then(gameRepository.calculateTotalWinnings(testPlayerId, startDate, endDate)))
                .expectNext(BigDecimal.valueOf(200))
                .verifyComplete();
    }
} 