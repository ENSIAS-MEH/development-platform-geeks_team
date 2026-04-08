package com.projtechhub.techhub.repositories;

import com.projtechhub.techhub.entities.User;
import com.projtechhub.techhub.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author pc
 **/
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
     User findByUserId(UUID id);
}
