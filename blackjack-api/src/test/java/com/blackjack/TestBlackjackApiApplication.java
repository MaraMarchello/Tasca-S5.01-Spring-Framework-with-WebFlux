package com.blackjack;

import org.springframework.boot.SpringApplication;

public class TestBlackjackApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(BlackjackApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
