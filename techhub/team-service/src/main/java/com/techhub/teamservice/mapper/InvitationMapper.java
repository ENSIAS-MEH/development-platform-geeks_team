package com.techhub.teamservice.mapper;

import com.techhub.teamservice.dto.InvitationRequest;
import com.techhub.teamservice.dto.InvitationResponse;
import com.techhub.teamservice.entity.TeamInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvitationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "senderId", ignore = true)
    @Mapping(target = "receiverId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "expirationTime", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TeamInvitation toEntity(InvitationRequest request);

    InvitationResponse toResponse(TeamInvitation entity);
}

