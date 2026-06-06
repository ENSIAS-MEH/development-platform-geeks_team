package com.techhub.teamservice.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Inbound DTO for sending a team invitation.
 * Posted to POST /api/invitations.
 */
public record InvitationRequest(

        @NotNull(message = "Team ID is required")
        UUID teamId,

        @NotNull(message = "Receiver user ID is required")
        UUID receiverId

) {}
