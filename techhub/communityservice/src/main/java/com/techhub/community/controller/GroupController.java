package com.techhub.community.controller;

import com.techhub.community.dto.*;
import com.techhub.community.enums.MemberRole;
import com.techhub.community.enums.Topic;
import com.techhub.community.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Groups", description = "Community group management endpoints")
public class GroupController {

    private final GroupService groupService;

    // ─── Group CRUD ─────────────────────────────────────────────────────

    @PostMapping
    @Operation(summary = "Create a new community group")
    public ResponseEntity<GroupResponse> createGroup(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody GroupRequest request) {
        GroupResponse group = groupService.createGroup(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(group);
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "Get group details by ID")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable UUID groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @GetMapping
    @Operation(summary = "Browse public groups, optionally filtered by topic")
    public ResponseEntity<Page<GroupResponse>> getGroups(
            @RequestParam(required = false) Topic topic,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(groupService.getPublicGroups(topic, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search groups by name keyword")
    public ResponseEntity<Page<GroupResponse>> searchGroups(
            @RequestParam String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(groupService.searchGroups(keyword, pageable));
    }

    @PutMapping("/{groupId}")
    @Operation(summary = "Update group details (owner or moderator)")
    public ResponseEntity<GroupResponse> updateGroup(
            @PathVariable UUID groupId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody GroupRequest request) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, userId, request));
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "Delete a group (owner only)")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable UUID groupId,
            @RequestHeader("X-User-Id") UUID userId) {
        groupService.deleteGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    // ─── Membership ─────────────────────────────────────────────────────

    @PostMapping("/{groupId}/join")
    @Operation(summary = "Join a group")
    public ResponseEntity<GroupMemberResponse> joinGroup(
            @PathVariable UUID groupId,
            @RequestHeader("X-User-Id") UUID userId) {
        GroupMemberResponse member = groupService.joinGroup(groupId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @DeleteMapping("/{groupId}/leave")
    @Operation(summary = "Leave a group")
    public ResponseEntity<Void> leaveGroup(
            @PathVariable UUID groupId,
            @RequestHeader("X-User-Id") UUID userId) {
        groupService.leaveGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/members")
    @Operation(summary = "List members of a group")
    public ResponseEntity<Page<GroupMemberResponse>> getMembers(
            @PathVariable UUID groupId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(groupService.getMembers(groupId, pageable));
    }

    @PutMapping("/{groupId}/members/{targetUserId}/role")
    @Operation(summary = "Update a member's role (owner/moderator only)")
    public ResponseEntity<GroupMemberResponse> updateMemberRole(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserId,
            @RequestHeader("X-User-Id") UUID requesterId,
            @RequestParam MemberRole role) {
        return ResponseEntity.ok(groupService.updateMemberRole(groupId, targetUserId, requesterId, role));
    }

    @DeleteMapping("/{groupId}/members/{targetUserId}")
    @Operation(summary = "Remove a member from a group (owner/moderator only)")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID groupId,
            @PathVariable UUID targetUserId,
            @RequestHeader("X-User-Id") UUID requesterId) {
        groupService.removeMember(groupId, targetUserId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
