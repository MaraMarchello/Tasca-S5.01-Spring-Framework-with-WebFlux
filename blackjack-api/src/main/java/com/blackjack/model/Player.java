package com.blackjack.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Table("players")
public class Player {
    @Id
    private Long id;
    
    @NotBlank
    @Column("username")
    private String username;
    
    @Email
    @NotBlank
    @Column("email")
    private String email;
    
    @Min(0)
    @Column("balance")
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Min(0)
    @Column("games_played")
    private int gamesPlayed = 0;
    
    @Min(0)
    @Column("games_won")
    private int gamesWon = 0;
    
    @Column("total_winnings")
    private BigDecimal totalWinnings = BigDecimal.ZERO;
    
    // Daily statistics fields referenced in repository
    @Min(0)
    @Column("games_played_today")
    private int gamesPlayedToday = 0;
    
    @Min(0)
    @Column("games_won_today")
    private int gamesWonToday = 0;
    
    @Column("last_login_date")
    private LocalDateTime lastLoginDate;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;

    public Player(String username, String email) {
        this.username = username;
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStats(boolean won) {
        this.gamesPlayed++;
        this.gamesPlayedToday++;
        if (won) {
            this.gamesWon++;
            this.gamesWonToday++;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBalance(BigDecimal amount) {
        if (this.balance.add(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
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
        this.gamesPlayedToday++;
        if (won) {
            this.gamesWon++;
            this.gamesWonToday++;
            this.totalWinnings = this.totalWinnings.add(amount);
            this.balance = this.balance.add(amount);
        } else {
            this.balance = this.balance.subtract(amount);
        }
    }

    public void resetDailyStatistics() {
        this.gamesPlayedToday = 0;
        this.gamesWonToday = 0;
        this.updatedAt = LocalDateTime.now();
    }

    public double getWinRate() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return (double) gamesWon / gamesPlayed;
    }

    public double getDailyWinRate() {
        if (gamesPlayedToday == 0) {
            return 0.0;
        }
        return (double) gamesWonToday / gamesPlayedToday;
    }

    @Override
    public String toString() {
        return String.format("Player{username='%s', email='%s', balance=%s, gamesPlayed=%d, gamesWon=%d, winRate=%.2f}",
                username, email, balance, gamesPlayed, gamesWon, getWinRate());
    }
} 