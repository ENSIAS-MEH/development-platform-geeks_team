package com.projtechhub.techhub.security;

/**
 * @author pc
 **/

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Runs once per request, before any controller.
 *
 * What it does:
 * 1. Reads the Authorization header
 * 2. Extracts and validates the JWT
 * 3. Checks the token is not blacklisted in Redis (logout support)
 * 4. Loads the user and sets SecurityContextHolder
 *
 * What it does NOT do:
 * - It never throws exceptions — if anything is wrong it just doesn't set the context
 *   and lets Spring Security return 401 naturally downstream
 * - It never redirects or writes responses directly
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {



        final String authHeader = request.getHeader("Authorization");

        // No Authorization header or wrong format — skip silently.
        // Spring Security will handle unauthenticated access based on SecurityConfig rules.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7); // strip "Bearer "
        System.out.println(">>> Authorization header: " + authHeader);
        System.out.println(">>> JWT: " + jwt);
        try {

            final String userEmail = jwtService.extractUsername(jwt);

            // Only process if we have an email and no authentication is set yet for this request.
            // The second condition prevents re-processing if a filter earlier in the chain
            // already authenticated this request.
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Check Redis blacklist — if this token was explicitly logged out,
                // reject it even if the signature is still valid.
                String blacklistKey = "auth:blacklist:" + jwt;
                Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    log.debug("Rejected blacklisted token for user: {}", userEmail);
                    filterChain.doFilter(request, response);
                    return;
                }

                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create an authenticated token — this is what Spring Security
                    // uses to know "this request is authenticated as userEmail"
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,                        // credentials null — already verified
                                    userDetails.getAuthorities()
                            );

                    // Attach request details (IP, session) to the authentication
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // This line is what makes the request "authenticated" for everything downstream
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            // Token expired — don't throw, just let the request continue unauthenticated
            // Spring Security will return 401 because SecurityContextHolder was never set
            log.debug("JWT token expired: {}", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // Any JWT parsing error (expired, malformed, wrong signature) lands here.
            // We just log it and continue — Spring Security will return 401 because
            // SecurityContextHolder was never set.
            log.debug("JWT authentication failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}