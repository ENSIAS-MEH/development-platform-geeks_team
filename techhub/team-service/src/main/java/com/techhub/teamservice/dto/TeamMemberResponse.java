package com.techhub.teamservice.dto;

import com.techhub.teamservice.entity.enums.MemberRole;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbound DTO for a single team member.
 * Returned by GET /api/teams/{id}/members.
 */
public record TeamMemberResponse(

        UUID        id,
        UUID        teamId,
        UUID        userId,
        MemberRole  role,
        Instant     joinedAt

) {}
