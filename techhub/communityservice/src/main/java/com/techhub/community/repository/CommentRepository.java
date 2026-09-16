package com.techhub.community.repository;

import com.techhub.community.entity.Comment;
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
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /** Top-level comments for a post (no parent) */
    Page<Comment> findByPostIdAndParentCommentIdIsNull(UUID postId, Pageable pageable);

    /** Replies to a specific comment */
    List<Comment> findByParentCommentId(UUID parentCommentId);

    /** All comments on a post */
    Page<Comment> findByPostId(UUID postId, Pageable pageable);

    /** Count comments on a post */
    long countByPostId(UUID postId);

    // ─── Atomic counter update ──────────────────────────────────────────

    @Modifying
    @Query("UPDATE Comment c SET c.upvotes = c.upvotes + 1 WHERE c.id = :commentId")
    void incrementUpvotes(@Param("commentId") UUID commentId);
}
