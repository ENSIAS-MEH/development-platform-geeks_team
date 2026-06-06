package com.techhub.teamservice.entity;

import com.techhub.teamservice.entity.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Junction entity between a {@link Team} and a user (from User Service).
 *
 * <p>Design decisions:
 * <ul>
 *   <li>No {@code @ManyToOne} to Team — avoids accidental eager loading of the team
 *       every time we query members. We query by {@code teamId} (UUID) directly.</li>
 *   <li>{@code userId} references User Service — no DB foreign key (cross-service).</li>
 *   <li>Unique constraint {@code uq_team_user} enforced in schema.sql; JPA layer
 *       trusts the DB to reject duplicates.</li>
 * </ul>
 */
@Entity
@Table(
    name = "team_members",
    indexes = {
        @Index(name = "idx_team_members_team_id", columnList = "team_id"),
        @Index(name = "idx_team_members_user_id", columnList = "user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_team_user", columnNames = {"team_id", "user_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TeamMember {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(name = "team_id", nullable = false, updatable = false)
    private UUID teamId;

    /** References User Service user — no DB FK (cross-service boundary). */
    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 10)
    private MemberRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    // ── JPA lifecycle hook ─────────────────────────────────────────

    @PrePersist
    void onPersist() {
        if (this.id == null)       this.id = UUID.randomUUID();
        if (this.joinedAt == null) this.joinedAt = Instant.now();
        if (this.role == null)     this.role = MemberRole.MEMBER;
    }

    // ── Domain helper ──────────────────────────────────────────────

    public boolean isOwner() {
        return MemberRole.OWNER == this.role;
    }
}











// package com.techhub.teamservice.entity;

// import jakarta.persistence.*;
// import lombok.*;

// import java.time.Instant;
// import java.util.UUID;

// @Entity
// @Table(name = "team_members")
// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// public class TeamMember {

//     @Id
//     @Column(name = "id", nullable = false)
//     private UUID id;

//     @Column(name = "team_id", nullable = false)
//     private UUID teamId;

//     @Column(name = "user_id", nullable = false)
//     private UUID userId;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "role", nullable = false)
//     private com.techhub.teamservice.entity.enums.MemberRole role;

//     @Column(name = "joined_at", nullable = false)
//     private Instant joinedAt;
// }

