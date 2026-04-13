package com.techhub.community.service;

import com.techhub.community.dto.CommentRequest;
import com.techhub.community.dto.CommentResponse;
import com.techhub.community.entity.Comment;
import com.techhub.community.exception.ResourceNotFoundException;
import com.techhub.community.exception.UnauthorizedException;
import com.techhub.community.repository.CommentRepository;
import com.techhub.community.repository.GroupMemberRepository;
import com.techhub.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final PostService postService;
    private final GroupMemberRepository memberRepository;

    private static final int MAX_NESTING_DEPTH = 2;

    // ─── Create ─────────────────────────────────────────────────────────

    @Transactional
    public CommentResponse createComment(UUID postId, UUID authorId, CommentRequest request) {
        var post = postService.findPostOrThrow(postId);

        // Verify author is a member of the group
        if (!memberRepository.existsByGroupIdAndUserId(post.getGroupId(), authorId)) {
            throw new UnauthorizedException("You must be a member of this group to comment");
        }

        // Enforce max nesting depth
        if (request.getParentCommentId() != null) {
            Comment parent = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", request.getParentCommentId()));

            // If parent already has a parent, we are at depth 2 → reject further nesting
            if (parent.getParentCommentId() != null) {
                throw new IllegalArgumentException(
                        "Maximum comment nesting depth of " + MAX_NESTING_DEPTH
                                + " reached. Reply to the parent comment instead.");
            }
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .content(request.getContent())
                .parentCommentId(request.getParentCommentId())
                .build();

        comment = commentRepository.save(comment);

        // Atomic increment of comment_count on the post
        postRepository.incrementCommentCount(postId);

        return toResponse(comment);
    }

    // ─── Read ───────────────────────────────────────────────────────────

    /**
     * Retrieves top-level comments for a post with nested replies.
     */
    public Page<CommentResponse> getCommentsByPost(UUID postId, Pageable pageable) {
        postService.findPostOrThrow(postId);

        Page<Comment> topLevel = commentRepository.findByPostIdAndParentCommentIdIsNull(postId, pageable);

        return topLevel.map(comment -> {
            CommentResponse response = toResponse(comment);
            // Fetch replies (depth 1 → depth 2)
            List<CommentResponse> replies = commentRepository.findByParentCommentId(comment.getId())
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
            response.setReplies(replies);
            return response;
        });
    }

    // ─── Upvote ─────────────────────────────────────────────────────────

    @Transactional
    public void upvoteComment(UUID commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new ResourceNotFoundException("Comment", "id", commentId);
        }
        commentRepository.incrementUpvotes(commentId);
    }

    // ─── Delete ─────────────────────────────────────────────────────────

    @Transactional
    public void deleteComment(UUID commentId, UUID userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthorId().equals(userId)) {
            throw new UnauthorizedException("Only the author can delete this comment");
        }

        commentRepository.delete(comment);
        postRepository.decrementCommentCount(comment.getPostId());
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    private CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .upvotes(comment.getUpvotes())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
