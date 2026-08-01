package com.promptlab.server.repository;

import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    @EntityGraph(attributePaths = {"user"})
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUser(User user);
    
    // Kept your old one just in case you need it elsewhere
    List<Post> findByUserId(Long userId);

 // Add this to PostRepository.java
     // --- NEW QUERIES FOR CONTROLLER ---
    
    // 1. For the /user/{userId}/portfolio & /me/portfolio endpoints (Paginated)
    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 2. For the /explore endpoint
    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByIsExploreTrueOrderByCreatedAtDesc(Pageable pageable);

    // 3. For the /me/drafts endpoint
    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(CAST(p.promptText AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.createdAt DESC")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    // --- NEW QUERIES FOR ADMIN DASHBOARD ---

    // Offloads the heavy calculation of total likes to the MySQL database
    @Query("SELECT COALESCE(SUM(p.upvoteCount), 0) FROM Post p")
    long countTotalLikes();

    // Fetches top 5 recent posts with the author eagerly loaded to prevent N+1 issues
    @EntityGraph(attributePaths = {"user"})
    List<Post> findTop5ByOrderByCreatedAtDesc();
}