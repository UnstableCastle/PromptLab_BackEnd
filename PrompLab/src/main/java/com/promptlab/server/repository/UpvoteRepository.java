package com.promptlab.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.Upvote;
import com.promptlab.server.entity.User;

@Repository
public interface UpvoteRepository extends JpaRepository<Upvote, Long> {

    boolean existsByUserAndPost(User user, Post post);

    Optional<Upvote> findByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);

    long countByPost(Post post);

    List<Upvote> findByUser(User user);

}