package com.blackjack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.blackjack.repository")
@EnableReactiveMongoRepositories(basePackages = "com.blackjack.repository")
public class DatabaseConfig {
    // R2DBC will automatically handle schema creation based on application.yml
    // MongoDB configuration is handled by Spring Boot auto-configuration
} 