package com.techhub.teamservice.entity;

import com.techhub.teamservice.entity.enums.TeamStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.Instant;
import java.util.UUID;

/**
 * Persistent entity representing a TechHub team.
 *
 * <p>Design decisions:
 * <ul>
 *   <li>ID is assigned by the application (UUID.randomUUID()), not the database —
 *       avoids an extra round-trip on insert and works cleanly with JPA merge.</li>
 *   <li>{@code currentMembers} is managed explicitly in {@code TeamServiceImpl}
 *       rather than via a size() on a @OneToMany collection — prevents loading
 *       the full member list just to count it.</li>
 *   <li>{@code @PrePersist} / {@code @PreUpdate} mirror the PostgreSQL trigger
 *       in schema.sql — both are present so the entity is always consistent
 *       whether accessed via JPA or raw SQL.</li>
 *   <li>Second-level cache: {@code READ_WRITE} — safe for the update pattern
 *       used in TeamServiceImpl (load → mutate → save in the same transaction).</li>
 * </ul>
 *
 * <p>Column names match schema.sql exactly — validated by Hibernate at startup
 * when ddl-auto=validate.
 */
@Entity
@Table(
    name = "teams",
    indexes = {
        @Index(name = "idx_teams_owner_id", columnList = "owner_id"),
        @Index(name = "idx_teams_status",   columnList = "status")
    }
)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Team {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(name = "current_members", nullable = false)
    private Integer currentMembers;

    /**
     * Stored as VARCHAR(10) to match the schema.sql CHECK constraint.
     * Using EnumType.STRING (not ORDINAL) so values survive enum reordering.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private TeamStatus status;

    /**
     * References User Service — no FK constraint (cross-service boundary).
     * Validated at the service layer by confirming the JWT subject matches.
     */
    @Column(name = "owner_id", nullable = false, updatable = false)
    private UUID ownerId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ── JPA lifecycle hooks ────────────────────────────────────────

    @PrePersist
    void onPersist() {
        Instant now = Instant.now();
        if (this.id == null)          this.id = UUID.randomUUID();
        if (this.createdAt == null)   this.createdAt = now;
        if (this.updatedAt == null)   this.updatedAt = now;
        if (this.status == null)      this.status = TeamStatus.OPEN;
        if (this.currentMembers == null) this.currentMembers = 1;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ── Domain helpers ─────────────────────────────────────────────

    /** Returns true when the team has reached its member limit. */
    public boolean isFull() {
        return this.currentMembers != null
            && this.maxMembers != null
            && this.currentMembers >= this.maxMembers;
    }

    /** Returns true when additional members can still join. */
    public boolean hasCapacity() {
        return !isFull() && this.status == TeamStatus.OPEN;
    }

    /**
     * Increments the member count and sets status to FULL when capacity is reached.
     * Called by {@code InvitationServiceImpl} on invitation acceptance.
     */
    public void incrementMembers() {
        this.currentMembers = (this.currentMembers == null ? 0 : this.currentMembers) + 1;
        if (isFull()) {
            this.status = TeamStatus.FULL;
        }
    }

    /**
     * Decrements the member count and reopens the team when it was FULL.
     * Called by {@code TeamServiceImpl#leaveTeam}.
     */
    public void decrementMembers() {
        if (this.currentMembers != null && this.currentMembers > 0) {
            this.currentMembers--;
        }
        if (this.status == TeamStatus.FULL) {
            this.status = TeamStatus.OPEN;
        }
    }
}



















// package com.techhub.teamservice.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.time.Instant;
// import java.util.UUID;

// @Entity
// @Table(name = "teams")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class Team {

//     @Id
//     @Column(name = "id", nullable = false)
//     private UUID id;

//     @Column(name = "name", length = 120, nullable = false)
//     private String name;

//     @Column(name = "description")
//     private String description;

//     @Column(name = "max_members", nullable = false)
//     private Integer maxMembers;

//     @Column(name = "current_members", nullable = false)
//     private Integer currentMembers;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "status", nullable = false)
//     private com.techhub.teamservice.entity.enums.TeamStatus status;

//     @Column(name = "owner_id", nullable = false)
//     private UUID ownerId;

//     @Column(name = "created_at", nullable = false)
//     private Instant createdAt;

//     @Column(name = "updated_at", nullable = false)
//     private Instant updatedAt;
// }

