package com.blackjack.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
class TestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testEndpoint_ShouldReturnSuccessMessage() {
        webTestClient.get()
                .uri("/api/test")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Test endpoint is working!");
    }
} 