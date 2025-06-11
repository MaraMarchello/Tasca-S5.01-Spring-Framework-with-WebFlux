-- ================================================
-- Blackjack Database Setup Script for MySQL
-- ================================================

-- Create the blackjack database
CREATE DATABASE IF NOT EXISTS blackjack
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;

-- Use the database
USE blackjack;

-- Create application user
CREATE USER IF NOT EXISTS 'blackjack_user'@'localhost' IDENTIFIED BY 'blackjack_password';

-- Grant privileges to the user
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, INDEX ON blackjack.* TO 'blackjack_user'@'localhost';

-- Grant privileges for remote access (if needed)
CREATE USER IF NOT EXISTS 'blackjack_user'@'%' IDENTIFIED BY 'blackjack_password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, ALTER, INDEX ON blackjack.* TO 'blackjack_user'@'%';

-- Flush privileges to ensure changes take effect
FLUSH PRIVILEGES;

-- Show created database and user
SHOW DATABASES LIKE 'blackjack';
SELECT User, Host FROM mysql.user WHERE User = 'blackjack_user';

-- ================================================
-- Optional: Create basic tables structure
-- ================================================

-- Players table (for R2DBC)
CREATE TABLE IF NOT EXISTS players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    total_winnings DECIMAL(10,2) DEFAULT 0.00,
    games_played INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_player_name (name)
);

-- Sample data
INSERT INTO players (name, total_winnings, games_played) VALUES 
('Alice', 250.50, 15),
('Bob', -100.25, 8),
('Charlie', 500.00, 25)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Verify setup
SELECT 'Database setup completed successfully!' as status;
SELECT * FROM players; 