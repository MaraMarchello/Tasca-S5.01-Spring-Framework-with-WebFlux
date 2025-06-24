package com.blackjack.controller;

import com.blackjack.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test Controller", description = "Sample endpoints for testing the API")
public class TestController {

    @Operation(
        summary = "Get test message",
        description = "Returns a simple test message to verify the API is working. " +
                     "This endpoint can be used for health checks and testing.",
        tags = {"test", "health"}
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved test message",
        content = @Content(
            mediaType = "text/plain",
            schema = @Schema(
                type = "string",
                example = "Test endpoint is working!"
            )
        )
    )
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ErrorResponse.class)
        )
    )
    @GetMapping
    public Mono<String> test() {
        return Mono.just("Test endpoint is working!");
    }

    @Operation(
        summary = "Echo message",
        description = "Returns the provided message back to the caller"
    )
    @ApiResponse(responseCode = "200", description = "Message echoed successfully",
        content = @Content(
            mediaType = "text/plain",
            schema = @Schema(
                type = "string",
                example = "Echo: Hello World"
            )
        ))
    @ApiResponse(responseCode = "400", description = "Invalid message provided",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/echo/{message}")
    public Mono<String> echo(
            @Parameter(description = "Message to echo", example = "Hello World")
            @PathVariable("message") String message) {
        return Mono.just("Echo: " + message);
    }
} 