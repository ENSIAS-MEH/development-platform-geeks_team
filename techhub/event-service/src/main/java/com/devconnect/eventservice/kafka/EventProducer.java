package com.devconnect.eventservice.kafka;

import com.devconnect.eventservice.kafka.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer for event-related domain events.
 * All sends are fire-and-forget with logging on failure.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes an event-created domain event to Kafka.
     *
     * @param event the event creation payload
     */
    public void publishEventCreated(EventCreatedEvent event) {
        kafkaTemplate.send(KafkaTopics.EVENT_CREATED, event.getId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) log.error("Failed to publish event-created for {}", event.getId(), ex);
                else log.info("Published event-created: {}", event.getId());
            });
    }

    /**
     * Publishes an event-published domain event to Kafka.
     *
     * @param event the event published payload
     */
    public void publishEventPublished(EventPublishedEvent event) {
        kafkaTemplate.send(KafkaTopics.EVENT_PUBLISHED, event.getEventId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) log.error("Failed to publish event-published for {}", event.getEventId(), ex);
            });
    }

    /**
     * Publishes a user-registered-event domain event to Kafka.
     *
     * @param event the registration payload
     */
    public void publishUserRegistered(UserRegisteredEvent event) {
        kafkaTemplate.send(KafkaTopics.USER_REGISTERED_EVENT, event.getUserId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) log.error("Failed to publish user-registered for user {} event {}", event.getUserId(), event.getEventId(), ex);
            });
    }
}
