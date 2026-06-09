package com.devconnect.projectservice.kafka;

import com.devconnect.projectservice.kafka.event.ProjectCreatedEvent;
import com.devconnect.projectservice.kafka.event.ProjectJoinedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class ProjectProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProjectCreated(ProjectCreatedEvent event) {
        kafkaTemplate.send("project-created", event.getId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) log.error("Failed to publish project-created for {}", event.getId(), ex);
                else log.info("Published project-created: {}", event.getId());
            });
    }

    public void publishProjectJoined(ProjectJoinedEvent event) {
        kafkaTemplate.send("project-joined", event.getProjectId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) log.error("Failed to publish project-joined for {}", event.getProjectId(), ex);
            });
    }
}
