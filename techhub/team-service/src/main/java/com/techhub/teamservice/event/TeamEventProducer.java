package com.techhub.teamservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topics.team-created}")
    private String teamCreatedTopic;

    @Value("${spring.kafka.topics.team-invited}")
    private String teamInvitedTopic;

    @Value("${spring.kafka.topics.team-joined}")
    private String teamJoinedTopic;

    @Value("${spring.kafka.topics.team-full}")
    private String teamFullTopic;

    @Value("${spring.kafka.topics.team-left}")
    private String teamLeftTopic;

    public void sendTeamCreated(Object payload) {
        send(teamCreatedTopic, payload);
    }

    public void sendTeamInvited(Object payload) {
        send(teamInvitedTopic, payload);
    }

    public void sendTeamJoined(Object payload) {
        send(teamJoinedTopic, payload);
    }

    public void sendTeamFull(Object payload) {
        send(teamFullTopic, payload);
    }

    public void sendTeamLeft(Object payload) {
        send(teamLeftTopic, payload);
    }

    private void send(String topic, Object payload) {
        kafkaTemplate.send(topic, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[TeamEventProducer] Failed to send to topic={} reason={}", topic, ex.getMessage());
                    } else {
                        log.debug("[TeamEventProducer] Sent to topic={} offset={}",
                                topic, result.getRecordMetadata().offset());
                    }
                });
    }
}

