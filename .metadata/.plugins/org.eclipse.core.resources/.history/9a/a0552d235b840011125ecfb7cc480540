package com.promptlab.server.repository;

import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.Upvote;
import com.promptlab.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpvoteRepository extends JpaRepository<Upvote, Long> {
    // Spring translates these directly into SQL: 
    // SELECT COUNT(*) FROM upvotes WHERE user_id = ? AND post_id = ?
    boolean existsByUserAndPost(User user, Post post);
    
    // DELETE FROM upvotes WHERE user_id = ? AND post_id = ?
    void deleteByUserAndPost(User user, Post post);
}