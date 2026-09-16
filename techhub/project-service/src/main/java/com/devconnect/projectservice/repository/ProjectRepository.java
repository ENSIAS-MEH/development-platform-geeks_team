package com.devconnect.projectservice.repository;

import com.devconnect.projectservice.entity.Project;
import com.devconnect.projectservice.enums.ProjectStatus;
import com.devconnect.projectservice.enums.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    List<Project> findByOwnerId(UUID ownerId);

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.technologies t WHERE t = :tech")
    Page<Project> findByTechnology(@Param("tech") String tech, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN p.skillsNeeded s WHERE s = :skill")
    Page<Project> findBySkillNeeded(@Param("skill") String skill, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE " +
           "(:type IS NULL OR p.type = :type) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(CAST(:keyword AS string) IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Project> searchProjects(
        @Param("type") ProjectType type,
        @Param("status") ProjectStatus status,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    @Query("SELECT p, COUNT(s) as matchCount FROM Project p JOIN p.skillsNeeded s " +
           "WHERE s IN :skills AND p.status = 'OPEN' " +
           "GROUP BY p ORDER BY matchCount DESC")
    List<Object[]> findMatchingProjectsWithScore(@Param("skills") List<String> skills, Pageable pageable);
}
