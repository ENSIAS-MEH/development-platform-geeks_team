package com.techhub.community.entity;

import com.techhub.community.enums.MemberRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a membership link between a user and a community group.
 * UNIQUE constraint on (group_id, user_id) ensures one membership per user per
 * group.
 */
@Entity
@Table(name = "group_members", uniqueConstraints = @UniqueConstraint(name = "uk_group_user", columnNames = { "group_id",
        "user_id" }), indexes = {
                @Index(name = "idx_member_user", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "group_id", nullable = false)
    private UUID groupId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = Instant.now();
        }
    }
}
