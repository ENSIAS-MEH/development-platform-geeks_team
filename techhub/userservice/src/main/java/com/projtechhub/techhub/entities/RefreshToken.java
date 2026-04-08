package com.projtechhub.techhub.entities;

/**
 * @author pc
 **/
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_refresh_tokens_expires_at", columnList = "expires_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_refresh_tokens_token_hash", columnNames = "token_hash")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_user"))
    private User user;

    @Column(name = "token_hash", nullable = false, length = 64, unique = true)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }
}