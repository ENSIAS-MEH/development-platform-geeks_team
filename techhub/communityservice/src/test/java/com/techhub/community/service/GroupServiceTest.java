package com.techhub.community.service;

import com.techhub.community.dto.GroupRequest;
import com.techhub.community.dto.GroupResponse;
import com.techhub.community.dto.GroupMemberResponse;
import com.techhub.community.entity.Group;
import com.techhub.community.entity.GroupMember;
import com.techhub.community.enums.MemberRole;
import com.techhub.community.enums.Topic;
import com.techhub.community.exception.DuplicateMemberException;
import com.techhub.community.exception.ResourceNotFoundException;
import com.techhub.community.exception.UnauthorizedException;
import com.techhub.community.repository.GroupMemberRepository;
import com.techhub.community.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;
    @Mock
    private GroupMemberRepository memberRepository;

    @InjectMocks
    private GroupService groupService;

    private UUID ownerId;
    private UUID groupId;
    private Group sampleGroup;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();
        groupId = UUID.randomUUID();

        sampleGroup = Group.builder()
                .id(groupId)
                .name("Test Group")
                .description("A test group")
                .topic(Topic.WEB)
                .isPublic(true)
                .ownerId(ownerId)
                .memberCount(1)
                .createdAt(Instant.now())
                .build();
    }

    // ─── Create Group ───────────────────────────────────────────────────

    @Nested
    @DisplayName("createGroup")
    class CreateGroup {

        @Test
        @DisplayName("should create group and auto-add owner as OWNER member")
        void shouldCreateGroupSuccessfully() {
            GroupRequest request = GroupRequest.builder()
                    .name("New Group")
                    .description("Description")
                    .topic(Topic.AI_ML)
                    .isPublic(true)
                    .build();

            when(groupRepository.save(any(Group.class))).thenReturn(sampleGroup);
            when(memberRepository.save(any(GroupMember.class)))
                    .thenReturn(GroupMember.builder().build());

            GroupResponse response = groupService.createGroup(ownerId, request);

            assertThat(response).isNotNull();
            assertThat(response.getOwnerId()).isEqualTo(ownerId);
            verify(groupRepository).save(any(Group.class));
            verify(memberRepository).save(argThat(m -> m.getRole() == MemberRole.OWNER));
        }
    }

    // ─── Get Group ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("getGroupById")
    class GetGroupById {

        @Test
        @DisplayName("should return group when found")
        void shouldReturnGroup() {
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(sampleGroup));

            GroupResponse response = groupService.getGroupById(groupId);

            assertThat(response.getName()).isEqualTo("Test Group");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(groupRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> groupService.getGroupById(unknownId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ─── Delete Group ───────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteGroup")
    class DeleteGroup {

        @Test
        @DisplayName("should delete when requester is owner")
        void shouldDeleteWhenOwner() {
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(sampleGroup));

            groupService.deleteGroup(groupId, ownerId);

            verify(groupRepository).delete(sampleGroup);
        }

        @Test
        @DisplayName("should throw UnauthorizedException when non-owner tries to delete")
        void shouldThrowWhenNonOwner() {
            UUID anotherUser = UUID.randomUUID();
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(sampleGroup));

            assertThatThrownBy(() -> groupService.deleteGroup(groupId, anotherUser))
                    .isInstanceOf(UnauthorizedException.class);
        }
    }

    // ─── Join Group ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("joinGroup")
    class JoinGroup {

        @Test
        @DisplayName("should join successfully")
        void shouldJoinSuccessfully() {
            UUID newUserId = UUID.randomUUID();
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(sampleGroup));
            when(memberRepository.existsByGroupIdAndUserId(groupId, newUserId)).thenReturn(false);
            when(memberRepository.save(any(GroupMember.class)))
                    .thenAnswer(inv -> {
                        GroupMember m = inv.getArgument(0);
                        m.setId(UUID.randomUUID());
                        m.setJoinedAt(Instant.now());
                        return m;
                    });

            GroupMemberResponse response = groupService.joinGroup(groupId, newUserId);

            assertThat(response.getRole()).isEqualTo(MemberRole.MEMBER);
            verify(groupRepository).incrementMemberCount(groupId);
        }

        @Test
        @DisplayName("should throw DuplicateMemberException when already a member")
        void shouldThrowWhenAlreadyMember() {
            when(groupRepository.findById(groupId)).thenReturn(Optional.of(sampleGroup));
            when(memberRepository.existsByGroupIdAndUserId(groupId, ownerId)).thenReturn(true);

            assertThatThrownBy(() -> groupService.joinGroup(groupId, ownerId))
                    .isInstanceOf(DuplicateMemberException.class);
        }
    }

    // ─── Leave Group ────────────────────────────────────────────────────

    @Nested
    @DisplayName("leaveGroup")
    class LeaveGroup {

        @Test
        @DisplayName("should leave successfully as MEMBER")
        void shouldLeaveAsMember() {
            UUID memberId = UUID.randomUUID();
            GroupMember member = GroupMember.builder()
                    .id(UUID.randomUUID())
                    .groupId(groupId)
                    .userId(memberId)
                    .role(MemberRole.MEMBER)
                    .build();
            when(memberRepository.findByGroupIdAndUserId(groupId, memberId))
                    .thenReturn(Optional.of(member));

            groupService.leaveGroup(groupId, memberId);

            verify(memberRepository).delete(member);
            verify(groupRepository).decrementMemberCount(groupId);
        }

        @Test
        @DisplayName("should throw when owner tries to leave")
        void shouldThrowWhenOwnerLeaves() {
            GroupMember ownerMember = GroupMember.builder()
                    .id(UUID.randomUUID())
                    .groupId(groupId)
                    .userId(ownerId)
                    .role(MemberRole.OWNER)
                    .build();
            when(memberRepository.findByGroupIdAndUserId(groupId, ownerId))
                    .thenReturn(Optional.of(ownerMember));

            assertThatThrownBy(() -> groupService.leaveGroup(groupId, ownerId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Owner cannot leave");
        }
    }
}
