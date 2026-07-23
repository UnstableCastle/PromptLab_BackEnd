package com.promptlab.server.service.impl;

import com.promptlab.server.dto.*;
import com.promptlab.server.entity.*;
import com.promptlab.server.repository.*;
import com.promptlab.server.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public PostResponse createPost(User user, PostRequest request) {
        Post post = Post.builder()
                .title(request.title())
                .promptText(request.promptText())
                .modelInfo(request.modelInfo())
                .attachmentUrl(request.attachmentUrl())
                .user(user)
                .upvoteCount(0)
                .isExplore(false)
                .build();

        Post savedPost = postRepository.save(post);

        return new PostResponse(
                savedPost.getId(),
                savedPost.getTitle(),
                savedPost.getPromptText(),
                savedPost.getModelInfo(),
                savedPost.getAttachmentUrl(),
                savedPost.getUpvoteCount(),
                savedPost.isExplore(),
                user.getUsername(),
                savedPost.getCreatedAt()
        );
    }

    @Override
    public Page<PostResponse> getAllPosts(int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getTitle(),
                        post.getPromptText(),
                        post.getModelInfo(),
                        post.getAttachmentUrl(),
                        post.getUpvoteCount(),
                        post.isExplore(),
                        post.getUser().getUsername(),
                        post.getCreatedAt()
                ));
    }
}