package com.projtechhub.techhub.security;

/**
 * @author pc
 **/

import com.projtechhub.techhub.entities.User;
import com.projtechhub.techhub.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Security calls this to load a user by username (email in our app).
 * It's called in two places:
 * 1. During login — AuthenticationManager uses it to verify credentials
 * 2. In JwtAuthenticationFilter — to reload the user from DB after token validation
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * @Transactional needed here if you have lazy-loaded collections on User.
     * Even if not needed now, it's safe to have and prevents surprises later.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        // Map our simple enabled flag + hardcoded role to Spring Security authorities.
        // Everyone is ROLE_USER unless we later add ROLE_ADMIN separately.
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getEnabled())
                .build();
    }
}