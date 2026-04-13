package com.techhub.community.service;

import com.techhub.community.dto.*;
import com.techhub.community.entity.Group;
import com.techhub.community.entity.GroupMember;
import com.techhub.community.enums.MemberRole;
import com.techhub.community.enums.Topic;
import com.techhub.community.exception.DuplicateMemberException;
import com.techhub.community.exception.ResourceNotFoundException;
import com.techhub.community.exception.UnauthorizedException;
import com.techhub.community.repository.GroupMemberRepository;
import com.techhub.community.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;

    // ─── Create ─────────────────────────────────────────────────────────

    @Transactional
    public GroupResponse createGroup(UUID ownerId, GroupRequest request) {
        Group group = Group.builder()
                .name(request.getName())
                .description(request.getDescription())
                .topic(request.getTopic())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .ownerId(ownerId)
                .memberCount(1) // owner counts as member
                .build();

        group = groupRepository.save(group);

        // Auto-add owner as OWNER member
        GroupMember ownerMember = GroupMember.builder()
                .groupId(group.getId())
                .userId(ownerId)
                .role(MemberRole.OWNER)
                .build();
        memberRepository.save(ownerMember);

        return toResponse(group);
    }

    // ─── Read ───────────────────────────────────────────────────────────

    @Cacheable(value = "groupDetails", key = "#groupId")
    public GroupResponse getGroupById(UUID groupId) {
        Group group = findGroupOrThrow(groupId);
        return toResponse(group);
    }

    public Page<GroupResponse> getPublicGroups(Topic topic, Pageable pageable) {
        Page<Group> page;
        if (topic != null) {
            page = groupRepository.findByTopicAndIsPublicTrue(topic, pageable);
        } else {
            page = groupRepository.findByIsPublicTrue(pageable);
        }
        return page.map(this::toResponse);
    }

    public Page<GroupResponse> searchGroups(String keyword, Pageable pageable) {
        return groupRepository
                .findByNameContainingIgnoreCaseAndIsPublicTrue(keyword, pageable)
                .map(this::toResponse);
    }

    // ─── Update ─────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "groupDetails", key = "#groupId")
    public GroupResponse updateGroup(UUID groupId, UUID userId, GroupRequest request) {
        Group group = findGroupOrThrow(groupId);
        assertOwnerOrModerator(groupId, userId);

        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setTopic(request.getTopic());
        if (request.getIsPublic() != null) {
            group.setIsPublic(request.getIsPublic());
        }

        return toResponse(groupRepository.save(group));
    }

    // ─── Delete ─────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "groupDetails", key = "#groupId")
    public void deleteGroup(UUID groupId, UUID userId) {
        Group group = findGroupOrThrow(groupId);
        if (!group.getOwnerId().equals(userId)) {
            throw new UnauthorizedException("Only the owner can delete this group");
        }
        groupRepository.delete(group);
    }

    // ─── Membership ─────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "groupDetails", key = "#groupId")
    public GroupMemberResponse joinGroup(UUID groupId, UUID userId) {
        findGroupOrThrow(groupId);

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new DuplicateMemberException("User is already a member of this group");
        }

        GroupMember member = GroupMember.builder()
                .groupId(groupId)
                .userId(userId)
                .role(MemberRole.MEMBER)
                .build();
        member = memberRepository.save(member);

        // Atomic counter increment
        groupRepository.incrementMemberCount(groupId);

        return toMemberResponse(member);
    }

    @Transactional
    @CacheEvict(value = "groupDetails", key = "#groupId")
    public void leaveGroup(UUID groupId, UUID userId) {
        GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership", "userId", userId));

        if (member.getRole() == MemberRole.OWNER) {
            throw new IllegalArgumentException("Owner cannot leave the group. Transfer ownership or delete the group.");
        }

        memberRepository.delete(member);
        groupRepository.decrementMemberCount(groupId);
    }

    public Page<GroupMemberResponse> getMembers(UUID groupId, Pageable pageable) {
        findGroupOrThrow(groupId);
        return memberRepository.findByGroupId(groupId, pageable).map(this::toMemberResponse);
    }

    @Transactional
    public GroupMemberResponse updateMemberRole(UUID groupId, UUID targetUserId, UUID requesterId, MemberRole newRole) {
        assertOwnerOrModerator(groupId, requesterId);

        GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership", "userId", targetUserId));

        if (member.getRole() == MemberRole.OWNER) {
            throw new IllegalArgumentException("Cannot change the owner's role");
        }

        member.setRole(newRole);
        return toMemberResponse(memberRepository.save(member));
    }

    @Transactional
    @CacheEvict(value = "groupDetails", key = "#groupId")
    public void removeMember(UUID groupId, UUID targetUserId, UUID requesterId) {
        assertOwnerOrModerator(groupId, requesterId);

        GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership", "userId", targetUserId));

        if (member.getRole() == MemberRole.OWNER) {
            throw new IllegalArgumentException("Cannot remove the owner");
        }

        memberRepository.delete(member);
        groupRepository.decrementMemberCount(groupId);
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    public Group findGroupOrThrow(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
    }

    private void assertOwnerOrModerator(UUID groupId, UUID userId) {
        GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new UnauthorizedException("User is not a member of this group"));
        if (member.getRole() != MemberRole.OWNER && member.getRole() != MemberRole.MODERATOR) {
            throw new UnauthorizedException("Only the owner or a moderator can perform this action");
        }
    }

    private GroupResponse toResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .topic(group.getTopic())
                .isPublic(group.getIsPublic())
                .ownerId(group.getOwnerId())
                .memberCount(group.getMemberCount())
                .createdAt(group.getCreatedAt())
                .build();
    }

    private GroupMemberResponse toMemberResponse(GroupMember member) {
        return GroupMemberResponse.builder()
                .id(member.getId())
                .groupId(member.getGroupId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
