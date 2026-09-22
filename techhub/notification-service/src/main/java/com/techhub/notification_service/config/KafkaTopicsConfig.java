package com.techhub.notification_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class KafkaTopicsConfig {

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Value("${kafka.topics.user-password-changed}")
    private String userPasswordChangedTopic;

    @Value("${kafka.topics.team-invited:team-invited}")
    private String teamInvitedTopic;

    @Value("${kafka.topics.team-joined:team-joined}")
    private String teamJoinedTopic;
}