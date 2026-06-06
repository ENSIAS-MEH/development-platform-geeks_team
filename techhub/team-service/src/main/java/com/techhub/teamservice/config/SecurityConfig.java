package com.techhub.teamservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * SecurityConfig
 *
 * <p>Stateless JWT-based security for the Team Service.
 * Sessions are never created — each request is authenticated
 * via the {@link JwtAuthenticationFilter} that extracts the
 * {@code userId} claim from the bearer token and populates
 * the {@link org.springframework.security.core.context.SecurityContext}.
 *
 * <p>Method-level security is enabled via {@code @EnableMethodSecurity}
 * so controllers can use {@code @PreAuthorize("hasRole('ADMIN')")} etc.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint authEntryPoint;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOrigins;

    // ─────────────────────────── Security Filter Chain ─────────────────

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // ── Disable CSRF — stateless REST API ──────────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── CORS — allow frontend origin ────────────────────────
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ── Session — always stateless ──────────────────────────
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ── Exception handling ──────────────────────────────────
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(authEntryPoint))

                // ── Authorization rules ─────────────────────────────────
                .authorizeHttpRequests(auth -> auth

                        // Actuator — no auth needed for K8s probes and Prometheus scraping
                        .requestMatchers("/actuator/health/**", "/actuator/info",
                                         "/actuator/prometheus").permitAll()

                        // Swagger UI / OpenAPI docs — dev only
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Public read-only endpoints
                        .requestMatchers(HttpMethod.GET, "/teams/{id}").permitAll()

                        // All other requests require a valid JWT
                        .anyRequest().authenticated()
                )

                // ── Insert JWT filter before standard username/password filter ──
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    // ─────────────────────────── CORS ────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        config.setAllowedOriginPatterns(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setExposedHeaders(List.of("X-Total-Count"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

