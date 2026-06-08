package com.devconnect.projectservice.service;

import com.devconnect.projectservice.dto.*;
import com.devconnect.projectservice.entity.*;
import com.devconnect.projectservice.enums.*;
import com.devconnect.projectservice.exception.*;
import com.devconnect.projectservice.kafka.ProjectProducer;
import com.devconnect.projectservice.kafka.event.*;
import com.devconnect.projectservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Business logic for project management.
 * Handles CRUD, team membership, commenting, and matching delegation.
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "projects")
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final ProjectCommentRepository commentRepository;
    private final ProjectProducer projectProducer;
    private final MatchingService matchingService;

    /**
     * Creates a new project and auto-adds the creator as OWNER member.
     *
     * @param request the project creation data
     * @param ownerId the authenticated user's UUID
     * @return the created project response
     */
    @Transactional
    @CacheEvict(cacheNames = "project-list", allEntries = true)
    public ProjectResponse createProject(CreateProjectRequest request, UUID ownerId) {
        Project project = Project.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .type(request.getType())
            .technologies(request.getTechnologies() != null ? request.getTechnologies() : new HashSet<>())
            .skillsNeeded(request.getSkillsNeeded() != null ? request.getSkillsNeeded() : new HashSet<>())
            .githubUrl(request.getGithubUrl())
            .ownerId(ownerId)
            .status(ProjectStatus.OPEN)
            .build();

        Project saved = projectRepository.save(project);

        memberRepository.save(ProjectMember.builder()
            .projectId(saved.getId())
            .userId(ownerId)
            .role(MemberRole.OWNER)
            .build());

        projectProducer.publishProjectCreated(ProjectCreatedEvent.builder()
            .id(saved.getId()).title(saved.getTitle()).ownerId(ownerId).build());

        return toResponse(saved, 1, true);
    }

    /**
     * Returns a single project by ID with Redis caching.
     *
     * @param projectId the project UUID
     * @param currentUserId the authenticated user UUID (may be null)
     * @return the project response
     * @throws ProjectNotFoundException if not found
     */
    @Cacheable(key = "#projectId + '_' + (#currentUserId != null ? #currentUserId.toString() : 'guest')")
    public ProjectResponse getProjectById(UUID projectId, UUID currentUserId) {
        Project project = findOrThrow(projectId);
        long count = memberRepository.countByProjectId(projectId);
        boolean isMember = currentUserId != null && memberRepository.existsByProjectIdAndUserId(projectId, currentUserId);
        return toResponse(project, count, isMember);
    }

    /**
     * Returns a paginated list of projects with optional filters.
     *
     * @param type project type filter (nullable)
     * @param status project status filter (nullable)
     * @param keyword search keyword (nullable)
     * @param pageable pagination parameters
     * @return paginated project responses
     */
    @Cacheable(cacheNames = "project-list", key = "#type + '_' + #status + '_' + #keyword + '_' + #pageable.pageNumber")
    public Page<ProjectResponse> searchProjects(ProjectType type, ProjectStatus status, String keyword, Pageable pageable) {
        return projectRepository.searchProjects(type, status, keyword, pageable)
            .map(p -> toResponse(p, memberRepository.countByProjectId(p.getId()), false));
    }

    /**
     * Updates an existing project. Only the owner can update.
     *
     * @param projectId the project UUID
     * @param request the partial update data
     * @param requesterId the authenticated user UUID
     * @return the updated project response
     * @throws ProjectNotFoundException if not found
     * @throws UnauthorizedActionException if requester is not the owner
     */
    @Transactional
    @CacheEvict(cacheNames = {"projects", "project-list"}, allEntries = true)
    public ProjectResponse updateProject(UUID projectId, UpdateProjectRequest request, UUID requesterId) {
        Project project = findOrThrow(projectId);
        if (!project.getOwnerId().equals(requesterId)) {
            throw new UnauthorizedActionException("Only the owner can update this project");
        }
        if (request.getTitle() != null) project.setTitle(request.getTitle());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getType() != null) project.setType(request.getType());
        if (request.getTechnologies() != null) project.setTechnologies(request.getTechnologies());
        if (request.getSkillsNeeded() != null) project.setSkillsNeeded(request.getSkillsNeeded());
        if (request.getStatus() != null) project.setStatus(request.getStatus());
        if (request.getGithubUrl() != null) project.setGithubUrl(request.getGithubUrl());

        Project saved = projectRepository.save(project);
        return toResponse(saved, memberRepository.countByProjectId(projectId), true);
    }

    /**
     * Adds the current user as a MEMBER of the project.
     *
     * @param projectId the project UUID
     * @param userId the authenticated user UUID
     * @return the member response
     * @throws ProjectNotFoundException if not found
     * @throws AlreadyMemberException if user is already a member
     */
    @Transactional
    @CacheEvict(cacheNames = {"projects", "project-list"}, allEntries = true)
    public ProjectMemberResponse joinProject(UUID projectId, UUID userId) {
        findOrThrow(projectId);
        if (memberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new AlreadyMemberException(projectId, userId);
        }
        ProjectMember member = memberRepository.save(ProjectMember.builder()
            .projectId(projectId).userId(userId).role(MemberRole.MEMBER).build());

        projectProducer.publishProjectJoined(ProjectJoinedEvent.builder()
            .projectId(projectId).userId(userId).build());

        return toMemberResponse(member);
    }

    /**
     * Removes the current user from the project.
     *
     * @param projectId the project UUID
     * @param userId the authenticated user UUID
     * @throws NotMemberException if user is not a member
     */
    @Transactional
    @CacheEvict(cacheNames = {"projects", "project-list"}, allEntries = true)
    public void leaveProject(UUID projectId, UUID userId) {
        ProjectMember member = memberRepository.findByProjectIdAndUserId(projectId, userId)
            .orElseThrow(() -> new NotMemberException(projectId, userId));
        memberRepository.delete(member);
    }

    /**
     * Returns all members of a project.
     *
     * @param projectId the project UUID
     * @return list of member responses
     * @throws ProjectNotFoundException if not found
     */
    public List<ProjectMemberResponse> getMembers(UUID projectId) {
        findOrThrow(projectId);
        return memberRepository.findByProjectId(projectId).stream()
            .map(this::toMemberResponse).toList();
    }

    /**
     * Adds a comment to a project. Only members can comment.
     *
     * @param projectId the project UUID
     * @param userId the authenticated user UUID
     * @param request the comment content
     * @return the comment response
     * @throws ProjectNotFoundException if not found
     * @throws UnauthorizedActionException if user is not a member
     */
    public CommentResponse addComment(UUID projectId, UUID userId, CommentRequest request) {
        findOrThrow(projectId);
        if (!memberRepository.existsByProjectIdAndUserId(projectId, userId)) {
            throw new UnauthorizedActionException("Only project members can comment");
        }
        ProjectComment saved = commentRepository.save(ProjectComment.builder()
            .projectId(projectId).userId(userId).content(request.getContent()).build());
        return toCommentResponse(saved);
    }

    /**
     * Returns paginated comments for a project.
     *
     * @param projectId the project UUID
     * @param pageable pagination parameters
     * @return paginated comment responses
     * @throws ProjectNotFoundException if not found
     */
    public Page<CommentResponse> getComments(UUID projectId, Pageable pageable) {
        findOrThrow(projectId);
        return commentRepository.findByProjectId(projectId, pageable).map(this::toCommentResponse);
    }

    /**
     * Returns projects matching the user's skill profile (cached).
     *
     * @param userId the authenticated user's UUID
     * @return list of matching projects with scores
     */
    @Cacheable(cacheNames = "matching-projects", key = "#userId")
    public List<MatchingProjectResponse> getMatchingProjectsForUser(UUID userId) {
        return matchingService.findMatchingProjectsForUser(userId);
    }

    /**
     * Returns user IDs whose skills match the project's needs.
     *
     * @param projectId the project UUID
     * @return list of matching user UUIDs
     */
    public List<UUID> getMatchingUsersForProject(UUID projectId) {
        return matchingService.findMatchingUsersForProject(projectId);
    }

    private Project findOrThrow(UUID id) {
        return projectRepository.findById(id)
            .orElseThrow(() -> new ProjectNotFoundException(id));
    }

    private ProjectResponse toResponse(Project p, long memberCount, boolean isMember) {
        return ProjectResponse.builder()
            .id(p.getId()).title(p.getTitle()).description(p.getDescription())
            .type(p.getType())
            .technologies(p.getTechnologies() != null ? new HashSet<>(p.getTechnologies()) : new HashSet<>())
            .skillsNeeded(p.getSkillsNeeded() != null ? new HashSet<>(p.getSkillsNeeded()) : new HashSet<>())
            .status(p.getStatus()).githubUrl(p.getGithubUrl()).ownerId(p.getOwnerId())
            .memberCount(memberCount).userIsMember(isMember)
            .createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt())
            .build();
    }

    private ProjectMemberResponse toMemberResponse(ProjectMember m) {
        return ProjectMemberResponse.builder().id(m.getId()).projectId(m.getProjectId())
            .userId(m.getUserId()).role(m.getRole()).joinedAt(m.getJoinedAt()).build();
    }

    private CommentResponse toCommentResponse(ProjectComment c) {
        return CommentResponse.builder().id(c.getId()).projectId(c.getProjectId())
            .userId(c.getUserId()).content(c.getContent()).createdAt(c.getCreatedAt()).build();
    }
}
