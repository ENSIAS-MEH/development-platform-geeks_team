package com.techhub.community.controller;

import com.techhub.community.dto.CommentRequest;
import com.techhub.community.dto.CommentResponse;
import com.techhub.community.dto.PostRequest;
import com.techhub.community.dto.PostResponse;
import com.techhub.community.service.CommentService;
import com.techhub.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups/{groupId}/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Post and comment management within groups")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    // ═══════════════════════════════════════════════════════════════════
    // POSTS
    // ═══════════════════════════════════════════════════════════════════

    @PostMapping
    @Operation(summary = "Create a new post in a group")
    public ResponseEntity<PostResponse> createPost(
            @PathVariable UUID groupId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody PostRequest request) {
        PostResponse post = postService.createPost(groupId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(post);
    }

    @GetMapping
    @Operation(summary = "List posts in a group (default: newest first)")
    public ResponseEntity<Page<PostResponse>> getPosts(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "false") boolean sortByPopularity,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PostResponse> posts;
        if (sortByPopularity) {
            posts = postService.getPostsByGroupSortedByPopularity(groupId, pageable);
        } else {
            posts = postService.getPostsByGroup(groupId, pageable);
        }
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get a single post by ID")
    public ResponseEntity<PostResponse> getPost(@PathVariable UUID groupId, @PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Update a post (author only)")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.updatePost(postId, userId, request));
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete a post (author or moderator/owner)")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") UUID userId) {
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/upvote")
    @Operation(summary = "Upvote a post (atomic increment)")
    public ResponseEntity<Void> upvotePost(@PathVariable UUID groupId, @PathVariable UUID postId) {
        postService.upvotePost(postId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}/pin")
    @Operation(summary = "Pin or unpin a post (moderator/owner only)")
    public ResponseEntity<PostResponse> togglePin(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.ok(postService.togglePin(postId, userId));
    }

    // ═══════════════════════════════════════════════════════════════════
    // COMMENTS
    // ═══════════════════════════════════════════════════════════════════

    @PostMapping("/{postId}/comments")
    @Operation(summary = "Add a comment to a post (supports nested replies, max depth 2)")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse comment = commentService.createComment(postId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "List comments for a post (with nested replies)")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, pageable));
    }

    @PostMapping("/{postId}/comments/{commentId}/upvote")
    @Operation(summary = "Upvote a comment (atomic increment)")
    public ResponseEntity<Void> upvoteComment(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId) {
        commentService.upvoteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "Delete a comment (author only)")
    public ResponseEntity<Void> deleteComment(
            @PathVariable UUID groupId,
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @RequestHeader("X-User-Id") UUID userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
