package com.techhub.teamservice.dto;

import com.techhub.teamservice.entity.enums.TeamStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Outbound DTO for team data returned to the frontend.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>{@code remainingSlots} is computed here (not on the entity) so the
 *       frontend never has to calculate it — avoids inconsistency.</li>
 *   <li>{@code isOwner} is a derived field injected by the service layer
 *       based on the authenticated user ID — avoids a second API call
 *       from the frontend to determine edit/delete button visibility.</li>
 *   <li>No internal fields (DB ids of other entities, audit timestamps
 *       beyond createdAt) are exposed.</li>
 * </ul>
 */
public record TeamResponse(

        UUID        id,
        String      name,
        String      description,
        Integer     maxMembers,
        Integer     currentMembers,
        Integer     remainingSlots,   // maxMembers - currentMembers (computed by mapper)
        TeamStatus  status,
        UUID        ownerId,
        boolean     isOwner,          // true when caller is the owner (set by service)
        Instant     createdAt

) {}
