package com.techhub.teamservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "teams")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @Column(name = "current_members", nullable = false)
    private Integer currentMembers;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private com.techhub.teamservice.entity.enums.TeamStatus status;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

