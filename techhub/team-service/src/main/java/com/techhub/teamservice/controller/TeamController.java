package com.techhub.teamservice.controller;

import com.techhub.teamservice.dto.TeamMemberResponse;
import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import com.techhub.teamservice.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(@Valid @RequestBody TeamRequest request) {
        TeamResponse created = teamService.createTeam(request);
        return ResponseEntity.created(URI.create("/teams/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeam(@PathVariable UUID id) {
        return ResponseEntity.ok(teamService.getTeam(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    // ── New endpoints ────────────────────────────────────────────────

    @GetMapping("/my-teams")
    public ResponseEntity<Page<TeamResponse>> getMyTeams(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(teamService.getMyTeams(page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TeamResponse>> searchTeams(
            @RequestParam(defaultValue = "") String query,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(teamService.searchTeams(query, page, size));
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<Page<TeamMemberResponse>> getTeamMembers(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(teamService.getTeamMembers(id, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable UUID id,
            @Valid @RequestBody TeamRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveTeam(@PathVariable UUID id) {
        teamService.leaveTeam(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID id,
            @PathVariable UUID memberId) {
        teamService.removeMember(id, memberId);
        return ResponseEntity.noContent().build();
    }
}

