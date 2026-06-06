package com.techhub.community.service;

import com.techhub.community.config.KafkaConfig;
import com.techhub.community.dto.CommentAddedEvent;
import com.techhub.community.dto.PostCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPostCreated(PostCreatedEvent event) {
        kafkaTemplate.send(KafkaConfig.TOPIC_POST_CREATED, event.getPostId().toString(), event);
    }

    public void publishCommentAdded(CommentAddedEvent event) {
        kafkaTemplate.send(KafkaConfig.TOPIC_COMMENT_ADDED, event.getCommentId().toString(), event);
    }
}
