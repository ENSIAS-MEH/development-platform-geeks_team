package com.projtechhub.techhub.repositories;

import com.projtechhub.techhub.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * @author pc
 **/
public interface UserRepository extends JpaRepository<User, Long> {

     Optional<User> findByEmail(String email);
     Boolean existsByEmail(String email);
     Page<User> findByEnabledTrue(Pageable pageable);

     boolean getUserByEmailExists(String email);

     @Query("""
        SELECT DISTINCT u FROM User u
        JOIN u.skills s
        WHERE LOWER(s.name) = LOWER(:skill)
        AND u.enabled = true
        """)
     Page<User> searchBySkill(
             @Param("skill") String skill,
             Pageable pageable
     );

     Optional<User> findById(UUID id);
}
