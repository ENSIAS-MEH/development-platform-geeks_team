package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.enums.TeamStatus;
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
 * Repository for {@link Team} persistence.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>Spring Data JPA for standard CRUD — avoids boilerplate.</li>
 *   <li>JPQL for queries that filter by enum or join — type-safe,
 *       refactor-friendly, and validated at startup by Hibernate.</li>
 *   <li>Native SQL only where JPQL cannot express the query
 *       (e.g. bulk UPDATE with RETURNING).</li>
 *   <li>No service logic here — repositories are pure data access.</li>
 * </ul>
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    // ── Ownership queries ─────────────────────────────────────────

    /**
     * Returns all teams owned by a given user.
     * Used by GET /api/teams/my when filtering by ownership.
     */
    List<Team> findByOwnerIdOrderByCreatedAtDesc(UUID ownerId);

    /**
     * Returns teams the user owns AND teams where they are a member.
     * Single query avoids two round-trips for the "my teams" endpoint.
     */
    @Query("""
            SELECT DISTINCT t FROM Team t
            LEFT JOIN TeamMember tm ON tm.teamId = t.id
            WHERE t.ownerId = :userId OR tm.userId = :userId
            ORDER BY t.createdAt DESC
            """)
    List<Team> findAllTeamsForUser(@Param("userId") UUID userId);

    /**
     * Paginated version of findAllTeamsForUser — used by GET /api/teams/my-teams.
     * A separate countQuery is required because DISTINCT + LEFT JOIN cannot be
     * derived automatically by Spring Data.
     */
    @Query(
        value = """
                SELECT DISTINCT t FROM Team t
                LEFT JOIN TeamMember tm ON tm.teamId = t.id
                WHERE t.ownerId = :userId OR tm.userId = :userId
                """,
        countQuery = """
                SELECT COUNT(DISTINCT t) FROM Team t
                LEFT JOIN TeamMember tm ON tm.teamId = t.id
                WHERE t.ownerId = :userId OR tm.userId = :userId
                """
    )
    Page<Team> findAllTeamsForUserPageable(@Param("userId") UUID userId, Pageable pageable);

    /** Full-text name search used by GET /api/teams/search. */
    Page<Team> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // ── Status queries ────────────────────────────────────────────

    /**
     * Returns all OPEN teams — used by team formation / auto-balance.
     */
    List<Team> findByStatusOrderByCreatedAtDesc(TeamStatus status);

    /**
     * Checks whether a team with the same name already exists for this owner.
     * Prevents duplicate team names per owner.
     */
    boolean existsByOwnerIdAndNameIgnoreCase(UUID ownerId, String name);

    /**
     * Loads team only if it exists AND belongs to the given owner.
     * Used by delete and close operations to validate ownership in one query.
     */
    Optional<Team> findByIdAndOwnerId(UUID id, UUID ownerId);

    // ── Bulk update (native SQL — JPQL cannot do bulk UPDATE with conditions) ──

    /**
     * Atomically increments member count and sets status to FULL when max is reached.
     * Called on invitation acceptance — avoids a load-then-save round-trip.
     *
     * @return number of rows updated (1 on success, 0 if team not found)
     */
    @Modifying
    @Query(value = """
            UPDATE teams SET
                current_members = current_members + 1,
                status = CASE
                    WHEN current_members + 1 >= max_members THEN 'FULL'
                    ELSE status
                END,
                updated_at = now()
            WHERE id = :teamId
            """, nativeQuery = true)
    int incrementMemberCount(@Param("teamId") UUID teamId);

    /**
     * Atomically decrements member count and reopens a FULL team.
     * Called on leave — same pattern as increment.
     */
    @Modifying
    @Query(value = """
            UPDATE teams SET
                current_members = GREATEST(current_members - 1, 0),
                status = CASE
                    WHEN status = 'FULL' THEN 'OPEN'
                    ELSE status
                END,
                updated_at = now()
            WHERE id = :teamId
            """, nativeQuery = true)
    int decrementMemberCount(@Param("teamId") UUID teamId);
}
