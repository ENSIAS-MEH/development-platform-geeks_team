package com.projtechhub.techhub.controllers;

import com.projtechhub.techhub.dto.request.ChangePasswordRequest;
import com.projtechhub.techhub.dto.request.PrivacyRequest;
import com.projtechhub.techhub.dto.request.SkillRequest;
import com.projtechhub.techhub.dto.request.UpdateProfileRequest;
import com.projtechhub.techhub.dto.response.UserProfileResponse;
import com.projtechhub.techhub.dto.response.UserResponse;
import com.projtechhub.techhub.dto.response.ChangePasswordResponse;
import com.projtechhub.techhub.repositories.UserRepository;
import com.projtechhub.techhub.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateMyProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String skill,
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.searchBySkill(skill, level, page, size));
    }

    // Returns updated List<String> of all skill names after adding
    // Frontend replaces its skills array with this response — no second GET needed
    @PostMapping("/me/skills")
    public ResponseEntity<List<String>> addSkill(
            @Valid @RequestBody SkillRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addSkill(request));
    }

    // Returns updated List<String> of remaining skills after deletion
    @DeleteMapping("/me/skills/{skillId}")
    public ResponseEntity<List<String>> deleteSkill(@PathVariable UUID skillId) {
        return ResponseEntity.ok(userService.deleteSkill(skillId));
    }

    @PutMapping("/me/password")
    public ResponseEntity<ChangePasswordResponse> updatePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    @PutMapping("/me/privacy")
    public ResponseEntity<Void> updatePrivacy(
            @RequestBody PrivacyRequest request
    ) {
        userService.updatePrivacy(request);
        return ResponseEntity.noContent().build();
    }

    // GET /api/users — returns all users for FindCollaboratorsPage initial load
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }






}