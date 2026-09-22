package com.techhub.community.repository;

import com.techhub.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    /** All posts in a group (paginated) */
    Page<Post> findByGroupId(UUID groupId, Pageable pageable);

    /** Posts in a group ordered by upvotes descending (popularity) */
    Page<Post> findByGroupIdOrderByUpvotesDesc(UUID groupId, Pageable pageable);

    /** Pinned posts in a group */
    List<Post> findByGroupIdAndIsPinnedTrue(UUID groupId);

    /** Popular posts across all groups – used by the cache layer */
    @Query("SELECT p FROM Post p ORDER BY p.upvotes DESC")
    Page<Post> findPopularPosts(Pageable pageable);

    /** Posts by a specific author */
    Page<Post> findByAuthorId(UUID authorId, Pageable pageable);

    // ─── Atomic counter updates ─────────────────────────────────────────

    @Modifying
    @Query("UPDATE Post p SET p.upvotes = p.upvotes + 1 WHERE p.id = :postId")
    void incrementUpvotes(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = p.commentCount + 1 WHERE p.id = :postId")
    void incrementCommentCount(@Param("postId") UUID postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentCount = CASE WHEN p.commentCount > 0 THEN p.commentCount - 1 ELSE 0 END WHERE p.id = :postId")
    void decrementCommentCount(@Param("postId") UUID postId);
}
