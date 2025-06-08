package com.blackjack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Error response object")
public class ErrorResponse {
    
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    
    @Schema(description = "Error type", example = "BAD_REQUEST")
    private String error;
    
    @Schema(description = "Error message", example = "Validation failed")
    private String message;
    
    @Schema(description = "Timestamp of the error", example = "2023-12-08T15:30:45")
    private LocalDateTime timestamp;
    
    @Schema(description = "Request path where error occurred", example = "/api/players")
    private String path;
    
    @Schema(description = "Detailed validation errors")
    private List<String> details;
    
    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
} 