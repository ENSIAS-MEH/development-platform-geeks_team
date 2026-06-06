package com.techhub.notification_service.service;

import com.techhub.notification_service.dto.NotificationRequest;
import com.techhub.notification_service.dto.NotificationResponse;
import com.techhub.notification_service.entity.NotificationStatus;
import com.techhub.notification_service.kafka.event.TeamInvitedEvent;
import com.techhub.notification_service.kafka.event.TeamJoinedEvent;
import com.techhub.notification_service.kafka.event.UserPasswordChangedEvent;
import com.techhub.notification_service.kafka.event.UserRegisteredEvent;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    // Kafka-triggered
    void handleUserRegistered(UserRegisteredEvent event);
    void handleUserPasswordChanged(UserPasswordChangedEvent event);
    void handleTeamInvited(TeamInvitedEvent event);
    void handleTeamJoined(TeamJoinedEvent event);

    // REST-triggered
    NotificationResponse sendManual(NotificationRequest request);

    // Queries
    List<NotificationResponse> getByUserId(UUID userId);
    List<NotificationResponse> getByStatus(NotificationStatus status);

    // Read state
    NotificationResponse markAsRead(UUID id);
    void markAllAsReadForUser(UUID userId);
}