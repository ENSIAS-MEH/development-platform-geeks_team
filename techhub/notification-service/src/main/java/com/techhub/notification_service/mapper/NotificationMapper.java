package com.techhub.notification_service.mapper;

import com.techhub.notification_service.dto.NotificationResponse;
import com.techhub.notification_service.entity.Notification;
import com.techhub.notification_service.entity.NotificationStatus;
import com.techhub.notification_service.entity.NotificationType;
import com.techhub.notification_service.kafka.event.TeamInvitedEvent;
import com.techhub.notification_service.kafka.event.TeamJoinedEvent;
import com.techhub.notification_service.kafka.event.UserPasswordChangedEvent;
import com.techhub.notification_service.kafka.event.UserRegisteredEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {NotificationType.class, NotificationStatus.class, java.util.UUID.class})
public interface NotificationMapper {

    // Entity → Response DTO
    NotificationResponse toResponse(Notification notification);

    // UserRegisteredEvent → Entity
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "sentAt",         ignore = true)
    @Mapping(target = "failureReason",  ignore = true)
    @Mapping(target = "read",           ignore = true)
    @Mapping(target = "recipientEmail", source = "email")
    @Mapping(target = "type",           expression = "java(NotificationType.WELCOME_EMAIL)")
    @Mapping(target = "status",         expression = "java(NotificationStatus.PENDING)")
    @Mapping(target = "title",          expression = "java(\"Welcome to TechHub\")")
    @Mapping(target = "message",        expression = "java(\"Hi \" + event.getDisplayName() + \", your account is ready.\")")
    Notification fromUserRegisteredEvent(UserRegisteredEvent event);

    // UserPasswordChangedEvent → Entity
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "sentAt",         ignore = true)
    @Mapping(target = "failureReason",  ignore = true)
    @Mapping(target = "read",           ignore = true)
    @Mapping(target = "recipientEmail", source = "email")
    @Mapping(target = "type",           expression = "java(NotificationType.PASSWORD_CHANGED_EMAIL)")
    @Mapping(target = "status",         expression = "java(NotificationStatus.PENDING)")
    @Mapping(target = "title",          expression = "java(\"Password changed\")")
    @Mapping(target = "message",        expression = "java(\"Your TechHub password was successfully updated.\")")
    Notification fromUserPasswordChangedEvent(UserPasswordChangedEvent event);

    // TeamInvitedEvent → Entity
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "sentAt",         ignore = true)
    @Mapping(target = "failureReason",  ignore = true)
    @Mapping(target = "read",           ignore = true)
    @Mapping(target = "userId",         source = "receiverId")
    @Mapping(target = "recipientEmail", ignore = true)
    @Mapping(target = "displayName",    ignore = true)
    @Mapping(target = "type",           expression = "java(NotificationType.TEAM_INVITED)")
    @Mapping(target = "status",         expression = "java(NotificationStatus.SENT)")
    @Mapping(target = "eventId",        expression = "java(event.getInvitationId() != null ? event.getInvitationId().toString() : java.util.UUID.randomUUID().toString())")
    @Mapping(target = "eventType",      expression = "java(\"team-invited\")")
    @Mapping(target = "title",          expression = "java(\"Team Invitation\")")
    @Mapping(target = "message",        expression = "java(\"You have been invited to join a team.\")")
    Notification fromTeamInvitedEvent(TeamInvitedEvent event);

    // TeamJoinedEvent → Entity
    @Mapping(target = "id",             ignore = true)
    @Mapping(target = "createdAt",      ignore = true)
    @Mapping(target = "sentAt",         ignore = true)
    @Mapping(target = "failureReason",  ignore = true)
    @Mapping(target = "read",           ignore = true)
    @Mapping(target = "recipientEmail", ignore = true)
    @Mapping(target = "displayName",    ignore = true)
    @Mapping(target = "type",           expression = "java(NotificationType.TEAM_JOINED)")
    @Mapping(target = "status",         expression = "java(NotificationStatus.SENT)")
    @Mapping(target = "eventId",        expression = "java(event.getTeamId() + \"_\" + event.getUserId())")
    @Mapping(target = "eventType",      expression = "java(\"team-joined\")")
    @Mapping(target = "title",          expression = "java(\"Joined a team\")")
    @Mapping(target = "message",        expression = "java(\"You successfully joined a team.\")")
    Notification fromTeamJoinedEvent(TeamJoinedEvent event);
}