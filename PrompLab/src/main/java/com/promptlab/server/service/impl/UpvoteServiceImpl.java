package com.promptlab.server.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.promptlab.server.entity.*;
import com.promptlab.server.repository.*;
import com.promptlab.server.service.UpvoteService;

@Service
@Transactional
public class UpvoteServiceImpl implements UpvoteService {

    private final UpvoteRepository upvoteRepository;
    private final PostRepository postRepository;

    public UpvoteServiceImpl(UpvoteRepository upvoteRepository, PostRepository postRepository) {
        this.upvoteRepository = upvoteRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void toggleUpvote(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found."));

        if (upvoteRepository.existsByUserAndPost(user, post)) {
            upvoteRepository.deleteByUserAndPost(user, post);
            post.setUpvoteCount(Math.max(0, post.getUpvoteCount() - 1));
        } else {
            Upvote upvote = Upvote.builder().user(user).post(post).build();
            upvoteRepository.save(upvote);
            post.setUpvoteCount(post.getUpvoteCount() + 1);
        }

        postRepository.save(post);
    }
}