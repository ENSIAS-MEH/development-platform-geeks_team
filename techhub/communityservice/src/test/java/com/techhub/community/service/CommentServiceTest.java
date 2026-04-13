package com.techhub.community.service;

import com.techhub.community.dto.CommentRequest;
import com.techhub.community.dto.CommentResponse;
import com.techhub.community.entity.Comment;
import com.techhub.community.entity.Post;
import com.techhub.community.enums.PostType;
import com.techhub.community.exception.ResourceNotFoundException;
import com.techhub.community.repository.CommentRepository;
import com.techhub.community.repository.GroupMemberRepository;
import com.techhub.community.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostService postService;
    @Mock
    private GroupMemberRepository memberRepository;

    @InjectMocks
    private CommentService commentService;

    private UUID postId;
    private UUID authorId;
    private UUID groupId;
    private UUID commentId;
    private Post samplePost;
    private Comment sampleComment;

    @BeforeEach
    void setUp() {
        postId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        groupId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        samplePost = Post.builder()
                .id(postId)
                .groupId(groupId)
                .authorId(authorId)
                .title("Post")
                .content("Content")
                .type(PostType.DISCUSSION)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        sampleComment = Comment.builder()
                .id(commentId)
                .postId(postId)
                .authorId(authorId)
                .content("Test comment")
                .upvotes(0)
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("createComment")
    class CreateComment {

        @Test
        @DisplayName("should create top-level comment")
        void shouldCreateTopLevelComment() {
            CommentRequest request = CommentRequest.builder()
                    .content("Great post!")
                    .build();

            when(postService.findPostOrThrow(postId)).thenReturn(samplePost);
            when(memberRepository.existsByGroupIdAndUserId(groupId, authorId)).thenReturn(true);
            when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);

            CommentResponse response = commentService.createComment(postId, authorId, request);

            assertThat(response).isNotNull();
            assertThat(response.getContent()).isEqualTo("Test comment");
            verify(postRepository).incrementCommentCount(postId);
        }

        @Test
        @DisplayName("should create reply to top-level comment")
        void shouldCreateReply() {
            UUID parentId = UUID.randomUUID();
            Comment parentComment = Comment.builder()
                    .id(parentId)
                    .postId(postId)
                    .authorId(UUID.randomUUID())
                    .content("Parent")
                    .parentCommentId(null) // top-level
                    .build();

            CommentRequest request = CommentRequest.builder()
                    .content("Reply!")
                    .parentCommentId(parentId)
                    .build();

            when(postService.findPostOrThrow(postId)).thenReturn(samplePost);
            when(memberRepository.existsByGroupIdAndUserId(groupId, authorId)).thenReturn(true);
            when(commentRepository.findById(parentId)).thenReturn(Optional.of(parentComment));
            when(commentRepository.save(any(Comment.class))).thenReturn(sampleComment);

            CommentResponse response = commentService.createComment(postId, authorId, request);

            assertThat(response).isNotNull();
            verify(postRepository).incrementCommentCount(postId);
        }

        @Test
        @DisplayName("should reject nesting beyond depth 2")
        void shouldRejectDeepNesting() {
            UUID parentId = UUID.randomUUID();
            Comment nestedParent = Comment.builder()
                    .id(parentId)
                    .postId(postId)
                    .authorId(UUID.randomUUID())
                    .content("Depth-2 comment")
                    .parentCommentId(UUID.randomUUID()) // already a reply → depth 2
                    .build();

            CommentRequest request = CommentRequest.builder()
                    .content("Too deep!")
                    .parentCommentId(parentId)
                    .build();

            when(postService.findPostOrThrow(postId)).thenReturn(samplePost);
            when(memberRepository.existsByGroupIdAndUserId(groupId, authorId)).thenReturn(true);
            when(commentRepository.findById(parentId)).thenReturn(Optional.of(nestedParent));

            assertThatThrownBy(() -> commentService.createComment(postId, authorId, request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Maximum comment nesting depth");
        }
    }

    @Nested
    @DisplayName("upvoteComment")
    class UpvoteComment {

        @Test
        @DisplayName("should atomically increment upvotes")
        void shouldUpvote() {
            when(commentRepository.existsById(commentId)).thenReturn(true);

            commentService.upvoteComment(commentId);

            verify(commentRepository).incrementUpvotes(commentId);
        }

        @Test
        @DisplayName("should throw when comment not found")
        void shouldThrowWhenNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(commentRepository.existsById(unknownId)).thenReturn(false);

            assertThatThrownBy(() -> commentService.upvoteComment(unknownId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteComment")
    class DeleteComment {

        @Test
        @DisplayName("should delete when requester is author")
        void shouldDeleteWhenAuthor() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(sampleComment));

            commentService.deleteComment(commentId, authorId);

            verify(commentRepository).delete(sampleComment);
            verify(postRepository).decrementCommentCount(postId);
        }
    }
}
