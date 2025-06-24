package com.blackjack.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Player entity representing a blackjack player")
public class Player {
    @Id
    @Schema(description = "Unique player identifier", example = "1")
    private Long id;
    
    @NotBlank
    @Column("username")
    @Schema(description = "Player's unique username", example = "johnsmith")
    private String username;
    
    @Email
    @NotBlank
    @Column("email")
    @Schema(description = "Player's email address", example = "john.smith@example.com")
    private String email;
    
    @Min(0)
    @Column("balance")
    @Schema(description = "Player's current balance", example = "150.75")
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Min(0)
    @Column("games_played")
    @Schema(description = "Total number of games played", example = "45")
    private int gamesPlayed = 0;
    
    @Min(0)
    @Column("games_won")
    @Schema(description = "Total number of games won", example = "23")
    private int gamesWon = 0;
    
    @Column("total_winnings")
    @Schema(description = "Total amount won across all games", example = "275.50")
    private BigDecimal totalWinnings = BigDecimal.ZERO;
    
    // Daily statistics fields referenced in repository
    @Min(0)
    @Column("games_played_today")
    @Schema(description = "Number of games played today", example = "5")
    private int gamesPlayedToday = 0;
    
    @Min(0)
    @Column("games_won_today")
    @Schema(description = "Number of games won today", example = "3")
    private int gamesWonToday = 0;
    
    @Column("last_login_date")
    @Schema(description = "Last login timestamp", example = "2023-12-08T10:15:30")
    private LocalDateTime lastLoginDate;
    
    @Column("created_at")
    @Schema(description = "Account creation timestamp", example = "2023-12-01T09:00:00")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    @Schema(description = "Last update timestamp", example = "2023-12-08T15:30:45")
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

    @Schema(description = "Overall win rate (games won / games played)", example = "0.51")
    public double getWinRate() {
        if (gamesPlayed == 0) {
            return 0.0;
        }
        return (double) gamesWon / gamesPlayed;
    }

    @Schema(description = "Daily win rate (games won today / games played today)", example = "0.60")
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