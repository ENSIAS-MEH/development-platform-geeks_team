package com.techhub.teamservice.entity;

import com.techhub.teamservice.entity.enums.InvitationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TeamInvitation} domain helpers.
 * No Spring context — pure POJO tests.
 */
@DisplayName("TeamInvitation entity — domain logic")
class TeamInvitationTest {

    private TeamInvitation buildInvitation(InvitationStatus status, Instant expirationTime) {
        return TeamInvitation.builder()
                .id(UUID.randomUUID())
                .teamId(UUID.randomUUID())
                .senderId(UUID.randomUUID())
                .receiverId(UUID.randomUUID())
                .status(status)
                .expirationTime(expirationTime)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ══ isActionable() ════════════════════════════════════════════

    @Nested
    @DisplayName("isActionable()")
    class IsActionableTests {

        @Test
        @DisplayName("returns true for PENDING invitation not yet expired")
        void isActionable_pendingNotExpired() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.PENDING,
                    Instant.now().plus(48, ChronoUnit.HOURS)
            );
            assertThat(inv.isActionable()).isTrue();
        }

        @Test
        @DisplayName("returns false for PENDING invitation past expiration time")
        void isActionable_pendingExpiredByTime() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.PENDING,
                    Instant.now().minus(1, ChronoUnit.HOURS)
            );
            assertThat(inv.isActionable()).isFalse();
        }

        @Test
        @DisplayName("returns false for ACCEPTED invitation")
        void isActionable_accepted() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.ACCEPTED,
                    Instant.now().plus(48, ChronoUnit.HOURS)
            );
            assertThat(inv.isActionable()).isFalse();
        }

        @Test
        @DisplayName("returns false for DECLINED invitation")
        void isActionable_declined() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.DECLINED,
                    Instant.now().plus(48, ChronoUnit.HOURS)
            );
            assertThat(inv.isActionable()).isFalse();
        }

        @Test
        @DisplayName("returns false for EXPIRED invitation")
        void isActionable_expired() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.EXPIRED,
                    Instant.now().minus(1, ChronoUnit.HOURS)
            );
            assertThat(inv.isActionable()).isFalse();
        }
    }

    // ══ isExpired() ═══════════════════════════════════════════════

    @Nested
    @DisplayName("isExpired()")
    class IsExpiredTests {

        @Test
        @DisplayName("returns true for PENDING invitation past expiration")
        void isExpired_pendingPastExpiry() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.PENDING,
                    Instant.now().minus(1, ChronoUnit.MINUTES)
            );
            assertThat(inv.isExpired()).isTrue();
        }

        @Test
        @DisplayName("returns false for PENDING invitation not yet expired")
        void isExpired_pendingNotYetExpired() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.PENDING,
                    Instant.now().plus(1, ChronoUnit.HOURS)
            );
            assertThat(inv.isExpired()).isFalse();
        }

        @Test
        @DisplayName("returns false when status is already EXPIRED (scheduler already ran)")
        void isExpired_alreadyMarkedExpired() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.EXPIRED,
                    Instant.now().minus(2, ChronoUnit.HOURS)
            );
            // Already EXPIRED — isExpired() only flags PENDING ones
            assertThat(inv.isExpired()).isFalse();
        }
    }

    // ══ State transitions ══════════════════════════════════════════

    @Nested
    @DisplayName("State transitions")
    class StateTransitionTests {

        @Test
        @DisplayName("accept() sets status to ACCEPTED")
        void accept_setsAccepted() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.PENDING,
                    Instant.now().plus(48, ChronoUnit.HOURS)
            );
            inv.accept();
            assertThat(inv.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
        }

        @Test
        @DisplayName("decline() sets status to DECLINED")
        void decline_setsDeclined() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.PENDING,
                    Instant.now().plus(48, ChronoUnit.HOURS)
            );
            inv.decline();
            assertThat(inv.getStatus()).isEqualTo(InvitationStatus.DECLINED);
        }

        @Test
        @DisplayName("expire() sets status to EXPIRED")
        void expire_setsExpired() {
            TeamInvitation inv = buildInvitation(
                    InvitationStatus.PENDING,
                    Instant.now().minus(1, ChronoUnit.HOURS)
            );
            inv.expire();
            assertThat(inv.getStatus()).isEqualTo(InvitationStatus.EXPIRED);
        }
    }

    // ══ @PrePersist ════════════════════════════════════════════════

    @Nested
    @DisplayName("@PrePersist via onPersist()")
    class OnPersistTests {

        @Test
        @DisplayName("assigns UUID when id is null")
        void onPersist_assignsId() {
            TeamInvitation inv = new TeamInvitation();
            inv.onPersist();
            assertThat(inv.getId()).isNotNull();
        }

        @Test
        @DisplayName("sets default status to PENDING")
        void onPersist_defaultStatusPending() {
            TeamInvitation inv = new TeamInvitation();
            inv.onPersist();
            assertThat(inv.getStatus()).isEqualTo(InvitationStatus.PENDING);
        }

        @Test
        @DisplayName("sets createdAt and updatedAt")
        void onPersist_setsTimestamps() {
            TeamInvitation inv = new TeamInvitation();
            Instant before = Instant.now().minusSeconds(1);
            inv.onPersist();
            Instant after = Instant.now().plusSeconds(1);

            assertThat(inv.getCreatedAt()).isBetween(before, after);
            assertThat(inv.getUpdatedAt()).isBetween(before, after);
        }

        @Test
        @DisplayName("preserves existing id")
        void onPersist_preservesExistingId() {
            UUID existing = UUID.randomUUID();
            TeamInvitation inv = new TeamInvitation();
            inv.setId(existing);
            inv.onPersist();
            assertThat(inv.getId()).isEqualTo(existing);
        }
    }

    // ══ @PreUpdate ════════════════════════════════════════════════

    @Test
    @DisplayName("onUpdate() refreshes updatedAt")
    void onUpdate_refreshesTimestamp() throws InterruptedException {
        TeamInvitation inv = buildInvitation(
                InvitationStatus.PENDING,
                Instant.now().plus(48, ChronoUnit.HOURS)
        );
        Instant original = inv.getUpdatedAt();
        Thread.sleep(2);
        inv.onUpdate();
        assertThat(inv.getUpdatedAt()).isAfter(original);
    }
}