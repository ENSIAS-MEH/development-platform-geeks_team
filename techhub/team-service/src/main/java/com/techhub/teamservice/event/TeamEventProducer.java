package com.techhub.teamservice.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendTeamCreated(Object eventPayload) {
        // stub: in real implementation send to configured topic
        // kafkaTemplate.send("team-created", eventPayload);
    }
}

