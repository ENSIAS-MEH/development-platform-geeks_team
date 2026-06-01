package com.techhub.teamservice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * JwtUtil
 *
 * <p>Stateless helper for parsing and validating JWTs issued by the
 * User Service. This service never <em>creates</em> tokens — it only
 * reads them. The secret key must match what User Service uses.
 */
@Slf4j
@Component
public class JwtUtil {

    private final SecretKey signingKey;

    public JwtUtil(@Value("${security.jwt.secret}") String secret) {
        // Derive a 256-bit key from the configured secret
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ─────────────────────────── Public API ──────────────────────────────

    /**
     * Validates the token signature and expiry.
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Extracts the subject (userId) from the token.
     */
    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extracts roles from the {@code roles} claim.
     * Returns an empty list if the claim is absent.
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        final Object roles = parseClaims(token).get("roles");
        if (roles instanceof List<?>) {
            return (List<String>) roles;
        }
        return Collections.emptyList();
    }

    // ─────────────────────────── Internals ───────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

