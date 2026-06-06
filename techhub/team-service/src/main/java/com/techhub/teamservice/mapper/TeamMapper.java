package com.techhub.teamservice.mapper;

import com.techhub.teamservice.dto.TeamMemberResponse;
import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamMember;
import com.techhub.teamservice.entity.enums.TeamStatus;
import org.mapstruct.*;

import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for {@link Team} ↔ DTO conversions.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>{@code componentModel = "spring"} — MapStruct generates a Spring
 *       {@code @Component}, injectable via constructor in services.</li>
 *   <li>{@code unmappedTargetPolicy = ERROR} — any new field added to
 *       {@code TeamResponse} that is not mapped fails the build immediately,
 *       preventing silent data loss.</li>
 *   <li>{@code remainingSlots} is a computed field — MapStruct cannot derive it
 *       automatically, so it uses a {@code @Named} expression method.</li>
 *   <li>{@code isOwner} is set by the service layer after mapping —
 *       the mapper always sets it to {@code false}; the service overrides it.</li>
 * </ul>
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TeamMapper {

    // ── Entity → Response ─────────────────────────────────────────

    /**
     * Maps a Team entity to a TeamResponse DTO.
     *
     * <p>{@code remainingSlots} is computed via the expression helper.
     * {@code isOwner} defaults to false — the service sets it correctly
     * by calling {@link #withOwnerFlag(TeamResponse, boolean)}.
     */
    @Mapping(target = "remainingSlots", expression = "java(computeRemainingSlots(team))")
    @Mapping(target = "isOwner",        constant = "false")
    TeamResponse toResponse(Team team);

    List<TeamResponse> toResponseList(List<Team> teams);

    // ── Request → Entity ──────────────────────────────────────────

    /**
     * Maps a TeamRequest to a new Team entity.
     * Fields set by the service layer (ownerId, status, timestamps)
     * are ignored here — the entity's {@code @PrePersist} sets the defaults.
     */
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "currentMembers", ignore = true)
    @Mapping(target = "status",         ignore = true)
    @Mapping(target = "ownerId",        ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "updatedAt",      ignore = true)
    Team toEntity(TeamRequest request);

    // ── Member mapping ────────────────────────────────────────────

    TeamMemberResponse toMemberResponse(TeamMember member);

    List<TeamMemberResponse> toMemberResponseList(List<TeamMember> members);

    // ── After-mapping helper: set the isOwner flag ────────────────

    /**
     * Returns a new TeamResponse with the {@code isOwner} flag set correctly.
     * Called by the service after mapping: {@code mapper.withOwnerFlag(resp, true)}.
     *
     * <p>Records are immutable — we create a new instance with the flag replaced.
     */
    default TeamResponse withOwnerFlag(TeamResponse response, boolean isOwner) {
        return new TeamResponse(
                response.id(),
                response.name(),
                response.description(),
                response.maxMembers(),
                response.currentMembers(),
                response.remainingSlots(),
                response.status(),
                response.ownerId(),
                isOwner,
                response.createdAt()
        );
    }

    // ── Expression helpers ────────────────────────────────────────

    /**
     * Computes remaining slots — used in @Mapping expression above.
     * Protected so MapStruct-generated impl can call it.
     */
    default int computeRemainingSlots(Team team) {
        if (team.getMaxMembers() == null || team.getCurrentMembers() == null) return 0;
        return Math.max(0, team.getMaxMembers() - team.getCurrentMembers());
    }
}
