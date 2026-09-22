package com.techhub.notification_service.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techhub.notification_service.kafka.event.TeamInvitedEvent;
import com.techhub.notification_service.kafka.event.TeamJoinedEvent;
import com.techhub.notification_service.kafka.event.UserPasswordChangedEvent;
import com.techhub.notification_service.kafka.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final ObjectMapper objectMapper;

    public UserRegisteredEvent toUserRegisteredEvent(Object value) {
        if (value instanceof UserRegisteredEvent event) {
            return event;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) value;
        return objectMapper.convertValue(map, UserRegisteredEvent.class);
    }

    public UserPasswordChangedEvent toUserPasswordChangedEvent(Object value) {
        if (value instanceof UserPasswordChangedEvent event) {
            return event;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) value;
        return objectMapper.convertValue(map, UserPasswordChangedEvent.class);
    }

    public TeamInvitedEvent toTeamInvitedEvent(Object value) {
        if (value instanceof TeamInvitedEvent event) {
            return event;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) value;
        return objectMapper.convertValue(map, TeamInvitedEvent.class);
    }

    public TeamJoinedEvent toTeamJoinedEvent(Object value) {
        if (value instanceof TeamJoinedEvent event) {
            return event;
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) value;
        return objectMapper.convertValue(map, TeamJoinedEvent.class);
    }
}
