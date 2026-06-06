package com.techhub.teamservice.service.impl;

import com.techhub.teamservice.dto.TeamMemberResponse;
import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamMember;
import com.techhub.teamservice.entity.enums.MemberRole;
import com.techhub.teamservice.entity.enums.TeamStatus;
import com.techhub.teamservice.exception.ForbiddenException;
import com.techhub.teamservice.exception.ResourceNotFoundException;
import com.techhub.teamservice.util.SecurityUtils;
import com.techhub.teamservice.mapper.TeamMapper;
import com.techhub.teamservice.repository.TeamMemberRepository;
import com.techhub.teamservice.repository.TeamRepository;
import com.techhub.teamservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMapper teamMapper;

    @Override
    public TeamResponse createTeam(TeamRequest request) {
        // owner comes from authentication context
        UUID ownerId = SecurityUtils.getCurrentUserId();

        Team entity = teamMapper.toEntity(request);
        Instant now = Instant.now();
        entity.setId(UUID.randomUUID());
        entity.setOwnerId(ownerId);
        entity.setCurrentMembers(1);
        entity.setStatus(TeamStatus.OPEN);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        Team saved = teamRepository.save(entity);

        TeamMember owner = TeamMember.builder()
                .id(UUID.randomUUID())
                .teamId(saved.getId())
                .userId(ownerId)
                .role(MemberRole.OWNER)
                .joinedAt(now)
                .build();

        teamMemberRepository.save(owner);

        return teamMapper.toResponse(saved);
    }

    @Override
    public TeamResponse getTeam(UUID id) {
        Team t = teamRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.team(id));
        return teamMapper.toResponse(t);
    }

    @Override
    public void deleteTeam(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.team(id));

        List<TeamMember> members = teamMemberRepository.findByTeamIdOrderByJoinedAtAsc(id);
        if (!members.isEmpty()) {
            teamMemberRepository.deleteAll(members);
        }

        teamRepository.deleteById(team.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamResponse> getMyTeams(int page, int size) {
        UUID userId = SecurityUtils.getCurrentUserId();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return teamRepository.findAllTeamsForUserPageable(userId, pageable)
                .map(team -> teamMapper.withOwnerFlag(
                        teamMapper.toResponse(team),
                        team.getOwnerId().equals(userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamResponse> searchTeams(String query, int page, int size) {
        UUID callerId = SecurityUtils.getCurrentUserIdOrNull();
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return teamRepository.findByNameContainingIgnoreCase(query, pageable)
                .map(team -> teamMapper.withOwnerFlag(
                        teamMapper.toResponse(team),
                        callerId != null && team.getOwnerId().equals(callerId)));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TeamMemberResponse> getTeamMembers(UUID teamId, int page, int size) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.team(teamId));
        return teamMemberRepository
                .findByTeamIdOrderByJoinedAtAsc(teamId, PageRequest.of(page, size))
                .map(teamMapper::toMemberResponse);
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(UUID teamId, TeamRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Team team = teamRepository.findByIdAndOwnerId(teamId, userId)
                .orElseThrow(ForbiddenException::notOwner);

        team.setName(request.name());
        team.setDescription(request.description());

        if (request.maxMembers() != null) {
            if (request.maxMembers() < team.getCurrentMembers()) {
                throw new IllegalArgumentException(
                        "Max members (" + request.maxMembers() + ") cannot be less than current member count ("
                        + team.getCurrentMembers() + ")");
            }
            team.setMaxMembers(request.maxMembers());
            if (team.isFull()) {
                team.setStatus(TeamStatus.FULL);
            } else if (team.getStatus() == TeamStatus.FULL) {
                team.setStatus(TeamStatus.OPEN);
            }
        }

        return teamMapper.withOwnerFlag(teamMapper.toResponse(teamRepository.save(team)), true);
    }

    @Override
    @Transactional
    public void leaveTeam(UUID teamId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> ResourceNotFoundException.team(teamId));

        if (team.getOwnerId().equals(userId)) {
            throw new ForbiddenException("Team owner cannot leave — delete the team instead");
        }

        TeamMember membership = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(ForbiddenException::notMember);

        teamMemberRepository.delete(membership);
        team.decrementMembers();
        teamRepository.save(team);
    }

    @Override
    @Transactional
    public void removeMember(UUID teamId, UUID memberId) {
        UUID callerId = SecurityUtils.getCurrentUserId();
        Team team = teamRepository.findByIdAndOwnerId(teamId, callerId)
                .orElseThrow(ForbiddenException::notOwner);

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> ResourceNotFoundException.member(memberId));

        if (!member.getTeamId().equals(teamId)) {
            throw ResourceNotFoundException.member(memberId);
        }
        if (member.getUserId().equals(callerId)) {
            throw new ForbiddenException("Owner cannot remove themselves — delete the team instead");
        }

        teamMemberRepository.delete(member);
        team.decrementMembers();
        teamRepository.save(team);
    }
}

