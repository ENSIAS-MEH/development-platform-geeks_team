package com.techhub.teamservice.mapper;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;
import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamInvitation;
import com.techhub.teamservice.entity.enums.InvitationStatus;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for {@link TeamInvitation} ↔ DTO conversions.
 *
 * <p>Fix applied: when mapping from two sources (invitation + team),
 * every target field must declare its source explicitly via
 * {@code source = "invitation.fieldName"} to avoid the
 * "Several possible source properties" compile error.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface InvitationMapper {

    // ── Single-source mapping (no ambiguity) ──────────────────────

    /**
     * Maps invitation only — teamName will be null.
     * Use for lightweight listings where team data is not loaded.
     */
    @Mapping(target = "teamName",  ignore = true)
    @Mapping(target = "isExpired", expression = "java(computeIsExpired(invitation))")
    InvitationResponse toResponse(TeamInvitation invitation);

    // ── Two-source mapping — ALL fields must be explicit ──────────

    /**
     * Maps invitation + team into the response.
     * Every target field declares its source to avoid MapStruct ambiguity.
     */
    @Mapping(source = "invitation.id",             target = "id")
    @Mapping(source = "invitation.teamId",         target = "teamId")
    @Mapping(source = "invitation.senderId",       target = "senderId")
    @Mapping(source = "invitation.receiverId",     target = "receiverId")
    @Mapping(source = "invitation.status",         target = "status")
    @Mapping(source = "invitation.expirationTime", target = "expirationTime")
    @Mapping(source = "invitation.createdAt",      target = "createdAt")
    @Mapping(source = "team.name",                 target = "teamName")
    @Mapping(target = "isExpired", expression = "java(computeIsExpired(invitation))")
    InvitationResponse toResponse(TeamInvitation invitation, Team team);

    // ── Request → Entity ──────────────────────────────────────────

    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "teamId",         ignore = true)
    @Mapping(target = "senderId",       ignore = true)
    @Mapping(target = "status",         ignore = true)
    @Mapping(target = "expirationTime", ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    TeamInvitation toEntity(InvitationRequest request);

    // ── List variant ──────────────────────────────────────────────

    List<InvitationResponse> toResponseList(List<TeamInvitation> invitations);

    // ── Expression helper ─────────────────────────────────────────

    default boolean computeIsExpired(TeamInvitation invitation) {
        if (invitation.getStatus() == InvitationStatus.EXPIRED) return true;
        if (invitation.getStatus() == InvitationStatus.PENDING
                && invitation.getExpirationTime() != null) {
            return Instant.now().isAfter(invitation.getExpirationTime());
        }
        return false;
    }
}