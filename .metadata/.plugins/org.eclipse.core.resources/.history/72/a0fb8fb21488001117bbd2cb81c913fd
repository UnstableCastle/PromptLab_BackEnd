package com.promptlab.server.repository;

import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph(attributePaths = {"user"})
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    @EntityGraph(attributePaths = {"user"})
    Page<Post> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUser(User user);
}