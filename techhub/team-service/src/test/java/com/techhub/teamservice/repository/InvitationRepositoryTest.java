package com.techhub.teamservice.repository;

import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamInvitation;
import com.techhub.teamservice.entity.enums.InvitationStatus;
import com.techhub.teamservice.entity.enums.TeamStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository slice test for {@link InvitationRepository}.
 *
 * <p>Critical tests:
 * <ul>
 *   <li>{@code findExpiredInvitations} — the scheduler depends on this.</li>
 *   <li>{@code existsByTeamIdAndReceiverIdAndStatus} — prevents duplicate invitations.</li>
 *   <li>{@code findByIdAndReceiverId} — the security boundary for accept/decline.</li>
 * </ul>
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("InvitationRepository — custom queries")
class InvitationRepositoryTest {

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private TestEntityManager em;

    private static final UUID TEAM_ID    = UUID.randomUUID();
    private static final UUID SENDER_ID  = UUID.randomUUID();
    private static final UUID RECEIVER_1 = UUID.randomUUID();
    private static final UUID RECEIVER_2 = UUID.randomUUID();

    private TeamInvitation pendingFuture;
    private TeamInvitation pendingExpiredByTime;
    private TeamInvitation accepted;
    private TeamInvitation alreadyExpired;

    @BeforeEach
    void setUp() {
        // Need a team for FK constraint
        em.persistAndFlush(Team.builder()
                .id(TEAM_ID)
                .name("Test Team")
                .maxMembers(5)
                .currentMembers(1)
                .status(TeamStatus.OPEN)
                .ownerId(SENDER_ID)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        // PENDING — not yet expired (48h in future)
        pendingFuture = em.persistAndFlush(TeamInvitation.builder()
                .id(UUID.randomUUID())
                .teamId(TEAM_ID)
                .senderId(SENDER_ID)
                .receiverId(RECEIVER_1)
                .status(InvitationStatus.PENDING)
                .expirationTime(Instant.now().plus(48, ChronoUnit.HOURS))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        // PENDING — expired by time (1h ago)
        pendingExpiredByTime = em.persistAndFlush(TeamInvitation.builder()
                .id(UUID.randomUUID())
                .teamId(TEAM_ID)
                .senderId(SENDER_ID)
                .receiverId(RECEIVER_2)
                .status(InvitationStatus.PENDING)
                .expirationTime(Instant.now().minus(1, ChronoUnit.HOURS))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        // ACCEPTED — should not appear in expired query
        accepted = em.persistAndFlush(TeamInvitation.builder()
                .id(UUID.randomUUID())
                .teamId(TEAM_ID)
                .senderId(SENDER_ID)
                .receiverId(UUID.randomUUID())
                .status(InvitationStatus.ACCEPTED)
                .expirationTime(Instant.now().plus(24, ChronoUnit.HOURS))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        // Already marked EXPIRED by a previous scheduler run
        alreadyExpired = em.persistAndFlush(TeamInvitation.builder()
                .id(UUID.randomUUID())
                .teamId(TEAM_ID)
                .senderId(SENDER_ID)
                .receiverId(UUID.randomUUID())
                .status(InvitationStatus.EXPIRED)
                .expirationTime(Instant.now().minus(2, ChronoUnit.HOURS))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build());

        em.clear();
    }

    // ══ findExpiredInvitations ════════════════════════════════════

    @Nested
    @DisplayName("findExpiredInvitations() — scheduler query")
    class FindExpiredInvitationsTests {

        @Test
        @DisplayName("returns only PENDING invitations whose time has passed")
        void returnsOnlyPendingExpiredByTime() {
            List<TeamInvitation> expired =
                    invitationRepository.findExpiredInvitations(Instant.now());

            assertThat(expired).hasSize(1);
            assertThat(expired.get(0).getId()).isEqualTo(pendingExpiredByTime.getId());
        }

        @Test
        @DisplayName("does not return ACCEPTED invitations")
        void doesNotReturnAccepted() {
            List<TeamInvitation> expired =
                    invitationRepository.findExpiredInvitations(Instant.now());

            assertThat(expired)
                    .noneMatch(i -> i.getStatus() == InvitationStatus.ACCEPTED);
        }

        @Test
        @DisplayName("does not return already EXPIRED invitations")
        void doesNotReturnAlreadyExpired() {
            List<TeamInvitation> expired =
                    invitationRepository.findExpiredInvitations(Instant.now());

            assertThat(expired)
                    .noneMatch(i -> i.getId().equals(alreadyExpired.getId()));
        }

        @Test
        @DisplayName("does not return PENDING invitations still in future")
        void doesNotReturnFuturePending() {
            List<TeamInvitation> expired =
                    invitationRepository.findExpiredInvitations(Instant.now());

            assertThat(expired)
                    .noneMatch(i -> i.getId().equals(pendingFuture.getId()));
        }

        @Test
        @DisplayName("returns empty list when nothing is expired")
        void emptyListWhenNothingExpired() {
            // Pass a 'now' far in the past — nothing is expired relative to that
            List<TeamInvitation> expired = invitationRepository
                    .findExpiredInvitations(Instant.now().minus(72, ChronoUnit.HOURS));

            assertThat(expired).isEmpty();
        }
    }

    // ══ existsByTeamIdAndReceiverIdAndStatus ══════════════════════

    @Nested
    @DisplayName("existsByTeamIdAndReceiverIdAndStatus() — duplicate prevention")
    class ExistsDuplicateTests {

        @Test
        @DisplayName("returns true when PENDING invitation already exists")
        void trueForExistingPending() {
            assertThat(invitationRepository.existsByTeamIdAndReceiverIdAndStatus(
                    TEAM_ID, RECEIVER_1, InvitationStatus.PENDING)).isTrue();
        }

        @Test
        @DisplayName("returns false for different receiver")
        void falseForDifferentReceiver() {
            assertThat(invitationRepository.existsByTeamIdAndReceiverIdAndStatus(
                    TEAM_ID, UUID.randomUUID(), InvitationStatus.PENDING)).isFalse();
        }

        @Test
        @DisplayName("returns false for ACCEPTED status (allows re-invite after acceptance)")
        void falseForAcceptedStatus() {
            assertThat(invitationRepository.existsByTeamIdAndReceiverIdAndStatus(
                    TEAM_ID, RECEIVER_1, InvitationStatus.ACCEPTED)).isFalse();
        }
    }

    // ══ findByIdAndReceiverId — security boundary ════════════════

    @Nested
    @DisplayName("findByIdAndReceiverId() — security boundary")
    class FindByIdAndReceiverIdTests {

        @Test
        @DisplayName("returns invitation when id and receiver match")
        void returnsWhenMatch() {
            Optional<TeamInvitation> result = invitationRepository
                    .findByIdAndReceiverId(pendingFuture.getId(), RECEIVER_1);

            assertThat(result).isPresent();
            assertThat(result.get().getReceiverId()).isEqualTo(RECEIVER_1);
        }

        @Test
        @DisplayName("returns empty when receiver does not match — prevents cross-user accept")
        void emptyWhenReceiverMismatch() {
            Optional<TeamInvitation> result = invitationRepository
                    .findByIdAndReceiverId(pendingFuture.getId(), UUID.randomUUID());

            assertThat(result).isEmpty();
        }
    }

    // ══ findByReceiverIdAndStatus ════════════════════════════════

    @Test
    @DisplayName("findByReceiverIdAndStatus() returns only PENDING for receiver")
    void findByReceiverIdAndStatus_returnsOnlyPending() {
        List<TeamInvitation> pending = invitationRepository
                .findByReceiverIdAndStatusOrderByCreatedAtDesc(
                        RECEIVER_1, InvitationStatus.PENDING);

        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).getStatus()).isEqualTo(InvitationStatus.PENDING);
    }

    // ══ findByTeamId ══════════════════════════════════════════════

    @Test
    @DisplayName("findByTeamIdOrderByCreatedAtDesc() returns all invitations for team")
    void findByTeamId_returnsAll() {
        List<TeamInvitation> all = invitationRepository
                .findByTeamIdOrderByCreatedAtDesc(TEAM_ID);

        // 4 invitations were persisted for TEAM_ID
        assertThat(all).hasSize(4);
    }
}
