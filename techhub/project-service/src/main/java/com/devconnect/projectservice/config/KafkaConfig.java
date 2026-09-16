package com.devconnect.projectservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.*;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean public NewTopic projectCreatedTopic() { return TopicBuilder.name("project-created").partitions(3).replicas(1).build(); }
    @Bean public NewTopic projectJoinedTopic() { return TopicBuilder.name("project-joined").partitions(3).replicas(1).build(); }
}
