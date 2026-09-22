package com.devconnect.eventservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;

/** Declares Kafka topics created automatically if they don't exist. */
@Configuration
public class KafkaConfig {

    @Bean public NewTopic eventCreatedTopic() { return TopicBuilder.name("event-created").partitions(3).replicas(1).build(); }
    @Bean public NewTopic eventPublishedTopic() { return TopicBuilder.name("event-published").partitions(3).replicas(1).build(); }
    @Bean public NewTopic userRegisteredTopic() { return TopicBuilder.name("user-registered-event").partitions(3).replicas(1).build(); }
}
