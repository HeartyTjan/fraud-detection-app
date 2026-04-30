package com.interswitch.fraudtransactionapp.controller;

import com.interswitch.fraudtransactionapp.config.JwtUtil;
import com.interswitch.fraudtransactionapp.dto.response.ApiResponse;
import com.interswitch.fraudtransactionapp.model.ApiKey;
import com.interswitch.fraudtransactionapp.service.ApiKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API key management and authentication endpoints")
public class AuthController {

    private final ApiKeyService apiKeyService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create-key")
    @Operation(
            summary = "Create API key",
            description = "Generates a new API key for a given owner name"
    )
    public ResponseEntity<ApiResponse<String>> createKey(
            @Parameter(description = "Name of the API key owner", example = "Fraud Service Client")
            @RequestParam String ownerName) {
        String rawKey = apiKeyService.createApiKey(ownerName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Created successfully", rawKey));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login with API key",
            description = "Authenticates using an API key and returns a JWT token"
    )
    public ResponseEntity<ApiResponse<String>> login(
            @Parameter(description = "API key provided in request header", example = "YOUR_API_KEY_VALUE")
            @RequestHeader("X-API-KEY") String apiKey) {

        ApiKey matchedKey = apiKeyService.validateApiKey(apiKey);
        String token = jwtUtil.generateToken(matchedKey.getOwnerName());
        return ResponseEntity.ok(ApiResponse.success("Login successful!", token));
    }

    @DeleteMapping("/api/auth/revoke-key/{id}")
    @Operation(
            summary = "Revoke API key",
            description = "Revokes an existing API key by its ID"
    )
    public ResponseEntity<ApiResponse<String>> revokeKey(
            @Parameter(description = "ID of the API key to revoke", example = "1")
            @PathVariable Long id) {
        apiKeyService.revokeApiKey(id);
        return ResponseEntity.ok(ApiResponse.success("Revoked successfully"));
    }
}