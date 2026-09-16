package com.techhub.teamservice.controller;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;
import com.techhub.teamservice.service.InvitationService;
import com.techhub.teamservice.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<InvitationResponse> invite(@Valid @RequestBody InvitationRequest request) {
        InvitationResponse created = invitationService.invite(request, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(created);
    }

    @PostMapping("/{id}:accept")
    public ResponseEntity<InvitationResponse> accept(@PathVariable UUID id) {
        InvitationResponse resp = invitationService.accept(id, SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<List<InvitationResponse>> listPending(@PathVariable UUID userId) {
        return ResponseEntity.ok(invitationService.listPendingForUser(userId));
    }
}

