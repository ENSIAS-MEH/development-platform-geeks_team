package com.techhub.teamservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;

/**
 * KafkaConfig
 *
 * <p>Declares topics with sensible defaults (3 partitions, 1 replica for dev;
 * override in production via Kafka AdminClient or Terraform).
 * Idempotent producer is enabled in application.yml.
 */
@Configuration
public class KafkaConfig {

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

    // ── Topic beans (auto-created if auto.create.topics.enable = true) ──

    @Bean
    public NewTopic teamCreatedTopic() {
        return TopicBuilder.name(teamCreatedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic teamInvitedTopic() {
        return TopicBuilder.name(teamInvitedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic teamJoinedTopic() {
        return TopicBuilder.name(teamJoinedTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic teamFullTopic() {
        return TopicBuilder.name(teamFullTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic teamLeftTopic() {
        return TopicBuilder.name(teamLeftTopic).partitions(3).replicas(1).build();
    }

    // ── KafkaTemplate — typed to String key + Object value (serialized as JSON) ──

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    // ── Transaction Manager (optional — enable if you need exactly-once semantics) ──

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }
}

