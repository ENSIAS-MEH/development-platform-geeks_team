package com.devconnect.projectservice.service;

import com.devconnect.projectservice.dto.*;
import com.devconnect.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Matching algorithm service.
 * Matches projects to a user's skill set by counting overlapping skills.
 * Results are ordered by match score descending (most compatible first).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final ProjectRepository projectRepository;
    private final WebClient userServiceClient;

    /**
     * Finds the top 10 open projects that match the given skill set.
     * Score = number of project's skillsNeeded that appear in userSkills.
     *
     * @param userSkills the list of skills the user has
     * @return list of matching projects ordered by score descending
     */
    public List<MatchingProjectResponse> findMatchingProjects(List<String> userSkills) {
        if (userSkills == null || userSkills.isEmpty()) return Collections.emptyList();

        Pageable top10 = PageRequest.of(0, 10);
        List<Object[]> results = projectRepository.findMatchingProjectsWithScore(
            userSkills.stream().map(String::toLowerCase).collect(Collectors.toList()),
            top10
        );

        return results.stream()
            .map(row -> {
                com.devconnect.projectservice.entity.Project project =
                    (com.devconnect.projectservice.entity.Project) row[0];
                long score = ((Number) row[1]).longValue();
                return MatchingProjectResponse.builder()
                    .project(toProjectResponse(project, 0, false))
                    .matchScore(score)
                    .build();
            })
            .collect(Collectors.toList());
    }

    /**
     * Fetches a user's skills from user-service via WebClient, then finds matching projects.
     *
     * @param userId the authenticated user's UUID
     * @return matching projects for that user's profile
     */
    public List<MatchingProjectResponse> findMatchingProjectsForUser(UUID userId) {
        try {
            UserSkillsResponse response = userServiceClient.get()
                .uri("/api/users/{id}/skills", userId)
                .retrieve()
                .bodyToMono(UserSkillsResponse.class)
                .block();

            List<String> skills = response != null ? response.getSkills() : Collections.emptyList();
            return findMatchingProjects(skills);
        } catch (Exception e) {
            log.warn("Could not fetch skills for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Finds users whose skills match the project's skillsNeeded.
     * Calls user-service search endpoint for each required skill.
     *
     * @param projectId the project UUID
     * @return list of matching user IDs (deduped)
     * @throws com.devconnect.projectservice.exception.ProjectNotFoundException if project not found
     */
    public List<UUID> findMatchingUsersForProject(UUID projectId) {
        com.devconnect.projectservice.entity.Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new com.devconnect.projectservice.exception.ProjectNotFoundException(projectId));

        Set<UUID> matchedUserIds = new LinkedHashSet<>();
        for (String skill : project.getSkillsNeeded()) {
            try {
                UserSearchResponse response = userServiceClient.get()
                    .uri("/api/users/search?skill={skill}", skill)
                    .retrieve()
                    .bodyToMono(UserSearchResponse.class)
                    .block();
                if (response != null) matchedUserIds.addAll(response.getUserIds());
            } catch (Exception e) {
                log.warn("Could not search users for skill {}: {}", skill, e.getMessage());
            }
        }
        return new ArrayList<>(matchedUserIds);
    }

    private ProjectResponse toProjectResponse(com.devconnect.projectservice.entity.Project p, long memberCount, boolean isMember) {
        return ProjectResponse.builder()
            .id(p.getId()).title(p.getTitle()).description(p.getDescription())
            .type(p.getType()).technologies(p.getTechnologies()).skillsNeeded(p.getSkillsNeeded())
            .status(p.getStatus()).githubUrl(p.getGithubUrl()).ownerId(p.getOwnerId())
            .memberCount(memberCount).userIsMember(isMember)
            .createdAt(p.getCreatedAt()).updatedAt(p.getUpdatedAt())
            .build();
    }

    @lombok.Data public static class UserSkillsResponse { private List<String> skills; }
    @lombok.Data public static class UserSearchResponse { private List<UUID> userIds; }
}
