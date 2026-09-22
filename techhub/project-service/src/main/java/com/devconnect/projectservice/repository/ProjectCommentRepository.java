package com.devconnect.projectservice.repository;

import com.devconnect.projectservice.entity.ProjectComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectCommentRepository extends JpaRepository<ProjectComment, UUID> {
    Page<ProjectComment> findByProjectId(UUID projectId, Pageable pageable);
}
