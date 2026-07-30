package com.promptlab.server.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Added this import
import org.springframework.stereotype.Repository;

import com.promptlab.server.dto.AdminUserResponse;
import com.promptlab.server.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // <-- Added the @Query annotation and the JPQL query
    @Query("SELECT new com.promptlab.server.dto.AdminUserResponse(" +
           "u.id, u.username, u.email, u.attachmentUrl, COUNT(p)) " +
           "FROM User u LEFT JOIN u.posts p " +
           "GROUP BY u.id, u.username, u.email, u.attachmentUrl")
    List<AdminUserResponse> fetchAdminUserSummaries();
}