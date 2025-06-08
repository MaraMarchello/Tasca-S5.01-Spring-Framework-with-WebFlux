package com.blackjack.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table("players")
public class Player {
    @Id
    private Long id;
    private String username;
    private String email;
    private BigDecimal balance = BigDecimal.ZERO;
    private int gamesPlayed = 0;
    private int gamesWon = 0;
    private BigDecimal totalWinnings = BigDecimal.ZERO;
    private LocalDateTime lastLoginDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Player(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStats(boolean won) {
        this.gamesPlayed++;
        if (won) {
            this.gamesWon++;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void setBalance(BigDecimal balance) {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    public void updateStatistics(boolean won, BigDecimal amount) {
        this.gamesPlayed++;
        if (won) {
            this.gamesWon++;
            this.totalWinnings = this.totalWinnings.add(amount);
            this.balance = this.balance.add(amount);
        } else {
            this.balance = this.balance.subtract(amount);
        }
    }

    public double getWinRate() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return (double) gamesWon / gamesPlayed;
    }

    @Override
    public String toString() {
        return String.format("Player{username='%s', email='%s', balance=%s, gamesPlayed=%d, gamesWon=%d, winRate=%.2f}",
                username, email, balance, gamesPlayed, gamesWon, getWinRate());
    }
} 