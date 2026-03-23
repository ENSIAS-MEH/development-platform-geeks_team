package com.projtechhub.techhub.controllers;

import com.projtechhub.techhub.dto.request.LoginRequest;
import com.projtechhub.techhub.dto.request.RegisterRequest;
import com.projtechhub.techhub.dto.response.AuthResponse;
import com.projtechhub.techhub.services.AuthService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles authentication endpoints — all public, no JWT required.
 * SecurityConfig permits /api/auth/** without authentication.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
//@Tag(name = "Authentication", description = "Register, login, logout, token refresh")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
//    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request  // @RequestBody reads the JSON body
    ) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // 201 not 200
    }

    @PostMapping("/login")
//    @Operation(summary = "Login with email and password")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
//    @Operation(summary = "Logout — invalidates the current access token")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        // Strip "Bearer " prefix before passing to service
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.noContent().build(); // 204 — success, no body
    }

    @PostMapping("/refresh")
//    @Operation(summary = "Get a new access token using a refresh token")
    public ResponseEntity<AuthResponse> refresh(
            @RequestHeader("Refresh-Token") String refreshToken
    ) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}