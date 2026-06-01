package com.techhub.teamservice.dto;

import java.time.Instant;
import java.util.UUID;

public record InvitationResponse(
        UUID id,
        UUID teamId,
        UUID senderId,
        UUID receiverId,
        String status,
        Instant expirationTime,
        Instant createdAt,
        Instant updatedAt
) {}

