package com.techhub.teamservice.dto;

import java.time.Instant;
import java.util.UUID;

public record TeamResponse(
        UUID id,
        String name,
        String description,
        Integer maxMembers,
        Integer currentMembers,
        String status,
        UUID ownerId,
        Instant createdAt,
        Instant updatedAt
) {}

