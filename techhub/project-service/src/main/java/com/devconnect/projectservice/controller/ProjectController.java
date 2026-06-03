package com.devconnect.projectservice.controller;

import com.devconnect.projectservice.dto.*;
import com.devconnect.projectservice.enums.*;
import com.devconnect.projectservice.service.ProjectService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project collaboration: create, join, match, comment")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create a new project", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Project created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT token")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody CreateProjectRequest req,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(req, userId));
    }

    @GetMapping
    @Operation(summary = "Search/list projects with filters")
    @ApiResponse(responseCode = "200", description = "Paginated list of projects")
    public ResponseEntity<Page<ProjectResponse>> searchProjects(
            @Parameter(description = "Filter by project type") @RequestParam(required = false) ProjectType type,
            @Parameter(description = "Filter by project status") @RequestParam(required = false) ProjectStatus status,
            @Parameter(description = "Search keyword in title") @RequestParam(required = false) String keyword,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        return ResponseEntity.ok(projectService.searchProjects(type, status, keyword, PageRequest.of(page, size)));
    }

    @GetMapping("/matching")
    @Operation(summary = "Get projects matching current user's skill profile", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Matching projects")
    public ResponseEntity<List<MatchingProjectResponse>> getMatchingProjects(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(projectService.getMatchingProjectsForUser(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    @ApiResponse(responseCode = "200", description = "Project found")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectResponse> getProject(
            @PathVariable UUID id,
            @RequestHeader(value = "X-User-Id", required = false) UUID userId) {
        return ResponseEntity.ok(projectService.getProjectById(id, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update project (owner only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Project updated")
    @ApiResponse(responseCode = "403", description = "Not the owner")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateProjectRequest req,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(projectService.updateProject(id, req, userId));
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "Join a project", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Joined successfully")
    @ApiResponse(responseCode = "409", description = "Already a member")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<ProjectMemberResponse> joinProject(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.joinProject(id, userId));
    }

    @DeleteMapping("/{id}/leave")
    @Operation(summary = "Leave a project", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "204", description = "Left project")
    @ApiResponse(responseCode = "404", description = "Not a member")
    public ResponseEntity<Void> leaveProject(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        projectService.leaveProject(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    @Operation(summary = "List project members")
    @ApiResponse(responseCode = "200", description = "Member list")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<List<ProjectMemberResponse>> getMembers(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getMembers(id));
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment (members only)", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "Comment added")
    @ApiResponse(responseCode = "403", description = "Not a member")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable UUID id,
            @Valid @RequestBody CommentRequest req,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.addComment(id, userId, req));
    }

    @GetMapping("/{id}/comments")
    @Operation(summary = "List project comments")
    @ApiResponse(responseCode = "200", description = "Paginated comments")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable UUID id,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(projectService.getComments(id, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}/matching-users")
    @Operation(summary = "Get users whose skills match this project's needs")
    @ApiResponse(responseCode = "200", description = "Matching user IDs")
    @ApiResponse(responseCode = "404", description = "Project not found")
    public ResponseEntity<List<UUID>> getMatchingUsers(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getMatchingUsersForProject(id));
    }
}
