package com.projtechhub.techhub.repositories;

import com.projtechhub.techhub.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;

/**
 * @author pc
 **/
public interface UserRepository extends JpaRepository<User, Long> {

     Optional<User> findByEmail(String email);
     Boolean existsByEmail(String email);
     Optional<User> findByEnabledTrue(Pageable pageable);

     boolean getUserByEmailExists(String email);
}
