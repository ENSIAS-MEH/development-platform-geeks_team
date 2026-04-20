package com.projtechhub.techhub.services;

import com.projtechhub.techhub.dto.request.ChangePasswordRequest;
import com.projtechhub.techhub.dto.request.PrivacyRequest;
import com.projtechhub.techhub.dto.request.SkillRequest;
import com.projtechhub.techhub.dto.request.UpdateProfileRequest;
import com.projtechhub.techhub.dto.response.ChangePasswordResponse;
import com.projtechhub.techhub.dto.response.UserProfileResponse;
import com.projtechhub.techhub.dto.response.UserResponse;
import com.projtechhub.techhub.dto.response.UserSummaryDTO;
import com.projtechhub.techhub.entities.Skill;
import com.projtechhub.techhub.entities.User;
import com.projtechhub.techhub.entities.UserProfile;
import com.projtechhub.techhub.exceptions.ResourceNotFoundException;
import com.projtechhub.techhub.repositories.SkillRepository;
import com.projtechhub.techhub.repositories.UserProfileRepository;
import com.projtechhub.techhub.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    // ── Helper ────────────────────────────────────────────────────────────

    private User getCurrentUser() {
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        System.out.println("getCurrentUser email: '" + email + "'");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    // ── GET /api/users/me ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        User user = getCurrentUser();
        UserProfile profile = userProfileRepository.findByUser_Id(user.getId())
                .orElse(null);
        return buildUserProfileResponse(user, profile);
    }

    // ── PUT /api/users/me ─────────────────────────────────────────────────

    @Transactional
    @CacheEvict(cacheNames = "users", key = "#result.id")
    public UserProfileResponse updateMyProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();

        if (request.getName() != null)      user.setDisplayName(request.getName());
        if (request.getBio() != null)        user.setBio(request.getBio());
        if (request.getLocation() != null)   user.setLocation(request.getLocation());
        if (request.getAvatarUrl() != null)  user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findByUser_Id(user.getId())
                .orElse(UserProfile.builder().user(user).build());

        if (request.getHeadline() != null)     profile.setHeadline(request.getHeadline());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getGithubUrl() != null)    profile.setGithubUrl(request.getGithubUrl());
        if (request.getLinkedinUrl() != null)  profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getWebsiteUrl() != null)   profile.setWebsiteUrl(request.getWebsiteUrl());
        userProfileRepository.save(profile);

        return buildUserProfileResponse(user, profile);
    }

    // ── GET /api/users/{id} ───────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "users", key = "#id")
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return buildUserResponse(user);
    }

    // ── GET /api/users/search ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<UserResponse> searchBySkill(String skill, String level, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
//        String levelParam = (level == null || level.isBlank()) ? null : level.toUpperCase();
        return userRepository.searchBySkill(skill, pageable)
                .map(this::buildUserResponse);
    }

    // ── POST /api/users/me/skills ─────────────────────────────────────────
    // Returns List<String> — just the updated full list of skill names
    // so the frontend can replace its local state in one shot
    private String normalizeSkillName(String name) {
        if (name == null || name.isBlank()) return name;
        String trimmed = name.trim();
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1).toLowerCase();
    }
    @Transactional
    public List<String> addSkill(SkillRequest request) {
        User user = getCurrentUser();

        Skill skill = Skill.builder()
                .user(user)
                .name(normalizeSkillName(request.getName()))  // trim whitespace
                .level(Skill.Level.BEGINNER)
                .build();

        try {
            skillRepository.saveAndFlush(skill);  // saveAndFlush forces immediate DB write
            // so the constraint violation happens HERE
            // not later when the transaction commits
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException(
                    "Skill '" + request.getName() + "' already exists"
            );
        }

        return skillRepository.findByUser_Id(user.getId())
                .stream()
                .map(Skill::getName)
                .toList();
    }
    // ── DELETE /api/users/me/skills/{skillId} ─────────────────────────────
    // Returns updated list of skill names after deletion

    @Transactional
    public List<String> deleteSkill(UUID skillId) {
        User user = getCurrentUser();

        Skill skill = skillRepository.findByIdAndUser_Id(skillId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found"));

        skillRepository.delete(skill);
        skillRepository.flush();

        return skillRepository.findByUser_Id(user.getId())
                .stream()
                .map(Skill::getName)
                .toList();
    }

    // ── DTO builders ──────────────────────────────────────────────────────

    public UserResponse buildUserResponse(User user) {
        // skills is just List<String> — names only
        List<String> skillNames = user.getSkills() != null
                ? user.getSkills().stream().map(Skill::getName).toList()
                : List.of();

        return UserResponse.builder()
                .id(user.getId().toString())
                .name(user.getDisplayName())
                .email(user.getShowEmail() != null && user.getShowEmail()? user.getEmail() : null)
                .role(mapUserType(user.getUserType()))
                .skills(skillNames)
                .location(user.getLocation())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .joinedAt(user.getCreatedAt() != null
                        ? user.getCreatedAt().toInstant().toString()
                        : null)
                .build();
    }

    private UserProfileResponse buildUserProfileResponse(User user, UserProfile profile) {
        // Same as UserResponse but with extra profile fields
        // skills still List<String> — consistent with everywhere else
        List<String> skillNames = (user.getSkills() != null && !user.getSkills().isEmpty())
                ? user.getSkills().stream().map(Skill::getName).toList()
                : List.of();

        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .name(user.getDisplayName())
                .email(user.getEmail())
                .showEmail(user.getShowEmail() != null ? user.getShowEmail() : false)  // include the setting so frontend can display the toggle state
                .authProvider(user.getAuthProvider() != null ? user.getAuthProvider() : "local")

                .role(mapUserType(user.getUserType()))
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .location(user.getLocation())
                .joinedAt(user.getCreatedAt() != null
                        ? user.getCreatedAt().toInstant().toString()
                        : null)
                .skills(skillNames)
                .headline(profile != null ? profile.getHeadline() : null)
                .portfolioUrl(profile != null ? profile.getPortfolioUrl() : null)
                .githubUrl(profile != null ? profile.getGithubUrl() : null)
                .linkedinUrl(profile != null ? profile.getLinkedinUrl() : null)
                .websiteUrl(profile != null ? profile.getWebsiteUrl() : null)
                .build();
    }


    private String mapUserType(com.projtechhub.techhub.entities.UserType userType) {
        if (userType == null) return "Developer";
        return switch (userType) {
            case STUDENT   -> "Student";
            case DEVELOPER -> "Developer";
            case ORGANIZER -> "Organizer";
            case COMPANY   -> "Company";
        };
    }


    public ChangePasswordResponse updatePassword(@Valid ChangePasswordRequest request) {
        User user = getCurrentUser();

        // Step 1 — verify current password matches what's stored
        // BCrypt.matches(rawPassword, hashedPassword) — never compare plain text
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Step 2 — verify new password and confirm match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirmation do not match");
        }

        // Step 3 — hash the new password and save
        // passwordEncoder.encode() runs BCrypt — never store plain text
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ChangePasswordResponse.builder()
                .message("Password updated successfully")
                .build();
    }

    public void updatePrivacy(PrivacyRequest request) {
        User user = getCurrentUser();
        if (request.getShowEmail() != null) {
            user.setShowEmail(request.getShowEmail());
        }
        userRepository.save(user);
    }


    public Page<UserSummaryDTO> getAllUsersExcept(String userEmail, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllExcept(userEmail, pageable)
                .map(this::mapToSummaryDTO); // Page has its own .map()
    }

    private UserSummaryDTO mapToSummaryDTO(User u) {
        return UserSummaryDTO.builder()
                .id(u.getId())
                .name(u.getDisplayName())
                .userType(String.valueOf(u.getUserType()))
                .bio(u.getBio())
                .location(u.getLocation())
                .skills(u.getSkills())
                .build();
    }
}