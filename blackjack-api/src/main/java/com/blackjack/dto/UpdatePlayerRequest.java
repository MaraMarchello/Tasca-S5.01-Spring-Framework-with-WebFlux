package com.blackjack.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for updating player information")
public class UpdatePlayerRequest {
    
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Schema(description = "Updated username", example = "johnsmith_new")
    private String username;
    
    @Email(message = "Email must be valid")
    @Schema(description = "Updated email address", example = "john.smith.new@example.com")
    private String email;
} 