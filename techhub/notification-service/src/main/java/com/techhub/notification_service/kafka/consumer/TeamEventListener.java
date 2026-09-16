package com.techhub.notification_service.kafka.consumer;

import com.techhub.notification_service.kafka.event.TeamInvitedEvent;
import com.techhub.notification_service.kafka.event.TeamJoinedEvent;
import com.techhub.notification_service.service.NotificationService;
import com.techhub.notification_service.util.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeamEventListener {

    private final EventMapper eventMapper;
    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.team-invited}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTeamInvited(ConsumerRecord<String, Object> record) {
        log.info("[TeamEventListener] Received event on topic={} key={} offset={}",
                record.topic(), record.key(), record.offset());
        try {
            TeamInvitedEvent event = eventMapper.toTeamInvitedEvent(record.value());
            log.info("[TeamEventListener] Processing TeamInvitedEvent invitationId={} receiverId={}",
                    event.getInvitationId(), event.getReceiverId());
            notificationService.handleTeamInvited(event);
        } catch (Exception e) {
            log.error("[TeamEventListener] Failed to process TeamInvitedEvent — skipping. Reason: {}",
                    e.getMessage());
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.team-joined}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onTeamJoined(ConsumerRecord<String, Object> record) {
        log.info("[TeamEventListener] Received event on topic={} key={} offset={}",
                record.topic(), record.key(), record.offset());
        try {
            TeamJoinedEvent event = eventMapper.toTeamJoinedEvent(record.value());
            log.info("[TeamEventListener] Processing TeamJoinedEvent teamId={} userId={}",
                    event.getTeamId(), event.getUserId());
            notificationService.handleTeamJoined(event);
        } catch (Exception e) {
            log.error("[TeamEventListener] Failed to process TeamJoinedEvent — skipping. Reason: {}",
                    e.getMessage());
        }
    }
}
