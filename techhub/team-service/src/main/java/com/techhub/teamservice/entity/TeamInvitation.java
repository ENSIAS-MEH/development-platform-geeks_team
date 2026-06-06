package com.techhub.teamservice.entity;

import com.techhub.teamservice.entity.enums.InvitationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents an invitation sent by a team owner to a prospective member.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>No {@code @ManyToOne} to Team — same reasoning as TeamMember: avoids
 *       loading the team entity on every invitation query.</li>
 *   <li>{@code senderId} / {@code receiverId} reference User Service (cross-service).
 *       No DB foreign keys — integrity enforced by service layer + JWT claims.</li>
 *   <li>{@code expirationTime} is set to {@code now + INVITATION_EXPIRY_HOURS}
 *       in {@code InvitationServiceImpl}, not here, to keep the entity free of
 *       application configuration (Single Responsibility).</li>
 *   <li>Terminal state check via {@code isActionable()} — service layer calls this
 *       before allowing accept/decline to prevent double-processing.</li>
 * </ul>
 */
@Entity
@Table(
    name = "team_invitations",
    indexes = {
        @Index(name = "idx_invitations_receiver_id",    columnList = "receiver_id"),
        @Index(name = "idx_invitations_team_id",        columnList = "team_id"),
        @Index(name = "idx_invitations_status",         columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeamInvitation {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "team_id", nullable = false, updatable = false)
    private UUID teamId;

    /** The team owner who sent the invitation — cross-service ref to User Service. */
    @Column(name = "sender_id", nullable = false, updatable = false)
    private UUID senderId;

    /** The user being invited — cross-service ref to User Service. */
    @Column(name = "receiver_id", nullable = false, updatable = false)
    private UUID receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private InvitationStatus status;

    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── JPA lifecycle hooks ────────────────────────────────────────

    @PrePersist
    void onPersist() {
        Instant now = Instant.now();
        if (this.id == null)        this.id = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
        if (this.status == null)    this.status = InvitationStatus.PENDING;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ── Domain helpers ─────────────────────────────────────────────

    /**
     * Returns {@code true} if the invitation can still be accepted or declined.
     * A PENDING invitation that is past its expiration time is not actionable —
     * the scheduler will mark it EXPIRED on its next run.
     */
    public boolean isActionable() {
        return this.status == InvitationStatus.PENDING
            && Instant.now().isBefore(this.expirationTime);
    }

    /** Returns {@code true} if this invitation has expired by wall-clock time. */
    public boolean isExpired() {
        return this.status == InvitationStatus.PENDING
            && Instant.now().isAfter(this.expirationTime);
    }

    /** Convenience: transition to ACCEPTED. */
    public void accept() {
        this.status = InvitationStatus.ACCEPTED;
    }

    /** Convenience: transition to DECLINED. */
    public void decline() {
        this.status = InvitationStatus.DECLINED;
    }

    /** Convenience: transition to EXPIRED (called by scheduler). */
    public void expire() {
        this.status = InvitationStatus.EXPIRED;
    }
}




// package com.techhub.teamservice.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.time.Instant;
// import java.util.UUID;

// @Entity
// @Table(name = "team_invitations")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class TeamInvitation {

//     @Id
//     @Column(name = "id", nullable = false)
//     private UUID id;

//     @Column(name = "team_id", nullable = false)
//     private UUID teamId;

//     @Column(name = "sender_id", nullable = false)
//     private UUID senderId;

//     @Column(name = "receiver_id", nullable = false)
//     private UUID receiverId;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "status", nullable = false)
//     private com.techhub.teamservice.entity.enums.InvitationStatus status;

//     @Column(name = "expiration_time", nullable = false)
//     private Instant expirationTime;

//     @Column(name = "created_at", nullable = false)
//     private Instant createdAt;

//     @Column(name = "updated_at", nullable = false)
//     private Instant updatedAt;
// }

