package com.techhub.teamservice.mapper;

import com.techhub.teamservice.dto.TeamRequest;
import com.techhub.teamservice.dto.TeamResponse;
import com.techhub.teamservice.entity.Team;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    Team toEntity(TeamRequest request);

    TeamResponse toResponse(Team entity);
}

