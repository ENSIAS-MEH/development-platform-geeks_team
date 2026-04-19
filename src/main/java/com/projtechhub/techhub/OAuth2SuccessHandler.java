package com.projtechhub.techhub;

import com.projtechhub.techhub.entities.User;
import com.projtechhub.techhub.entities.UserType;
import com.projtechhub.techhub.repositories.UserRepository;
import com.projtechhub.techhub.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final OAuth2AuthorizedClientService clientService;

    // ── THIS is the method Spring OAuth2 actually calls ───────────────────
    // The FilterChain override is for servlet filters — NOT for OAuth2 success
    // All logic must be here
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("OAuth2 success handler triggered");

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = token.getPrincipal().getAttributes();
        String provider = token.getAuthorizedClientRegistrationId();

        log.info("OAuth2 provider: {}", provider);
        log.info("OAuth2 attributes: {}", attributes);

        // Extract email — GitHub sometimes hides it, so we have a fallback
        String email = extractEmail(attributes, provider, token);
        log.info("Extracted email: {}", email);

        if (email == null) {
            log.warn("No email found from OAuth2 provider: {}", provider);
            response.sendRedirect("http://localhost:5173/auth/login?error=no_email");
            return;
        }

        // Extract name — GitHub uses "name", Google uses "name" too
        // GitHub login (username) used as fallback if name is null
        String name = (String) attributes.get("name");
        if (name == null) {
            name = (String) attributes.get("login"); // GitHub username
        }
        if (name == null) {
            name = email.split("@")[0]; // last resort fallback
        }

        // Find existing user or create new one
        final String finalName = name;
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, finalName));

        log.info("User found/created: {}", user.getEmail());

        // Generate YOUR JWT — from this point the frontend works exactly
        // like a normal email/password login
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String jwt = jwtService.generateAccessToken(userDetails);

        log.info("JWT generated, redirecting to frontend callback");

        // Redirect to frontend callback page with token in query param
        // The frontend reads this token, stores it in localStorage, navigates to dashboard
        response.sendRedirect("http://localhost:5173/auth/callback?token=" + jwt);
    }

    // ── Extract email depending on provider ───────────────────────────────

    private String extractEmail(
            Map<String, Object> attributes,
            String provider,
            OAuth2AuthenticationToken token
    ) {
        if ("github".equals(provider)) {
            // GitHub may return null email if user has set email to private
            String email = (String) attributes.get("email");
            if (email != null && !email.isBlank()) {
                return email;
            }
            // Fallback — call GitHub API to get primary verified email
            log.info("GitHub email not in attributes, fetching from GitHub API");
            return fetchGitHubEmail(token);
        }

        // Google always returns email in attributes
        return (String) attributes.get("email");
    }

    // ── Fetch email from GitHub API (when user has private email) ─────────

    private String fetchGitHubEmail(OAuth2AuthenticationToken token) {
        try {
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                    token.getAuthorizedClientRegistrationId(),
                    token.getName()
            );

            if (client == null || client.getAccessToken() == null) {
                log.warn("No authorized client found for GitHub");
                return null;
            }

            String accessToken = client.getAccessToken().getTokenValue();

            RestTemplate rest = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.set("Accept", "application/vnd.github.v3+json");

            ResponseEntity<List<Map<String, Object>>> resp = rest.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );

            if (resp.getBody() == null) return null;

            // Find primary verified email
            return resp.getBody().stream()
                    .filter(e -> Boolean.TRUE.equals(e.get("primary"))
                            && Boolean.TRUE.equals(e.get("verified")))
                    .map(e -> (String) e.get("email"))
                    .findFirst()
                    .orElse(null);

        } catch (Exception e) {
            log.error("Failed to fetch GitHub email: {}", e.getMessage());
            return null;
        }
    }

    // ── Create new user for first-time OAuth login ────────────────────────

    private User createNewUser(String email, String name) {
        log.info("Creating new OAuth2 user: {}", email);
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("OAUTH2_NO_PASSWORD"); // never used for login
        user.setDisplayName(name);
        user.setEnabled(true);
        user.setEmailVerified(true);  // provider already verified their email
        user.setUserType(UserType.DEVELOPER); // default — user can change in settings
        return userRepository.save(user);
    }
}