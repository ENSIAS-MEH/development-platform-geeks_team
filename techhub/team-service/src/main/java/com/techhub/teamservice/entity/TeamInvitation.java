package com.techhub.teamservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "team_invitations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamInvitation {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private com.techhub.teamservice.entity.enums.InvitationStatus status;

    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

