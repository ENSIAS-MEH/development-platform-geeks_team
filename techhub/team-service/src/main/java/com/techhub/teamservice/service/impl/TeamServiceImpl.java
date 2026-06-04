package com.techhub.teamservice.service.impl;

import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import com.techhub.teamservice.entity.Team;
import com.techhub.teamservice.entity.TeamMember;
import com.techhub.teamservice.entity.enums.MemberRole;
import com.techhub.teamservice.entity.enums.TeamStatus;
import com.techhub.teamservice.exception.ResourceNotFoundException;
import com.techhub.teamservice.util.SecurityUtils;
import com.techhub.teamservice.mapper.TeamMapper;
import com.techhub.teamservice.repository.TeamMemberRepository;
import com.techhub.teamservice.repository.TeamRepository;
import com.techhub.teamservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        List<TeamMember> members = teamMemberRepository.findAllByTeamId(id);
        if (!members.isEmpty()) {
            teamMemberRepository.deleteAll(members);
        }

        teamRepository.deleteById(team.getId());
    }
}

