package com.blackjack.service.impl;

import com.blackjack.model.Card;
import com.blackjack.model.Game;
import com.blackjack.model.Hand;
import com.blackjack.repository.GameRepository;
import com.blackjack.service.DeckService;
import com.blackjack.service.GameService;
import com.blackjack.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;
    private final DeckService deckService;

    @Override
    public Mono<Game> startGame(Long playerId, BigDecimal bet) {
        return playerService.getPlayerById(playerId)
                .flatMap(player -> {
                    if (player.getBalance().compareTo(bet) < 0) {
                        return Mono.error(new IllegalStateException("Insufficient funds"));
                    }
                    
                    return deckService.needsReshuffle()
                        .flatMap(needsShuffle -> needsShuffle
                            ? deckService.initializeDeck(6).then().then(deckService.shuffle().then())
                            : deckService.shuffle().then()
                        )
                        .then(dealInitialCards())
                        .flatMap(hands -> {
                            Game game = new Game(playerId, bet);
                            game.setPlayerHand(hands.playerHand);
                            game.setDealerHand(hands.dealerHand);
                            game.setStartTime(LocalDateTime.now());
                            game.setStatus(Game.GameStatus.IN_PROGRESS);
                            
                            return gameRepository.save(game)
                                    .flatMap(savedGame -> playerService.updateBalance(playerId, bet.negate())
                                            .thenReturn(savedGame));
                        });
                });
    }

    @Override
    public Mono<Game> hit(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
                        return Mono.error(new IllegalStateException("Game is not in progress"));
                    }
                    
                    return deckService.drawCard()
                            .flatMap(card -> {
                                game.getPlayerHand().addCard(card);
                                if (game.getPlayerHand().isBusted()) {
                                    return handlePlayerBust(game);
                                }
                                return gameRepository.save(game);
                            });
                });
    }

    @Override
    public Mono<Game> stand(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getStatus() != Game.GameStatus.IN_PROGRESS) {
                        return Mono.error(new IllegalStateException("Game is not in progress"));
                    }
                    
                    return playDealerHand(game)
                            .flatMap(this::determineWinner);
                });
    }

    @Override
    public Mono<Game> split(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (!canSplit(game)) {
                        return Mono.error(new IllegalStateException("Cannot split this hand"));
                    }
                    
                    return playerService.getPlayerById(game.getPlayerId())
                            .flatMap(player -> {
                                if (player.getBalance().compareTo(game.getBet()) < 0) {
                                    return Mono.error(new IllegalStateException("Insufficient funds for split"));
                                }
                                
                                Hand originalHand = game.getPlayerHand();
                                Hand newHand = new Hand();
                                newHand.addCard(originalHand.getCards().remove(1));
                                
                                return deckService.drawCards(2)
                                        .collectList()
                                        .flatMap(cards -> {
                                            originalHand.addCard(cards.get(0));
                                            newHand.addCard(cards.get(1));
                                            
                                            Game splitGame = new Game(game.getPlayerId(), game.getBet());
                                            splitGame.setPlayerHand(newHand);
                                            splitGame.setDealerHand(game.getDealerHand());
                                            splitGame.setStartTime(LocalDateTime.now());
                                            splitGame.setStatus(Game.GameStatus.IN_PROGRESS);
                                            
                                            return gameRepository.save(game)
                                                    .then(gameRepository.save(splitGame))
                                                    .then(playerService.updateBalance(game.getPlayerId(), game.getBet().negate()))
                                                    .thenReturn(game);
                                        });
                            });
                });
    }

    @Override
    public Mono<Game> insurance(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (!canTakeInsurance(game)) {
                        return Mono.error(new IllegalStateException("Cannot take insurance"));
                    }
                    
                    BigDecimal insuranceBet = game.getBet().divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);
                    return playerService.getPlayerById(game.getPlayerId())
                            .flatMap(player -> {
                                if (player.getBalance().compareTo(insuranceBet) < 0) {
                                    return Mono.error(new IllegalStateException("Insufficient funds for insurance"));
                                }
                                
                                game.setInsuranceBet(insuranceBet);
                                return playerService.updateBalance(game.getPlayerId(), insuranceBet.negate())
                                        .then(gameRepository.save(game));
                            });
                });
    }

    @Override
    public Mono<Game> getGameById(String gameId) {
        return gameRepository.findById(gameId);
    }

    @Override
    public Flux<Game> getActiveGames(Long playerId) {
        return gameRepository.findByPlayerIdAndStatus(playerId, Game.GameStatus.IN_PROGRESS);
    }

    @Override
    public Flux<Game> getCompletedGames(Long playerId, LocalDateTime startDate, LocalDateTime endDate) {
        return gameRepository.findGamesByPlayerIdAndDateRange(playerId, startDate, endDate);
    }

    @Override
    public Flux<Game> getHighStakeGames(BigDecimal threshold) {
        return gameRepository.findHighStakeGames(threshold);
    }

    @Override
    public Mono<Long> cleanupOldGames(LocalDateTime olderThan) {
        return gameRepository.deleteByStatusAndEndTimeBefore(Game.GameStatus.COMPLETED, olderThan);
    }

    private Mono<InitialHands> dealInitialCards() {
        return deckService.drawCards(4)
                .collectList()
                .map(cards -> {
                    Hand playerHand = new Hand();
                    Hand dealerHand = new Hand();
                    
                    playerHand.addCard(cards.get(0));
                    dealerHand.addCard(cards.get(1));
                    playerHand.addCard(cards.get(2));
                    dealerHand.addCard(cards.get(3));
                    dealerHand.getCards().get(1).flip(); // Hide dealer's second card
                    
                    return new InitialHands(playerHand, dealerHand);
                });
    }

    private Mono<Game> playDealerHand(Game game) {
        return Mono.just(game)
                .flatMap(g -> {
                    g.getDealerHand().getCards().get(1).flip(); // Reveal dealer's hidden card
                    
                    return Mono.defer(() -> {
                        if (shouldDealerDraw(g.getDealerHand())) {
                            return deckService.drawCard()
                                    .map(card -> {
                                        g.getDealerHand().addCard(card);
                                        return g;
                                    })
                                    .flatMap(this::playDealerHand);
                        }
                        return Mono.just(g);
                    });
                });
    }

    private Mono<Game> determineWinner(Game game) {
        Hand playerHand = game.getPlayerHand();
        Hand dealerHand = game.getDealerHand();
        
        if (dealerHand.isBlackjack() && game.getInsuranceBet() != null) {
            // Player wins insurance bet
            return handleInsuranceWin(game);
        }
        
        if (playerHand.isBlackjack()) {
            return handleBlackjackWin(game);
        } else if (dealerHand.isBlackjack()) {
            return handleDealerWin(game);
        } else if (playerHand.isBusted()) {
            return handleDealerWin(game);
        } else if (dealerHand.isBusted()) {
            return handlePlayerWin(game);
        } else {
            int playerScore = playerHand.getValue();
            int dealerScore = dealerHand.getValue();
            
            if (playerScore > dealerScore) {
                return handlePlayerWin(game);
            } else if (dealerScore > playerScore) {
                return handleDealerWin(game);
            } else {
                return handlePush(game);
            }
        }
    }

    private boolean shouldDealerDraw(Hand hand) {
        int value = hand.getValue();
        return value < 17 || (value == 17 && hand.isSoft());
    }

    private boolean canSplit(Game game) {
        Hand hand = game.getPlayerHand();
        return hand.getCards().size() == 2 &&
               hand.getCards().get(0).getRank() == hand.getCards().get(1).getRank();
    }

    private boolean canTakeInsurance(Game game) {
        return game.getStatus() == Game.GameStatus.IN_PROGRESS &&
               game.getInsuranceBet() == null &&
               game.getDealerHand().getCards().getFirst().getRank() == Card.Rank.ACE;
    }

    private Mono<Game> handleGameCompletion(Game game, boolean isPlayerWin, BigDecimal payoutMultiplier) {
        game.setStatus(Game.GameStatus.COMPLETED);
        game.setEndTime(LocalDateTime.now());
        
        if (payoutMultiplier != null && payoutMultiplier.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal winnings = game.getBet().multiply(payoutMultiplier);
            return playerService.updateBalance(game.getPlayerId(), winnings)
                    .then(playerService.updateStatistics(game.getPlayerId(), isPlayerWin, game.getBet()))
                    .then(gameRepository.save(game));
        }
        
        return playerService.updateStatistics(game.getPlayerId(), isPlayerWin, game.getBet())
                .then(gameRepository.save(game));
    }

    private Mono<Game> handlePlayerBust(Game game) {
        return handleGameCompletion(game, false, null);
    }

    private Mono<Game> handlePlayerWin(Game game) {
        return handleGameCompletion(game, true, BigDecimal.valueOf(2));
    }

    private Mono<Game> handleDealerWin(Game game) {
        return handleGameCompletion(game, false, null);
    }

    private Mono<Game> handleBlackjackWin(Game game) {
        return handleGameCompletion(game, true, BigDecimal.valueOf(2.5));
    }

    private Mono<Game> handlePush(Game game) {
        return handleGameCompletion(game, true, BigDecimal.ONE);
    }

    private Mono<Game> handleInsuranceWin(Game game) {
        BigDecimal insuranceWin = game.getInsuranceBet().multiply(BigDecimal.valueOf(2));
        return playerService.updateBalance(game.getPlayerId(), insuranceWin)
                .then(handleDealerWin(game));
    }

    private record InitialHands(Hand playerHand, Hand dealerHand) {}
} 