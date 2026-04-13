package com.techhub.community.service;

import com.techhub.community.dto.PostRequest;
import com.techhub.community.dto.PostResponse;
import com.techhub.community.entity.Group;
import com.techhub.community.entity.Post;
import com.techhub.community.enums.PostType;
import com.techhub.community.enums.Topic;
import com.techhub.community.exception.ResourceNotFoundException;
import com.techhub.community.exception.UnauthorizedException;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private GroupMemberRepository memberRepository;
    @Mock
    private GroupService groupService;

    @InjectMocks
    private PostService postService;

    private UUID groupId;
    private UUID authorId;
    private UUID postId;
    private Post samplePost;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        authorId = UUID.randomUUID();
        postId = UUID.randomUUID();

        samplePost = Post.builder()
                .id(postId)
                .groupId(groupId)
                .authorId(authorId)
                .title("Sample Post")
                .content("Sample content")
                .type(PostType.DISCUSSION)
                .upvotes(0)
                .commentCount(0)
                .isPinned(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("createPost")
    class CreatePost {

        @Test
        @DisplayName("should create post when user is group member")
        void shouldCreatePost() {
            PostRequest request = PostRequest.builder()
                    .title("New Post")
                    .content("Content")
                    .type(PostType.DISCUSSION)
                    .build();

            Group group = Group.builder().id(groupId).build();
            when(groupService.findGroupOrThrow(groupId)).thenReturn(group);
            when(memberRepository.existsByGroupIdAndUserId(groupId, authorId)).thenReturn(true);
            when(postRepository.save(any(Post.class))).thenReturn(samplePost);

            PostResponse response = postService.createPost(groupId, authorId, request);

            assertThat(response).isNotNull();
            assertThat(response.getTitle()).isEqualTo("Sample Post");
            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("should throw UnauthorizedException when user is not a member")
        void shouldThrowWhenNotMember() {
            PostRequest request = PostRequest.builder()
                    .title("Post")
                    .content("Content")
                    .type(PostType.DISCUSSION)
                    .build();

            Group group = Group.builder().id(groupId).build();
            when(groupService.findGroupOrThrow(groupId)).thenReturn(group);
            when(memberRepository.existsByGroupIdAndUserId(groupId, authorId)).thenReturn(false);

            assertThatThrownBy(() -> postService.createPost(groupId, authorId, request))
                    .isInstanceOf(UnauthorizedException.class);
        }
    }

    @Nested
    @DisplayName("getPostById")
    class GetPostById {

        @Test
        @DisplayName("should return post when found")
        void shouldReturnPost() {
            when(postRepository.findById(postId)).thenReturn(Optional.of(samplePost));

            PostResponse response = postService.getPostById(postId);

            assertThat(response.getId()).isEqualTo(postId);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when not found")
        void shouldThrowWhenNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(postRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.getPostById(unknownId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("upvotePost")
    class UpvotePost {

        @Test
        @DisplayName("should call atomic increment")
        void shouldUpvote() {
            when(postRepository.findById(postId)).thenReturn(Optional.of(samplePost));

            postService.upvotePost(postId);

            verify(postRepository).incrementUpvotes(postId);
        }
    }

    @Nested
    @DisplayName("updatePost")
    class UpdatePost {

        @Test
        @DisplayName("should update when requester is author")
        void shouldUpdate() {
            PostRequest request = PostRequest.builder()
                    .title("Updated")
                    .content("Updated content")
                    .type(PostType.RESOURCE)
                    .build();

            when(postRepository.findById(postId)).thenReturn(Optional.of(samplePost));
            when(postRepository.save(any(Post.class))).thenReturn(samplePost);

            PostResponse response = postService.updatePost(postId, authorId, request);

            assertThat(response).isNotNull();
            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("should throw when non-author tries to update")
        void shouldThrowWhenNonAuthor() {
            UUID anotherUser = UUID.randomUUID();
            PostRequest request = PostRequest.builder()
                    .title("Hack")
                    .content("Hacked content")
                    .type(PostType.DISCUSSION)
                    .build();

            when(postRepository.findById(postId)).thenReturn(Optional.of(samplePost));

            assertThatThrownBy(() -> postService.updatePost(postId, anotherUser, request))
                    .isInstanceOf(UnauthorizedException.class);
        }
    }
}
