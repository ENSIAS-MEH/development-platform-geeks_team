package com.devconnect.projectservice.service;

import com.devconnect.projectservice.dto.*;
import com.devconnect.projectservice.entity.*;
import com.devconnect.projectservice.enums.*;
import com.devconnect.projectservice.exception.*;
import com.devconnect.projectservice.kafka.ProjectProducer;
import com.devconnect.projectservice.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock private ProjectRepository projectRepository;
    @Mock private ProjectMemberRepository memberRepository;
    @Mock private ProjectCommentRepository commentRepository;
    @Mock private ProjectProducer projectProducer;
    @Mock private MatchingService matchingService;

    @InjectMocks private ProjectService projectService;

    private final UUID projectId = UUID.randomUUID();
    private final UUID ownerId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void joinProject_whenAlreadyMember_throwsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject()));
        when(memberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);

        assertThatThrownBy(() -> projectService.joinProject(projectId, userId))
            .isInstanceOf(AlreadyMemberException.class);
    }

    @Test
    void joinProject_whenValid_addsMemberAndPublishesKafka() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject()));
        when(memberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);
        ProjectMember member = ProjectMember.builder()
            .id(UUID.randomUUID()).projectId(projectId).userId(userId).role(MemberRole.MEMBER).build();
        when(memberRepository.save(any(ProjectMember.class))).thenReturn(member);

        var response = projectService.joinProject(projectId, userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        verify(projectProducer).publishProjectJoined(any());
    }

    @Test
    void findMatchingProjects_returnsOrderedByMatchCount() {
        Project p1 = sampleProject();
        Project p2 = Project.builder().id(UUID.randomUUID()).title("Other").type(ProjectType.OPEN_SOURCE)
            .status(ProjectStatus.OPEN).ownerId(ownerId).skillsNeeded(Set.of("go")).build();

        when(projectRepository.findMatchingProjectsWithScore(anyList(), eq(PageRequest.of(0, 10))))
            .thenReturn(List.of(new Object[]{p1, 2L}, new Object[]{p2, 1L}));

        MatchingService service = new MatchingService(projectRepository, null);
        var results = service.findMatchingProjects(List.of("java", "spring"));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getMatchScore()).isEqualTo(2L);
        assertThat(results.get(1).getMatchScore()).isEqualTo(1L);
    }

    @Test
    void addComment_whenNotMember_throwsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject()));
        when(memberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        CommentRequest req = new CommentRequest();
        req.setContent("Hello");

        assertThatThrownBy(() -> projectService.addComment(projectId, userId, req))
            .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void leaveProject_whenNotMember_throwsException() {
        when(memberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.leaveProject(projectId, userId))
            .isInstanceOf(NotMemberException.class);
    }

    @Test
    void updateProject_whenNotOwner_throwsException() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(sampleProject()));

        assertThatThrownBy(() -> projectService.updateProject(projectId, new UpdateProjectRequest(), userId))
            .isInstanceOf(UnauthorizedActionException.class);
    }

    @Test
    void createProject_addsOwnerAsMember() {
        CreateProjectRequest req = new CreateProjectRequest();
        req.setTitle("New Project");
        req.setType(ProjectType.OPEN_SOURCE);

        Project saved = Project.builder()
            .id(projectId).title("New Project").type(ProjectType.OPEN_SOURCE)
            .status(ProjectStatus.OPEN).ownerId(ownerId).build();

        when(projectRepository.save(any(Project.class))).thenReturn(saved);
        when(memberRepository.save(any(ProjectMember.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = projectService.createProject(req, ownerId);

        assertThat(response.getOwnerId()).isEqualTo(ownerId);
        assertThat(response.isUserIsMember()).isTrue();
        verify(memberRepository).save(argThat(m ->
            m.getRole() == MemberRole.OWNER && m.getUserId().equals(ownerId)));
        verify(projectProducer).publishProjectCreated(any());
    }

    @Test
    void getMatchingProjectsForUser_callsMatchingService() {
        List<MatchingProjectResponse> expected = List.of(
            MatchingProjectResponse.builder().matchScore(2).build()
        );
        when(matchingService.findMatchingProjectsForUser(userId)).thenReturn(expected);

        var results = projectService.getMatchingProjectsForUser(userId);

        assertThat(results).isEqualTo(expected);
        verify(matchingService).findMatchingProjectsForUser(userId);
    }

    private Project sampleProject() {
        return Project.builder()
            .id(projectId)
            .title("Sample")
            .type(ProjectType.STARTUP_IDEA)
            .status(ProjectStatus.OPEN)
            .ownerId(ownerId)
            .skillsNeeded(new HashSet<>(Set.of("java", "spring")))
            .build();
    }
}
