package com.blackjack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .openapi("3.0.1")
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"))
                .info(new Info()
                        .title("Blackjack API")
                        .version("1.0.0")
                        .description("REST API for Blackjack game with comprehensive documentation")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact()
                                .name("Blackjack Team")
                                .email("support@blackjack.com")
                                .url("https://blackjack.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Blackjack Game Documentation")
                        .url("https://blackjack.com/docs"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
} 