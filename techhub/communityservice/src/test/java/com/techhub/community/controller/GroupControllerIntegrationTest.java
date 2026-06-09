package com.techhub.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techhub.community.dto.GroupRequest;
import com.techhub.community.enums.Topic;
import com.techhub.community.config.TestKafkaConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GroupController.
 * Uses H2 in-memory database (test profile).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
class GroupControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_HEADER = "X-User-Id";

    // ─── Create & Retrieve ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/groups → 201 + GET → returns created group")
    void createAndRetrieveGroup() throws Exception {
        UUID userId = UUID.randomUUID();

        GroupRequest request = GroupRequest.builder()
                .name("Integration Test Group")
                .description("An integration test group")
                .topic(Topic.DEVOPS)
                .isPublic(true)
                .build();

        // Create
        String responseBody = mockMvc.perform(post("/api/groups")
                .header(USER_HEADER, userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Test Group"))
                .andExpect(jsonPath("$.topic").value("DEVOPS"))
                .andExpect(jsonPath("$.ownerId").value(userId.toString()))
                .andExpect(jsonPath("$.memberCount").value(1))
                .andReturn().getResponse().getContentAsString();

        String groupId = objectMapper.readTree(responseBody).get("id").asText();

        // Retrieve
        mockMvc.perform(get("/api/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Integration Test Group"));
    }

    // ─── Validation ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/groups with blank name → 400")
    void createGroupValidationFails() throws Exception {
        UUID userId = UUID.randomUUID();

        GroupRequest request = GroupRequest.builder()
                .name("")
                .topic(Topic.WEB)
                .build();

        mockMvc.perform(post("/api/groups")
                .header(USER_HEADER, userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists());
    }

    // ─── Join & Leave ───────────────────────────────────────────────────

    @Test
    @DisplayName("Full join/leave lifecycle")
    void joinAndLeaveGroup() throws Exception {
        UUID ownerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        // Create group
        GroupRequest request = GroupRequest.builder()
                .name("Join Test Group")
                .topic(Topic.SECURITY)
                .build();

        String responseBody = mockMvc.perform(post("/api/groups")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String groupId = objectMapper.readTree(responseBody).get("id").asText();

        // Join
        mockMvc.perform(post("/api/groups/" + groupId + "/join")
                .header(USER_HEADER, memberId.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("MEMBER"));

        // Duplicate join → 409
        mockMvc.perform(post("/api/groups/" + groupId + "/join")
                .header(USER_HEADER, memberId.toString()))
                .andExpect(status().isConflict());

        // Leave
        mockMvc.perform(delete("/api/groups/" + groupId + "/leave")
                .header(USER_HEADER, memberId.toString()))
                .andExpect(status().isNoContent());
    }

    // ─── Get Members ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/groups/{id}/members → returns paginated members")
    void getGroupMembers() throws Exception {
        UUID ownerId = UUID.randomUUID();

        GroupRequest request = GroupRequest.builder()
                .name("Members Test")
                .topic(Topic.DATA)
                .build();

        String responseBody = mockMvc.perform(post("/api/groups")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String groupId = objectMapper.readTree(responseBody).get("id").asText();

        mockMvc.perform(get("/api/groups/" + groupId + "/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].role").value("OWNER"));
    }

    // ─── Not Found ──────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/groups/{unknownId} → 404")
    void getGroupNotFound() throws Exception {
        mockMvc.perform(get("/api/groups/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // ─── Delete ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/groups/{id} by owner → 204, then GET → 404")
    void deleteGroup() throws Exception {
        UUID ownerId = UUID.randomUUID();

        GroupRequest request = GroupRequest.builder()
                .name("Delete Me")
                .topic(Topic.MOBILE)
                .build();

        String responseBody = mockMvc.perform(post("/api/groups")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String groupId = objectMapper.readTree(responseBody).get("id").asText();

        // Delete
        mockMvc.perform(delete("/api/groups/" + groupId)
                .header(USER_HEADER, ownerId.toString()))
                .andExpect(status().isNoContent());

        // Verify gone
        mockMvc.perform(get("/api/groups/" + groupId))
                .andExpect(status().isNotFound());
    }
}
