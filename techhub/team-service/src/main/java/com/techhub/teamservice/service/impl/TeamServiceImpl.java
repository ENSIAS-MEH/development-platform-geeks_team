package com.techhub.teamservice.service.impl;

import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import com.techhub.teamservice.mapper.TeamMapper;
import com.techhub.teamservice.repository.TeamRepository;
import com.techhub.teamservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    public TeamResponse createTeam(TeamRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public TeamResponse getTeam(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteTeam(UUID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

