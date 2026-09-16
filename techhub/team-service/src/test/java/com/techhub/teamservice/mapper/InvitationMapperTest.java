package com.techhub.teamservice.mapper;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;
import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamInvitation;
import com.techhub.teamservice.entity.enums.InvitationStatus;
import com.techhub.teamservice.entity.enums.TeamStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link InvitationMapper}.
 *
 * <p>Key scenarios:
 * <ul>
 *   <li>{@code isExpired} computation — 4 cases</li>
 *   <li>{@code teamName} denormalisation from Team entity</li>
 *   <li>{@code toEntity} — service-managed fields must be null</li>
 * </ul>
 */
@DisplayName("InvitationMapper — mapping logic")
class InvitationMapperTest {

    private InvitationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(InvitationMapper.class);
    }

    private TeamInvitation buildInvitation(InvitationStatus status, Instant expiry) {
        return TeamInvitation.builder()
                .id(UUID.randomUUID())
                .teamId(UUID.randomUUID())
                .senderId(UUID.randomUUID())
                .receiverId(UUID.randomUUID())
                .status(status)
                .expirationTime(expiry)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private Team buildTeam(String name) {
        return Team.builder()
                .id(UUID.randomUUID())
                .name(name)
                .maxMembers(5).currentMembers(1)
                .status(TeamStatus.OPEN)
                .ownerId(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ══ isExpired computation ══════════════════════════════════════

    @Nested
    @DisplayName("isExpired flag computation")
    class IsExpiredTests {

        @Test
        @DisplayName("false for PENDING not yet expired")
        void falseForPendingNotExpired() {
            TeamInvitation inv = buildInvitation(InvitationStatus.PENDING,
                    Instant.now().plus(48, ChronoUnit.HOURS));
            InvitationResponse resp = mapper.toResponse(inv);
            assertThat(resp.isExpired()).isFalse();
        }

        @Test
        @DisplayName("true for PENDING past expiration time")
        void trueForPendingPastExpiry() {
            TeamInvitation inv = buildInvitation(InvitationStatus.PENDING,
                    Instant.now().minus(1, ChronoUnit.HOURS));
            InvitationResponse resp = mapper.toResponse(inv);
            assertThat(resp.isExpired()).isTrue();
        }

        @Test
        @DisplayName("true for EXPIRED status regardless of time")
        void trueForExpiredStatus() {
            TeamInvitation inv = buildInvitation(InvitationStatus.EXPIRED,
                    Instant.now().plus(10, ChronoUnit.HOURS));
            InvitationResponse resp = mapper.toResponse(inv);
            assertThat(resp.isExpired()).isTrue();
        }

        @Test
        @DisplayName("false for ACCEPTED invitation")
        void falseForAccepted() {
            TeamInvitation inv = buildInvitation(InvitationStatus.ACCEPTED,
                    Instant.now().plus(48, ChronoUnit.HOURS));
            InvitationResponse resp = mapper.toResponse(inv);
            assertThat(resp.isExpired()).isFalse();
        }

        @Test
        @DisplayName("false for DECLINED invitation")
        void falseForDeclined() {
            TeamInvitation inv = buildInvitation(InvitationStatus.DECLINED,
                    Instant.now().plus(48, ChronoUnit.HOURS));
            InvitationResponse resp = mapper.toResponse(inv);
            assertThat(resp.isExpired()).isFalse();
        }
    }

    // ══ teamName denormalisation ══════════════════════════════════

    @Nested
    @DisplayName("teamName denormalisation")
    class TeamNameTests {

        @Test
        @DisplayName("maps team name into response when team is provided")
        void mapsTeamName() {
            TeamInvitation inv = buildInvitation(InvitationStatus.PENDING,
                    Instant.now().plus(48, ChronoUnit.HOURS));
            Team team = buildTeam("AI Research Team");
            InvitationResponse resp = mapper.toResponse(inv, team);
            assertThat(resp.teamName()).isEqualTo("AI Research Team");
        }

        @Test
        @DisplayName("teamName is null when mapping without team")
        void nullTeamNameWithoutTeam() {
            TeamInvitation inv = buildInvitation(InvitationStatus.PENDING,
                    Instant.now().plus(48, ChronoUnit.HOURS));
            InvitationResponse resp = mapper.toResponse(inv);
            assertThat(resp.teamName()).isNull();
        }
    }

    // ══ toEntity ══════════════════════════════════════════════════

    @Nested
    @DisplayName("toEntity(InvitationRequest)")
    class ToEntityTests {

        @Test
        @DisplayName("maps receiverId from request")
        void mapsReceiverId() {
            UUID receiverId = UUID.randomUUID();
            InvitationRequest request = new InvitationRequest(UUID.randomUUID(), receiverId);
            TeamInvitation entity = mapper.toEntity(request);
            assertThat(entity.getReceiverId()).isEqualTo(receiverId);
        }

        @Test
        @DisplayName("service-managed fields are null — set by InvitationServiceImpl")
        void serviceManagedFieldsAreNull() {
            InvitationRequest request = new InvitationRequest(UUID.randomUUID(), UUID.randomUUID());
            TeamInvitation entity = mapper.toEntity(request);

            assertThat(entity.getId()).isNull();
            assertThat(entity.getTeamId()).isNull();
            assertThat(entity.getSenderId()).isNull();
            assertThat(entity.getStatus()).isNull();
            assertThat(entity.getExpirationTime()).isNull();
            assertThat(entity.getCreatedAt()).isNull();
        }
    }

    // ══ standard field mapping ════════════════════════════════════

    @Test
    @DisplayName("toResponse maps all standard fields correctly")
    void toResponse_mapsAllFields() {
        TeamInvitation inv = buildInvitation(InvitationStatus.PENDING,
                Instant.now().plus(48, ChronoUnit.HOURS));
        InvitationResponse resp = mapper.toResponse(inv);

        assertThat(resp.id()).isEqualTo(inv.getId());
        assertThat(resp.teamId()).isEqualTo(inv.getTeamId());
        assertThat(resp.senderId()).isEqualTo(inv.getSenderId());
        assertThat(resp.receiverId()).isEqualTo(inv.getReceiverId());
        assertThat(resp.status()).isEqualTo(InvitationStatus.PENDING);
        assertThat(resp.expirationTime()).isEqualTo(inv.getExpirationTime());
        assertThat(resp.createdAt()).isEqualTo(inv.getCreatedAt());
    }
}
