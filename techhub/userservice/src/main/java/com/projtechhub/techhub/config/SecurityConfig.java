package com.projtechhub.techhub.config;

/**
 * @author pc
 **/


import com.projtechhub.techhub.security.JwtAuthenticationFilter;
import com.projtechhub.techhub.security.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central security configuration.
 *
 * Key decisions made here:
 * - Session is STATELESS — no cookies, no server-side session, JWT only
 * - CSRF disabled — not needed for stateless REST APIs
 * - Our JwtAuthenticationFilter runs before Spring's default auth filter
 * - CORS configured here so it applies to all requests including preflight OPTIONS
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // enables @PreAuthorize on methods if needed later
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                // Disable CSRF — not needed for stateless REST APIs.
//                // CSRF protects cookie-based sessions; we use JWT in headers.
//                .csrf(AbstractHttpConfigurer::disable)
//
//                // CORS — allow frontend origin to call us
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//
//                // Route access rules
//                .authorizeHttpRequests(auth -> auth
//
//                        // Public — no token needed
//                        .requestMatchers(
//                                "/api/auth/**",           // login, register, refresh
//                                "/swagger-ui/**",         // Swagger UI
//                                "/swagger-ui.html",
//                                "/v3/api-docs/**",        // OpenAPI spec
//                                "/actuator/health"        // health check
//                        ).permitAll()
//
//                        // Everything else requires a valid JWT
//                        .anyRequest().authenticated()
//                )
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/logout"
                        ).permitAll()
                        .anyRequest().permitAll()
                )

                // No server-side session — every request is independent
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Wire our custom auth provider (uses UserDetailsServiceImpl + BCrypt)
                .authenticationProvider(authenticationProvider())

                // Our JWT filter runs before Spring's default username/password filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Tells Spring Security how to authenticate users:
     * - Load user by email via UserDetailsServiceImpl
     * - Verify password with BCrypt
     *
     * AuthenticationManager calls this internally during login.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager is what AuthService calls directly to authenticate
     * a login attempt — authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password))
     * It internally uses the AuthenticationProvider above.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCrypt with strength 12 — strong enough for production, not too slow for dev.
     * Strength 10 is minimum acceptable, 12 is the recommended default.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * CORS configuration — allows the React frontend to call this API.
     * During development the frontend runs on localhost:5173 (Vite default).
     *
     * In production replace localhost:5173 with your actual frontend domain.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173",   // Vite dev server
                "http://localhost:3000"    // in case they use CRA or another port
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-User-Id",       // sent by gateway to downstream services
                "X-User-Roles"     // sent by gateway to downstream services
        ));

        // Allow frontend to read the Authorization header from responses
        config.setExposedHeaders(List.of("Authorization"));

        // Allow cookies if needed (e.g. for refresh token in httpOnly cookie)
        config.setAllowCredentials(true);

        config.setMaxAge(3600L); // cache preflight response for 1 hour

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}