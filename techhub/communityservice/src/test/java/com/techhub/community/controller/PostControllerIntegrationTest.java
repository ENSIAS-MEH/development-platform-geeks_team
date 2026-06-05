package com.techhub.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techhub.community.dto.CommentRequest;
import com.techhub.community.dto.GroupRequest;
import com.techhub.community.dto.PostRequest;
import com.techhub.community.enums.PostType;
import com.techhub.community.enums.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PostController.
 * Tests the full post + comment flow end-to-end using H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_HEADER = "X-User-Id";

    private String groupId;
    private UUID ownerId;

    @BeforeEach
    void setUp() throws Exception {
        ownerId = UUID.randomUUID();

        // Create a group for posts
        GroupRequest groupReq = GroupRequest.builder()
                .name("Post Test Group " + UUID.randomUUID().toString().substring(0, 8))
                .topic(Topic.WEB)
                .build();

        String body = mockMvc.perform(post("/api/groups")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        groupId = objectMapper.readTree(body).get("id").asText();
    }

    // ─── Create Post ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/groups/{groupId}/posts → 201")
    void createPost() throws Exception {
        PostRequest request = PostRequest.builder()
                .title("Hello World Post")
                .content("This is a test post")
                .type(PostType.DISCUSSION)
                .build();

        mockMvc.perform(post("/api/groups/" + groupId + "/posts")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Hello World Post"))
                .andExpect(jsonPath("$.type").value("DISCUSSION"))
                .andExpect(jsonPath("$.upvotes").value(0))
                .andExpect(jsonPath("$.commentCount").value(0));
    }

    // ─── Post not allowed for non-member ────────────────────────────────

    @Test
    @DisplayName("POST by non-member → 403")
    void createPostByNonMember() throws Exception {
        UUID outsider = UUID.randomUUID();

        PostRequest request = PostRequest.builder()
                .title("Intruder")
                .content("Should fail")
                .type(PostType.DISCUSSION)
                .build();

        mockMvc.perform(post("/api/groups/" + groupId + "/posts")
                .header(USER_HEADER, outsider.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ─── GET posts sorted by popularity ─────────────────────────────────

    @Test
    @DisplayName("GET /api/groups/{id}/posts?sortByPopularity=true → 200 sorted by upvotes")
    void getPostsSortedByPopularity() throws Exception {
        // Create multiple posts
        for (int i = 0; i < 3; i++) {
            PostRequest req = PostRequest.builder()
                    .title("Post " + i)
                    .content("Content " + i)
                    .type(PostType.DISCUSSION)
                    .build();

            mockMvc.perform(post("/api/groups/" + groupId + "/posts")
                    .header(USER_HEADER, ownerId.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        // Get posts sorted by popularity
        mockMvc.perform(get("/api/groups/" + groupId + "/posts")
                .param("sortByPopularity", "true")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)));
    }

    // ─── Upvote post ────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/groups/{gid}/posts/{pid}/upvote → 200")
    void upvotePost() throws Exception {
        // Create a post
        PostRequest request = PostRequest.builder()
                .title("Upvote Me")
                .content("Please upvote")
                .type(PostType.DISCUSSION)
                .build();

        String postBody = mockMvc.perform(post("/api/groups/" + groupId + "/posts")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String postId = objectMapper.readTree(postBody).get("id").asText();

        // Upvote
        mockMvc.perform(post("/api/groups/" + groupId + "/posts/" + postId + "/upvote"))
                .andExpect(status().isOk());

        // Verify upvote count increased
        mockMvc.perform(get("/api/groups/" + groupId + "/posts/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.upvotes").value(1));
    }

    // ─── Create comment ─────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/groups/{gid}/posts/{pid}/comments → 201")
    void createComment() throws Exception {
        // Create a post first
        PostRequest postReq = PostRequest.builder()
                .title("Comment Test Post")
                .content("Testing comments")
                .type(PostType.DISCUSSION)
                .build();

        String postBody = mockMvc.perform(post("/api/groups/" + groupId + "/posts")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String postId = objectMapper.readTree(postBody).get("id").asText();

        // Create comment
        CommentRequest commentReq = CommentRequest.builder()
                .content("Great post!")
                .build();

        mockMvc.perform(post("/api/groups/" + groupId + "/posts/" + postId + "/comments")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Great post!"));
    }

    // ─── Full post + comment + upvote flow ──────────────────────────────

    @Test
    @DisplayName("Full lifecycle: create post → upvote → comment → reply → list comments")
    void fullPostCommentLifecycle() throws Exception {
        // 1. Create post
        PostRequest postReq = PostRequest.builder()
                .title("Lifecycle Test")
                .content("Testing full flow")
                .type(PostType.RESOURCE)
                .build();

        String postBody = mockMvc.perform(post("/api/groups/" + groupId + "/posts")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String postId = objectMapper.readTree(postBody).get("id").asText();

        // 2. Upvote
        mockMvc.perform(post("/api/groups/" + groupId + "/posts/" + postId + "/upvote"))
                .andExpect(status().isOk());

        // 3. Create top-level comment
        CommentRequest commentReq = CommentRequest.builder()
                .content("Great resource!")
                .build();

        String commentBody = mockMvc.perform(post("/api/groups/" + groupId + "/posts/" + postId + "/comments")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Great resource!"))
                .andReturn().getResponse().getContentAsString();

        String commentId = objectMapper.readTree(commentBody).get("id").asText();

        // 4. Reply to comment
        CommentRequest replyReq = CommentRequest.builder()
                .content("Thanks!")
                .parentCommentId(UUID.fromString(commentId))
                .build();

        mockMvc.perform(post("/api/groups/" + groupId + "/posts/" + postId + "/comments")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(replyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.parentCommentId").value(commentId));

        // 5. List comments (should have 1 top-level with 1 reply)
        mockMvc.perform(get("/api/groups/" + groupId + "/posts/" + postId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].replies", hasSize(1)));
    }

    // ─── Pin/unpin ──────────────────────────────────────────────────────

    @Test
    @DisplayName("PIN toggle by owner")
    void pinPost() throws Exception {
        PostRequest postReq = PostRequest.builder()
                .title("Pin Me")
                .content("Important")
                .type(PostType.ANNOUNCEMENT)
                .build();

        String postBody = mockMvc.perform(post("/api/groups/" + groupId + "/posts")
                .header(USER_HEADER, ownerId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String postId = objectMapper.readTree(postBody).get("id").asText();

        // Pin
        mockMvc.perform(put("/api/groups/" + groupId + "/posts/" + postId + "/pin")
                .header(USER_HEADER, ownerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPinned").value(true));

        // Unpin
        mockMvc.perform(put("/api/groups/" + groupId + "/posts/" + postId + "/pin")
                .header(USER_HEADER, ownerId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isPinned").value(false));
    }
}
