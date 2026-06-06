package com.techhub.teamservice.util;

import com.techhub.teamservice.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

/**
 * SecurityUtils
 *
 * <p>Utility for extracting the authenticated user's ID from the
 * Spring {@link org.springframework.security.core.context.SecurityContext}
 * without passing request objects through service layers (violates SRP).
 *
 * <p>The principal is set as a {@code String} (UUID) by
 * {@link com.techhub.teamservice.config.JwtAuthenticationFilter}.
 */
public final class SecurityUtils {

    // Utility class — prevent instantiation
    private SecurityUtils() {}

    /**
     * Returns the UUID of the currently authenticated user.
     *
     * @return authenticated user's {@link UUID}
     * @throws UnauthorizedException if no authentication is present
     */
    public static UUID getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("No authenticated user found in SecurityContext");
        }

        try {
            return UUID.fromString(authentication.getPrincipal().toString());
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Invalid user ID format in JWT principal");
        }
    }

    /**
     * Returns the UUID of the authenticated user, or {@code null} if the request
     * is unauthenticated. Use this for endpoints that are publicly accessible but
     * enrich the response when a caller is logged in (e.g. setting isOwner flag).
     */
    public static UUID getCurrentUserIdOrNull() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        try {
            return UUID.fromString(authentication.getPrincipal().toString());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Returns {@code true} if the current user holds the given role.
     * Role names are compared WITHOUT the {@code ROLE_} prefix.
     */
    public static boolean hasRole(String role) {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}

