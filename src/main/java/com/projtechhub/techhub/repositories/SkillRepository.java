package com.projtechhub.techhub.repositories;

import com.projtechhub.techhub.entities.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author pc
 **/
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    public List<Skill> findByUserId(UUID userId);
    public List<Skill> findByUserIdAndName(UUID userId, String name);

}
