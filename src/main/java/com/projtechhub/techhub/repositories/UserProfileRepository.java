package com.projtechhub.techhub.repositories;

import com.projtechhub.techhub.entities.User;
import com.projtechhub.techhub.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * @author pc
 **/
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
     User findByUserId(UUID id);
}
