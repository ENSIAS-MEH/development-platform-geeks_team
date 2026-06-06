package com.techhub.notification_service.kafka.consumer;

import com.techhub.notification_service.kafka.event.UserPasswordChangedEvent;
import com.techhub.notification_service.kafka.event.UserRegisteredEvent;
import com.techhub.notification_service.service.NotificationService;
import com.techhub.notification_service.util.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EventMapper eventMapper;
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.user-registered}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onUserRegistered(ConsumerRecord<String, Object> record) {
        log.info("[UserEventListener] Received event on topic={} key={} offset={}",
                record.topic(), record.key(), record.offset());
        try {
            UserRegisteredEvent event = eventMapper.toUserRegisteredEvent(record.value());
            log.info("[UserEventListener] Processing UserRegisteredEvent for userId={} email={}",
                    event.getUserId(), event.getEmail());
            notificationService.handleUserRegistered(event);
        } catch (Exception e) {
            log.error("[UserEventListener] Failed to process UserRegisteredEvent — skipping. Reason: {}",
                    e.getMessage());
            // Do NOT rethrow — prevents consumer from stopping on a bad message
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.user-password-changed}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onUserPasswordChanged(ConsumerRecord<String, Object> record) {
        log.info("[UserEventListener] Received event on topic={} key={} offset={}",
                record.topic(), record.key(), record.offset());
        try {
            UserPasswordChangedEvent event = eventMapper.toUserPasswordChangedEvent(record.value());
            log.info("[UserEventListener] Processing UserPasswordChangedEvent for userId={} email={}",
                    event.getUserId(), event.getEmail());
            notificationService.handleUserPasswordChanged(event);
        } catch (Exception e) {
            log.error("[UserEventListener] Failed to process UserPasswordChangedEvent — skipping. Reason: {}",
                    e.getMessage());
        }
    }
}