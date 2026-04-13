package com.techhub.community.service;

import com.techhub.community.dto.PostRequest;
import com.techhub.community.dto.PostResponse;
import com.techhub.community.entity.Post;
import com.techhub.community.enums.MemberRole;
import com.techhub.community.exception.ResourceNotFoundException;
import com.techhub.community.exception.UnauthorizedException;
import com.techhub.community.repository.GroupMemberRepository;
import com.techhub.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final GroupMemberRepository memberRepository;
    private final GroupService groupService;

    // ─── Create ─────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "popularPosts", allEntries = true)
    public PostResponse createPost(UUID groupId, UUID authorId, PostRequest request) {
        // Verify group exists
        groupService.findGroupOrThrow(groupId);

        // Verify the author is a member of the group
        if (!memberRepository.existsByGroupIdAndUserId(groupId, authorId)) {
            throw new UnauthorizedException("You must be a member of this group to post");
        }

        Post post = Post.builder()
                .groupId(groupId)
                .authorId(authorId)
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .build();

        return toResponse(postRepository.save(post));
    }

    // ─── Read ───────────────────────────────────────────────────────────

    public PostResponse getPostById(UUID postId) {
        Post post = findPostOrThrow(postId);
        return toResponse(post);
    }

    public Page<PostResponse> getPostsByGroup(UUID groupId, Pageable pageable) {
        return postRepository.findByGroupId(groupId, pageable).map(this::toResponse);
    }

    /** Pagination by popularity (sorted by upvotes desc) */
    public Page<PostResponse> getPostsByGroupSortedByPopularity(UUID groupId, Pageable pageable) {
        return postRepository.findByGroupIdOrderByUpvotesDesc(groupId, pageable).map(this::toResponse);
    }

    /** Popular posts across all groups – cached in Redis */
    @Cacheable(value = "popularPosts", key = "'page_' + #page + '_size_' + #size")
    public Page<PostResponse> getPopularPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository.findPopularPosts(pageable).map(this::toResponse);
    }

    // ─── Update ─────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "popularPosts", allEntries = true)
    public PostResponse updatePost(UUID postId, UUID userId, PostRequest request) {
        Post post = findPostOrThrow(postId);

        if (!post.getAuthorId().equals(userId)) {
            throw new UnauthorizedException("Only the author can edit this post");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setType(request.getType());

        return toResponse(postRepository.save(post));
    }

    // ─── Delete ─────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "popularPosts", allEntries = true)
    public void deletePost(UUID postId, UUID userId) {
        Post post = findPostOrThrow(postId);

        // Author or a group moderator/owner can delete
        boolean isAuthor = post.getAuthorId().equals(userId);
        boolean isModerator = memberRepository
                .findByGroupIdAndUserId(post.getGroupId(), userId)
                .map(m -> m.getRole() == MemberRole.OWNER || m.getRole() == MemberRole.MODERATOR)
                .orElse(false);

        if (!isAuthor && !isModerator) {
            throw new UnauthorizedException("You do not have permission to delete this post");
        }

        postRepository.delete(post);
    }

    // ─── Upvote ─────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(value = "popularPosts", allEntries = true)
    public void upvotePost(UUID postId) {
        findPostOrThrow(postId);
        postRepository.incrementUpvotes(postId);
    }

    // ─── Pin / Unpin ────────────────────────────────────────────────────

    @Transactional
    public PostResponse togglePin(UUID postId, UUID userId) {
        Post post = findPostOrThrow(postId);

        // Only owner or moderator can pin
        var member = memberRepository.findByGroupIdAndUserId(post.getGroupId(), userId)
                .orElseThrow(() -> new UnauthorizedException("Not a member of this group"));

        if (member.getRole() != MemberRole.OWNER && member.getRole() != MemberRole.MODERATOR) {
            throw new UnauthorizedException("Only owner or moderator can pin/unpin posts");
        }

        post.setIsPinned(!post.getIsPinned());
        return toResponse(postRepository.save(post));
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    public Post findPostOrThrow(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
    }

    private PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .groupId(post.getGroupId())
                .authorId(post.getAuthorId())
                .title(post.getTitle())
                .content(post.getContent())
                .type(post.getType())
                .upvotes(post.getUpvotes())
                .commentCount(post.getCommentCount())
                .isPinned(post.getIsPinned())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
