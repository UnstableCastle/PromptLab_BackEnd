package com.promptlab.server.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.Upvote;
import com.promptlab.server.entity.User;

@Repository
public interface UpvoteRepository extends JpaRepository<Upvote, Long> {
    boolean existsByUserAndPost(User user, Post post);
    Optional<Upvote> findByUserAndPost(User user, Post post);

    @Modifying
    @Query("DELETE FROM Upvote u WHERE u.user = :user AND u.post = :post")
    void deleteByUserAndPost(@Param("user") User user, @Param("post") Post post);
}