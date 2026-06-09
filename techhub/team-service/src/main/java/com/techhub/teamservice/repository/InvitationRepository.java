package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.TeamInvitation;
import com.techhub.teamservice.entity.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link TeamInvitation} persistence.
 *
 * <p>The scheduler query uses a partial index on (expiration_time) WHERE status='PENDING'
 * defined in schema.sql — only pending rows are scanned, making the 60-second
 * expiry job cheap even with millions of historical invitations.
 */
@Repository
public interface InvitationRepository extends JpaRepository<TeamInvitation, UUID> {

    // ── Receiver queries ──────────────────────────────────────────

    /**
     * Returns all invitations for a given receiver, newest first.
     * Used by GET /api/invitations/my.
     */
    List<TeamInvitation> findByReceiverIdOrderByCreatedAtDesc(UUID receiverId);

    /**
     * Returns only PENDING invitations for a receiver.
     * Used in the invitations page to show only actionable items.
     */
    List<TeamInvitation> findByReceiverIdAndStatusOrderByCreatedAtDesc(
            UUID receiverId, InvitationStatus status);

    // ── Duplicate prevention ──────────────────────────────────────

    /**
     * Checks whether a PENDING invitation already exists for this user+team combination.
     * Prevents duplicate invitations. Called before creating a new one.
     */
    boolean existsByTeamIdAndReceiverIdAndStatus(
            UUID teamId, UUID receiverId, InvitationStatus status);

    // ── Scheduler query (uses partial index idx_invitations_pending_expiry) ─

    /**
     * Returns all PENDING invitations whose expiration time has passed.
     *
     * <p>This query targets the partial index:
     * <pre>
     *   CREATE INDEX idx_invitations_pending_expiry
     *       ON team_invitations (expiration_time)
     *       WHERE status = 'PENDING';
     * </pre>
     * With the index, this is an index scan on only pending rows —
     * not a full table scan — regardless of total invitation count.
     *
     * @param now current timestamp injected by the scheduler
     */
    @Query("""
            SELECT i FROM TeamInvitation i
            WHERE i.status = 'PENDING'
            AND i.expirationTime < :now
            """)
    List<TeamInvitation> findExpiredInvitations(@Param("now") Instant now);

    // ── Bulk expiry (alternative to loading then saving each row) ─

    /**
     * Bulk-marks expired invitations in a single UPDATE statement.
     * More efficient than loading all rows, setting each status, and saving.
     * Used alongside findExpiredInvitations when only Kafka events need firing.
     *
     * @return number of rows updated
     */
    @Modifying
    @Query("""
            UPDATE TeamInvitation i SET i.status = 'EXPIRED'
            WHERE i.status = 'PENDING'
            AND i.expirationTime < :now
            """)
    int bulkExpireInvitations(@Param("now") Instant now);

    // ── Team-scoped queries ───────────────────────────────────────

    /**
     * Returns all invitations for a specific team.
     * Used by team owners to see sent invitations.
     */
    List<TeamInvitation> findByTeamIdOrderByCreatedAtDesc(UUID teamId);

    /**
     * Returns a specific invitation by ID, but only if the receiver matches.
     * Prevents one user from accepting another user's invitation.
     * This is the secure version of findById for accept/decline operations.
     */
    Optional<TeamInvitation> findByIdAndReceiverId(UUID id, UUID receiverId);
}
