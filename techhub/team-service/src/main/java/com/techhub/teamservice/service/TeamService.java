package com.techhub.teamservice.service;

import com.techhub.teamservice.dto.TeamMemberResponse;
import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TeamService {

    TeamResponse createTeam(TeamRequest request);

    TeamResponse getTeam(UUID id);

    void deleteTeam(UUID id);

    Page<TeamResponse> getMyTeams(int page, int size);

    Page<TeamResponse> searchTeams(String query, int page, int size);

    Page<TeamMemberResponse> getTeamMembers(UUID teamId, int page, int size);

    TeamResponse updateTeam(UUID teamId, TeamRequest request);

    void leaveTeam(UUID teamId);

    void removeMember(UUID teamId, UUID memberId);
}

