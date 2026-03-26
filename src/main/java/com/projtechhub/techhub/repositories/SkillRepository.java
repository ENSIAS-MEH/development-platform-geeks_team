package com.projtechhub.techhub.repositories;

import com.projtechhub.techhub.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author pc
 **/
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    public List<Skill> findByUser_Id(UUID userId);
    // Used to check for duplicates before adding a new skill
    public Optional<List<Skill>> findByUser_IdAndNameIgnoreCase(UUID userId, String name);

    // Used to verify ownership before delete/update
    Optional<Skill> findByIdAndUser_Id(UUID id, UUID userId);}
