package com.blackjack.repository;

import com.blackjack.model.Game;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Implementation of CustomGameRepository interface.
 * Provides complex MongoDB queries and aggregations for game analysis.
 */
@Repository
public class CustomGameRepositoryImpl implements CustomGameRepository {

    private static final String STATUS = "status";
    private static final String PLAYER_ID = "playerId";
    private static final String START_TIME = "startTime";

    private final ReactiveMongoTemplate mongoTemplate;

    public CustomGameRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Flux<Game> findGamesByPlayerIdAndDateRange(Long playerId, LocalDateTime startDate, LocalDateTime endDate) {
        Query query = new Query()
                .addCriteria(Criteria.where(PLAYER_ID).is(playerId)
                        .and(START_TIME).gte(startDate)
                        .and(START_TIME).lte(endDate));
        
        return mongoTemplate.find(query, Game.class);
    }

    @Override
    public Flux<Game> findHighStakeGames(BigDecimal threshold) {
        Query query = new Query()
                .addCriteria(Criteria.where("bet").gte(threshold));
        
        return mongoTemplate.find(query, Game.class);
    }



    private Mono<Game> enrichGameWithDetails(Game game) {
        // Additional enrichment logic can be added here
        return Mono.just(game);
    }

    @Override
    public Mono<Game> findLastUnfinishedGameByPlayerId(Long playerId) {
        Query query = new Query()
                .addCriteria(Criteria.where(PLAYER_ID).is(playerId)
                        .and(STATUS).is(Game.GameStatus.IN_PROGRESS))
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, START_TIME))
                .limit(1);
        
        return mongoTemplate.findOne(query, Game.class);
    }

    @Override
    public Flux<Game> findTopWinningGames(int limit) {
        Query query = new Query()
                .addCriteria(Criteria.where("result").in("PLAYER_WIN", "PLAYER_BLACKJACK"))
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "bet"))
                .limit(limit);
        
        return mongoTemplate.find(query, Game.class);
    }

    @Override
    public Flux<Game> findGamesByPlayerIdAndResult(Long playerId, Game.GameResult result) {
        Query query = new Query()
                .addCriteria(Criteria.where(PLAYER_ID).is(playerId)
                        .and("result").is(result));
        
        return mongoTemplate.find(query, Game.class);
    }

    @Override
    public Flux<Game> findGamesByBetRange(BigDecimal minBet, BigDecimal maxBet, org.springframework.data.domain.Pageable pageable) {
        Query query = new Query()
                .addCriteria(Criteria.where("bet").gte(minBet).lte(maxBet))
                .with(pageable);
        
        return mongoTemplate.find(query, Game.class);
    }

    @Override
    public Mono<BigDecimal> calculateTotalWinnings(Long playerId, LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where(PLAYER_ID).is(playerId)
                        .and(STATUS).is(Game.GameStatus.COMPLETED)
                        .and(START_TIME).gte(startDate).lte(endDate)),
                Aggregation.project()
                        .andExpression("cond: { if: { $eq: ['$result', 'PLAYER_WIN'] }, then: '$bet', else: { cond: { if: { $eq: ['$result', 'PLAYER_BLACKJACK'] }, then: { $multiply: ['$bet', 1.5] }, else: 0 } } }")
                        .as("winnings"),
                Aggregation.group().sum("winnings").as("totalWinnings")
        );

        return mongoTemplate.aggregate(aggregation, Game.class, Object.class)
                .map(result -> {
                    org.bson.Document doc = (org.bson.Document) result;
                    Number winnings = doc.getDouble("totalWinnings");
                    return winnings != null ? new BigDecimal(winnings.toString()) : BigDecimal.ZERO;
                })
                .next()
                .defaultIfEmpty(BigDecimal.ZERO);
    }

    @Override
    public Flux<Game> findGamesByActionSequence(java.util.List<Game.GameAction> actions, int limit) {
        Query query = new Query()
                .addCriteria(Criteria.where("actions").is(actions))
                .limit(limit);
        
        return mongoTemplate.find(query, Game.class);
    }
} 