package com.promptlab.server.repo;

import com.promptlab.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Required for the CustomUserDetailsService to load a user during login
    Optional<User> findByUsername(String username);

    // Required for the AuthController to check if a username is taken during registration
    boolean existsByUsername(String username);

    // Required for the AuthController to check if an email is taken during registration
    boolean existsByEmail(String email);
    
    
}