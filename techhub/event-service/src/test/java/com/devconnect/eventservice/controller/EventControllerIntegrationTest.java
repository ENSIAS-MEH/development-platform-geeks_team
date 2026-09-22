package com.devconnect.eventservice.controller;

import com.devconnect.eventservice.dto.CreateEventRequest;
import com.devconnect.eventservice.enums.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {"event-created", "event-published", "user-registered-event"})
@org.junit.jupiter.api.Tag("integration")
class EventControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("test_event_db")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.cache.type", () -> "none");
        registry.add("spring.autoconfigure.exclude", () ->
            "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration");
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private EmbeddedKafkaBroker embeddedKafka;

    private final UUID organizerId = UUID.randomUUID();
    private final UUID participantId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Flyway migrations run on context startup
    }

    @Test
    void fullEventFlow() throws Exception {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("Integration Test Hackathon");
        request.setDescription("Test event for integration");
        request.setType(EventType.HACKATHON);
        request.setStartDate(LocalDateTime.now().plusDays(10));
        request.setEndDate(LocalDateTime.now().plusDays(12));
        request.setLocation("Casablanca");
        request.setMaxParticipants(100);

        MvcResult createResult = mockMvc.perform(post("/api/events")
                .header("X-User-Id", organizerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Integration Test Hackathon"))
            .andExpect(jsonPath("$.status").value("DRAFT"))
            .andReturn();

        String eventId = objectMapper.readTree(createResult.getResponse().getContentAsString())
            .get("id").asText();

        mockMvc.perform(post("/api/events/{id}/publish", eventId)
                .header("X-User-Id", organizerId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("PUBLISHED"));

        mockMvc.perform(post("/api/events/{id}/register", eventId)
                .header("X-User-Id", participantId.toString()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(participantId.toString()));

        mockMvc.perform(get("/api/events/{id}/participants", eventId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].userId").value(participantId.toString()));

        mockMvc.perform(delete("/api/events/{id}/register", eventId)
                .header("X-User-Id", participantId.toString()))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/events")
                .param("type", "HACKATHON")
                .param("status", "PUBLISHED"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());

        mockMvc.perform(post("/api/events/{id}/register", eventId)
                .header("X-User-Id", participantId.toString()))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/events/{id}/register", eventId)
                .header("X-User-Id", participantId.toString()))
            .andExpect(status().isConflict());

        assertKafkaMessagePublished("user-registered-event");
    }

    private void assertKafkaMessagePublished(String topic) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group-" + UUID.randomUUID(), "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "com.devconnect.*");

        try (Consumer<Object, Object> consumer = new DefaultKafkaConsumerFactory<>(consumerProps).createConsumer()) {
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, topic);
            ConsumerRecords<Object, Object> records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10));
            assertThat(records.count()).isGreaterThan(0);
        }
    }
}
