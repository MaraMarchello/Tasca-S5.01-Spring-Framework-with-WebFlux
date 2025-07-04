package com.blackjack;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestcontainersConfiguration {
    
	@Bean
	@ServiceConnection
	public MySQLContainer<?> mySQLContainer() {
		return new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("blackjack")
			.withUsername("test")
			.withPassword("test")
			.withReuse(true);
	}
	
	@Bean
	@ServiceConnection
	public MongoDBContainer mongoDBContainer() {
		return new MongoDBContainer(DockerImageName.parse("mongo:latest"))
			.withReuse(true);
	}
}
