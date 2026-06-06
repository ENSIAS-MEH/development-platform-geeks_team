package com.techhub.teamservice.dto;

import com.techhub.teamservice.entity.enums.InvitationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbound DTO for invitation data.
 * Returned by GET /api/invitations/my and POST /api/teams/{id}/invite.
 *
 * <p>{@code expirationTime} is included so the frontend can render
 * a countdown timer ("Expires in 46h") without a second API call.
 * {@code isExpired} is a convenience boolean derived from the status
 * to simplify conditional rendering in React.
 */
public record InvitationResponse(

        UUID              id,
        UUID              teamId,
        String            teamName,      // denormalised — avoids frontend join
        UUID              senderId,
        UUID              receiverId,
        InvitationStatus  status,
        Instant           expirationTime,
        boolean           isExpired,     // true when status == EXPIRED or time past
        Instant           createdAt

) {}
