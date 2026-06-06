package com.techhub.teamservice.mapper;

import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.enums.TeamStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link TeamMapper}.
 *
 * <p>Uses {@code Mappers.getMapper()} — loads the MapStruct-generated implementation
 * without Spring context. Fast, isolated, no infrastructure needed.
 *
 * <p>Focus: computed fields (remainingSlots), withOwnerFlag helper,
 * and ignored fields in toEntity.
 */
@DisplayName("TeamMapper — mapping logic")
class TeamMapperTest {

    private TeamMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(TeamMapper.class);
    }

    private Team buildTeam(int current, int max, TeamStatus status) {
        return Team.builder()
                .id(UUID.randomUUID())
                .name("Test Team")
                .description("A test team")
                .maxMembers(max)
                .currentMembers(current)
                .status(status)
                .ownerId(UUID.randomUUID())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    // ══ toResponse ════════════════════════════════════════════════

    @Nested
    @DisplayName("toResponse(Team)")
    class ToResponseTests {

        @Test
        @DisplayName("maps all standard fields correctly")
        void mapsAllFields() {
            Team team = buildTeam(3, 5, TeamStatus.OPEN);
            TeamResponse response = mapper.toResponse(team);

            assertThat(response.id()).isEqualTo(team.getId());
            assertThat(response.name()).isEqualTo(team.getName());
            assertThat(response.description()).isEqualTo(team.getDescription());
            assertThat(response.maxMembers()).isEqualTo(5);
            assertThat(response.currentMembers()).isEqualTo(3);
            assertThat(response.status()).isEqualTo(TeamStatus.OPEN);
            assertThat(response.ownerId()).isEqualTo(team.getOwnerId());
            assertThat(response.createdAt()).isEqualTo(team.getCreatedAt());
        }

        @Test
        @DisplayName("computes remainingSlots correctly")
        void computesRemainingSlots() {
            Team team = buildTeam(3, 5, TeamStatus.OPEN);
            TeamResponse response = mapper.toResponse(team);
            assertThat(response.remainingSlots()).isEqualTo(2);
        }

        @Test
        @DisplayName("remainingSlots is 0 when team is full")
        void remainingSlotsZeroWhenFull() {
            Team team = buildTeam(5, 5, TeamStatus.FULL);
            TeamResponse response = mapper.toResponse(team);
            assertThat(response.remainingSlots()).isZero();
        }

        @Test
        @DisplayName("remainingSlots is never negative")
        void remainingSlotsNeverNegative() {
            Team team = buildTeam(6, 5, TeamStatus.FULL); // over capacity edge case
            TeamResponse response = mapper.toResponse(team);
            assertThat(response.remainingSlots()).isZero();
        }

        @Test
        @DisplayName("isOwner defaults to false — must be set by service")
        void isOwnerDefaultsFalse() {
            Team team = buildTeam(1, 5, TeamStatus.OPEN);
            TeamResponse response = mapper.toResponse(team);
            assertThat(response.isOwner()).isFalse();
        }
    }

    // ══ withOwnerFlag ═════════════════════════════════════════════

    @Nested
    @DisplayName("withOwnerFlag()")
    class WithOwnerFlagTests {

        @Test
        @DisplayName("sets isOwner to true and preserves all other fields")
        void setsOwnerFlagTrue() {
            Team team = buildTeam(2, 5, TeamStatus.OPEN);
            TeamResponse response = mapper.toResponse(team);
            TeamResponse withFlag = mapper.withOwnerFlag(response, true);

            assertThat(withFlag.isOwner()).isTrue();
            // All other fields must be identical
            assertThat(withFlag.id()).isEqualTo(response.id());
            assertThat(withFlag.name()).isEqualTo(response.name());
            assertThat(withFlag.remainingSlots()).isEqualTo(response.remainingSlots());
            assertThat(withFlag.status()).isEqualTo(response.status());
        }

        @Test
        @DisplayName("sets isOwner to false preserving other fields")
        void setsOwnerFlagFalse() {
            Team team = buildTeam(1, 5, TeamStatus.OPEN);
            TeamResponse response = mapper.toResponse(team);
            TeamResponse withFlag = mapper.withOwnerFlag(response, false);
            assertThat(withFlag.isOwner()).isFalse();
        }
    }

    // ══ toEntity ══════════════════════════════════════════════════

    @Nested
    @DisplayName("toEntity(TeamRequest)")
    class ToEntityTests {

        @Test
        @DisplayName("maps name, description, maxMembers from request")
        void mapsRequestFields() {
            TeamRequest request = new TeamRequest("New Team", "A description", 10);
            Team entity = mapper.toEntity(request);

            assertThat(entity.getName()).isEqualTo("New Team");
            assertThat(entity.getDescription()).isEqualTo("A description");
            assertThat(entity.getMaxMembers()).isEqualTo(10);
        }

        @Test
        @DisplayName("leaves id, ownerId, status, timestamps null — @PrePersist sets them")
        void leavesServiceFieldsNull() {
            TeamRequest request = new TeamRequest("Team X", null, 5);
            Team entity = mapper.toEntity(request);

            assertThat(entity.getId()).isNull();
            assertThat(entity.getOwnerId()).isNull();
            assertThat(entity.getStatus()).isNull();
            assertThat(entity.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("compact constructor strips whitespace from name")
        void stripsWhitespaceFromName() {
            TeamRequest request = new TeamRequest("  Trimmed Team  ", null, 5);
            assertThat(request.name()).isEqualTo("Trimmed Team");
        }
    }

    // ══ toResponseList ════════════════════════════════════════════

    @Test
    @DisplayName("toResponseList() maps all items in the list")
    void toResponseList_mapsAll() {
        List<Team> teams = List.of(
                buildTeam(1, 5, TeamStatus.OPEN),
                buildTeam(3, 3, TeamStatus.FULL)
        );
        List<TeamResponse> responses = mapper.toResponseList(teams);
        assertThat(responses).hasSize(2);
        assertThat(responses.get(1).status()).isEqualTo(TeamStatus.FULL);
    }
}
