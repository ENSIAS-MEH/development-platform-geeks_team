package com.techhub.community.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_POST_CREATED = "post-created";
    public static final String TOPIC_COMMENT_ADDED = "comment-added";

    @Bean
    public NewTopic postCreatedTopic() {
        return TopicBuilder.name(TOPIC_POST_CREATED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic commentAddedTopic() {
        return TopicBuilder.name(TOPIC_COMMENT_ADDED)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
