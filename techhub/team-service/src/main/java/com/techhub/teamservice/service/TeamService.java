package com.techhub.teamservice.service;

import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;

import java.util.UUID;

public interface TeamService {

    TeamResponse createTeam(TeamRequest request);

    TeamResponse getTeam(UUID id);

    void deleteTeam(UUID id);
}

