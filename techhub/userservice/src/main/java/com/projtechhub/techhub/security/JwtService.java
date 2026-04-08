package com.projtechhub.techhub.security;

/**
 * @author pc
 **/

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Handles all JWT operations — generation, validation, and claim extraction.
 * This class has no Spring dependencies beyond @Value — it is pure logic.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long accessTokenExpiration;   // milliseconds — e.g. 86400000 = 24h

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;  // milliseconds — e.g. 604800000 = 7 days

    // ── Token generation ──────────────────────────────────────────────────

    /**
     * Generates an access token for the given user.
     * The subject is the user's email — used to reload UserDetails on each request.
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates an access token with extra claims embedded in the payload.
     * Use this to embed userId, role, etc. so downstream services can read them
     * from the token without hitting the database.
     */
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())   // username = email in our app
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Generates a refresh token — longer lived, no extra claims needed.
     * We also embed a unique jti (JWT ID) so each refresh token is unique
     * even for the same user — prevents replay after rotation.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .id(UUID.randomUUID().toString())     // jti claim — unique per token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSignKey())
                .compact();
    }

    // ── Token validation ──────────────────────────────────────────────────

    /**
     * Returns true if the token is valid for the given user:
     * - subject matches the user's username (email)
     * - token is not expired
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ── Claim extraction ──────────────────────────────────────────────────

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Returns remaining lifetime of token in milliseconds.
     * Used when storing token in Redis blacklist — TTL should match
     * so the blacklist entry expires exactly when the token would have anyway.
     */
    public long extractRemainingTtlMillis(String token) {
        Date expiration = extractExpiration(token);
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return Math.max(remaining, 0);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ── Key ───────────────────────────────────────────────────────────────

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}