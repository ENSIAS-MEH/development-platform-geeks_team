package com.projtechhub.techhub.services;

import com.projtechhub.techhub.dto.request.LoginRequest;
import com.projtechhub.techhub.dto.request.RegisterRequest;
import com.projtechhub.techhub.dto.response.AuthResponse;
import com.projtechhub.techhub.dto.response.UserResponse;
import com.projtechhub.techhub.entities.*;
import com.projtechhub.techhub.exceptions.EmailAlreadyExistsException;
import com.projtechhub.techhub.kafka.event.UserRegistredEvent;
import com.projtechhub.techhub.kafka.producer.MessageProducer;
import com.projtechhub.techhub.repositories.*;
import com.projtechhub.techhub.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * It Handles all authentication logic — registration, login, token refresh, logout.
 */
@Service
@RequiredArgsConstructor  // Lombok generates constructor — injects all final fields
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefreshTokensRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;          // injected from SecurityConfig bean
    private final JwtService jwtService;                   // your security/JwtService.java
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;
    //add kafka message producer
    private final MessageProducer messageProducer;


    // ── Register ───────────────────────────────────────────────────────

    @Transactional  // if anything fails, the whole thing rolls back — no partial saves
    public AuthResponse register(RegisterRequest request) {

        // 1. Check email not already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already in use: " + request.getEmail()
            );
        }

        // 2. Map role string to enum — "Event Organizer" → ORGANIZER etc.
        UserType userType = mapUserType(request.getRole());

        // 3. Build and save the User — use builder pattern, not setters one by one
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getName())
                .userType(userType)
                .enabled(true)
                .emailVerified(false)
                .bio("")
                .location("")
                .avatarUrl("")
                .build();

        userRepository.save(user);

        // 4. Save default role
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(UserRole.Role.ROLE_USER)
                .build();
        userRoleRepository.save(userRole);

        // 5. Create empty profile — will be filled in by user later
        UserProfile profile = UserProfile.builder()
                .user(user)
                .portfolioUrl("")
                .githubUrl("")
                .linkedinUrl("")
                .websiteUrl("")
                .headline("")
                .build();
        userProfileRepository.save(profile);

//        //add kafka event
        UserRegistredEvent event = UserRegistredEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("user registered")
                .timestamp(Instant.now().toString())
                .userId(user.getId())
                .role(request.getRole())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .build();
        messageProducer.publishUserRegistredEvent(event);
        // 6. Generate tokens and return
        return buildAuthResponse(user);
    }

    // ── Login ─────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        // This does two things in one call:
        // - loads the user via UserDetailsServiceImpl
        // - verifies the password with BCrypt
        // - throws BadCredentialsException automatically if wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // If we reach here, credentials were correct
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        return buildAuthResponse(user);
    }

    // ── Logout ────────────────────────────────────────────────────────────

    /**
     * Blacklists the access token in Redis so it can't be used again
     * even though the signature is still technically valid.
     * Also revokes all refresh tokens for this user.
     */
    public void logout(String accessToken) {
        long ttlMillis = jwtService.extractRemainingTtlMillis(accessToken);

        if (ttlMillis > 0) {
            redisTemplate.opsForValue().set(
                    "auth:blacklist:" + accessToken,
                    "1",
                    ttlMillis,
                    TimeUnit.MILLISECONDS
            );
        }

        // Revoke all refresh tokens for this user
        String email = jwtService.extractUsername(accessToken);
        userRepository.findByEmail(email).ifPresent(user ->
                refreshTokenRepository.revokeAllByUserId(user.getId())
        );
    }

    // ── Token refresh ─────────────────────────────────────────────────────

    public AuthResponse refreshToken(String refreshToken) {
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new RuntimeException("Refresh token is invalid or expired");
        }

        // Generate new access token only — keep the same refresh token
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)   // same refresh token returned
                .tokenType("Bearer")
                .expiresIn(86400L)
                .user(buildUserResponse(user))
                .build();
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Builds the AuthResponse returned on login and register.
     * Generates both tokens and packages the user response.
     */
    private AuthResponse buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", user.getId().toString());
        String accessToken = jwtService.generateAccessToken(extraClaims, userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Save hashed refresh token to DB for rotation/revocation
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(OffsetDateTime.now().plus(7, ChronoUnit.DAYS).toInstant())
                .revoked(false)
                .build();
        refreshTokenRepository.save(token);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(86400L)            // 24h in seconds
                .user(buildUserResponse(user))
                .build();
    }

    /**
     * Maps the frontend role string to your UserType enum.
     * The frontend sends "Student", "Developer", "Event Organizer", "Company".
     */
    private UserType mapUserType(String role) {
        return switch (role) {
            case "Student"         -> UserType.STUDENT;
            case "Developer"       -> UserType.DEVELOPER;
            case "Organizer"       -> UserType.ORGANIZER;
            case "Company"         -> UserType.COMPANY;
            default                -> UserType.DEVELOPER; // safe fallback
        };
    }

    /**
     * Builds the UserResponse DTO from a User entity.
     * This is what gets embedded inside AuthResponse and returned to the frontend.
     */
    private UserResponse buildUserResponse(User user) {
        List<String> skills = user.getSkills() != null
                ? user.getSkills().stream().map(Skill::getName).toList()
                : List.of();

        return UserResponse.builder()
                .id(user.getId().toString())
                .name(user.getDisplayName())
                .email(user.getEmail())
                .role(mapUserTypeToFrontendRole(user.getUserType()))
                .skills(skills)
                .location(user.getLocation())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .joinedAt(user.getCreatedAt().toInstant().toString())
                .build();
    }

    /**
     * Converts internal UserType enum to the string the frontend expects.
     */
    private String mapUserTypeToFrontendRole(UserType userType) {
        return switch (userType) {
            case STUDENT   -> "Student";
            case DEVELOPER -> "Developer";
            case ORGANIZER -> "Organizer";
            case COMPANY   -> "Company";
        };
    }

    /**
     * Simple SHA-256 hash of the raw refresh token for safe DB storage.
     * We never store raw tokens.
     */
    private String hashToken(String rawToken) {
        try {
            java.security.MessageDigest digest =
                    java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    rawToken.getBytes(java.nio.charset.StandardCharsets.UTF_8)
            );
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}