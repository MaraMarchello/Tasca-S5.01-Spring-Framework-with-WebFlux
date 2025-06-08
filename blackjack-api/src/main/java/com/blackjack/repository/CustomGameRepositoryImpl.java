package com.blackjack.repository;

import com.blackjack.model.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

/**
 * Implementation of CustomGameRepository interface.
 * Provides complex MongoDB queries and aggregations for game analysis.
 */
@Component
public class CustomGameRepositoryImpl implements CustomGameRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public CustomGameRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Game> findGamesByPlayerIdAndDateRange(Long playerId, LocalDateTime startDate, LocalDateTime endDate) {
        Query query = Query.query(
                Criteria.where("playerId").is(playerId)
                        .and("startTime").gte(startDate)
                        .and("endTime").lte(endDate))
                .with(Sort.by(Sort.Direction.DESC, "startTime"));

        return mongoTemplate.find(query, Game.class)
                .onErrorResume(e -> Flux.error(new RuntimeException("Error fetching games by date range: " + e.getMessage())));
    }

    @Override
    public Mono<Game> findLastUnfinishedGameByPlayerId(Long playerId) {
        Query query = Query.query(
                Criteria.where("playerId").is(playerId)
                        .and("status").is(Game.GameStatus.IN_PROGRESS))
                .with(Sort.by(Sort.Direction.DESC, "startTime"))
                .limit(1);

        return mongoTemplate.findOne(query, Game.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error fetching unfinished game: " + e.getMessage())));
    }

    @Override
    public Flux<Game> findTopWinningGames(int limit) {
        Query query = Query.query(
                Criteria.where("status").is(Game.GameStatus.COMPLETED)
                        .and("result").in(Game.GameResult.PLAYER_WIN, Game.GameResult.PLAYER_BLACKJACK))
                .with(Sort.by(Sort.Direction.DESC, "bet"))
                .limit(limit);

        return mongoTemplate.find(query, Game.class)
                .onErrorResume(e -> Flux.error(new RuntimeException("Error fetching top winning games: " + e.getMessage())));
    }

    @Override
    public Flux<Game> findGamesByPlayerIdAndResult(Long playerId, Game.GameResult result) {
        Query query = Query.query(
                Criteria.where("playerId").is(playerId)
                        .and("result").is(result))
                .with(Sort.by(Sort.Direction.DESC, "startTime"));

        return mongoTemplate.find(query, Game.class)
                .onErrorResume(e -> Flux.error(new RuntimeException("Error fetching games by result: " + e.getMessage())));
    }

    @Override
    public Flux<Game> findGamesByBetRange(BigDecimal minBet, BigDecimal maxBet, Pageable pageable) {
        Query query = Query.query(
                Criteria.where("bet").gte(minBet).lte(maxBet))
                .with(pageable);

        return mongoTemplate.find(query, Game.class)
                .onErrorResume(e -> Flux.error(new RuntimeException("Error fetching games by bet range: " + e.getMessage())));
    }

    @Override
    public Mono<BigDecimal> calculateTotalWinnings(Long playerId, LocalDateTime startDate, LocalDateTime endDate) {
        Query query = Query.query(
            Criteria.where("playerId").is(playerId)
                .and("startTime").gte(startDate)
                .and("endTime").lte(endDate)
                .and("status").is(Game.GameStatus.COMPLETED)
                .and("result").in(Game.GameResult.PLAYER_WIN, Game.GameResult.PLAYER_BLACKJACK));

        return mongoTemplate.find(query, Game.class)
                .map(Game::getBet)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .defaultIfEmpty(BigDecimal.ZERO)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error calculating total winnings: " + e.getMessage())));
    }

    @Override
    public Flux<Game> findGamesByActionSequence(List<Game.GameAction> actions, int limit) {
        Query query = Query.query(
                Criteria.where("actions").all(actions))
                .with(Sort.by(Sort.Direction.DESC, "startTime"))
                .limit(limit);

        return mongoTemplate.find(query, Game.class)
                .onErrorResume(e -> Flux.error(new RuntimeException("Error fetching games by action sequence: " + e.getMessage())));
    }

    @Override
    public Flux<Game> findHighStakeGames(BigDecimal minBet) {
        Query query = Query.query(
                Criteria.where("bet").gte(minBet)
                        .and("status").is(Game.GameStatus.IN_PROGRESS));

        return mongoTemplate.find(query, Game.class)
                .onErrorResume(e -> Flux.error(new RuntimeException("Error fetching high stake games: " + e.getMessage())));
    }
}