package com.techhub.teamservice.mapper;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;
import com.techhub.teamservice.entity.TeamInvitation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    TeamInvitation toEntity(InvitationRequest request);

    InvitationResponse toResponse(TeamInvitation entity);
}

