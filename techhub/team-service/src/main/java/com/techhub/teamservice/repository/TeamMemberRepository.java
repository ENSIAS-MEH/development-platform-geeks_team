package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.TeamMember;
import com.techhub.teamservice.entity.enums.MemberRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link TeamMember} persistence.
 *
 * <p>Handles membership checks that the service layer needs
 * without loading the entire Team aggregate.
 */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {

    // ── Membership checks ─────────────────────────────────────────

    /**
     * Checks whether a user is already a member of the given team.
     * Used before sending an invitation — prevents inviting existing members.
     */
    boolean existsByTeamIdAndUserId(UUID teamId, UUID userId);

    /**
     * Checks whether a user has a specific role in a team.
     * Used to verify owner rights before delete / invite operations.
     */
    boolean existsByTeamIdAndUserIdAndRole(UUID teamId, UUID userId, MemberRole role);

    /**
     * Returns all members of a team, ordered by join date.
     * Used by GET /api/teams/{id}/members.
     */
    List<TeamMember> findByTeamIdOrderByJoinedAtAsc(UUID teamId);

    /** Paginated version — used when the team has many members. */
    Page<TeamMember> findByTeamIdOrderByJoinedAtAsc(UUID teamId, Pageable pageable);

    /**
     * Returns a specific membership record.
     * Used by leave-team to find and delete the membership.
     */
    Optional<TeamMember> findByTeamIdAndUserId(UUID teamId, UUID userId);

    /**
     * Returns all teams a user belongs to (as member or owner).
     * Used to build the user's team list from the member side.
     */
    List<TeamMember> findByUserId(UUID userId);

    // ── Bulk delete ───────────────────────────────────────────────

    /**
     * Removes all members when a team is deleted.
     * Called inside TeamServiceImpl#deleteTeam within the same transaction.
     * The DB-level CASCADE also handles this, but explicit deletion
     * ensures audit logs and entity lifecycle callbacks fire.
     */
    @Modifying
    @Query("DELETE FROM TeamMember tm WHERE tm.teamId = :teamId")
    void deleteAllByTeamId(@Param("teamId") UUID teamId);

    /**
     * Counts members in a team.
     * Cheap alternative to loading the entire member list just to count.
     */
    long countByTeamId(UUID teamId);
}
