-- Create players table for R2DBC tests
CREATE TABLE IF NOT EXISTS players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    games_played INT NOT NULL DEFAULT 0,
    games_won INT NOT NULL DEFAULT 0,
    total_winnings DECIMAL(19,2) NOT NULL DEFAULT 0.00,
    games_played_today INT NOT NULL DEFAULT 0,
    games_won_today INT NOT NULL DEFAULT 0,
    last_login_date DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
); 