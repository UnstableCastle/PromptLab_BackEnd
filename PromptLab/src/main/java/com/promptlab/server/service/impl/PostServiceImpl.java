package com.promptlab.server.service.impl;

import com.promptlab.server.dto.*;
import com.promptlab.server.entity.*;
import com.promptlab.server.repository.*;
import com.promptlab.server.service.PostService;

import java.util.List;
import java.util.stream.Collectors;

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

    // --- NEW DRAFT-FIRST METHODS ---

    @Override
    @Transactional
    public PostResponse createDraftPost(User user) {
        // Initialize with dummy data to satisfy @NotBlank database constraints
        Post draft = Post.builder()
                .title("Untitled Draft") 
                .promptText("Draft content...") 
                .modelInfo("")
                .attachmentUrl(null)
                .user(user)
                .upvoteCount(0)
                .isExplore(false)
                .build();

        Post savedDraft = postRepository.save(draft);

        return mapToPostResponse(savedDraft);
    }

    @Override
    @Transactional
    public Object finalizeDraft(Long postId, User user, PostRequest request, String attachmentUrl) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Ensure only the owner can finalize their draft
        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        // Apply the actual user data to the draft
        post.setTitle(request.title());
        post.setPromptText(request.promptText());
        post.setModelInfo(request.modelInfo());
        
        // Only update the attachment URL if a new file was uploaded during submission
        if (attachmentUrl != null) {
            post.setAttachmentUrl(attachmentUrl);
        }

        Post updatedPost = postRepository.save(post);

        return mapToPostResponse(updatedPost);
    }

    // --- EXISTING METHODS ---

    @Override
    public Page<PostResponse> getAllPosts(int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(this::mapToPostResponse);
    }

    @Override
    public Page<PostResponse> searchPosts(String keyword, int page, int size) {
        return postRepository.searchPosts(keyword, PageRequest.of(page, size))
                .map(this::mapToPostResponse);
    }

    @Override
    @Transactional
    public Object updatePost(Long postId, User user, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Ensure only the owner can update their post
        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        // Update fields if provided
        post.setTitle(request.title());
        post.setPromptText(request.promptText());
        post.setModelInfo(request.modelInfo());
        post.setAttachmentUrl(request.attachmentUrl());

        Post updatedPost = postRepository.save(post);

        return mapToPostResponse(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        // Ensure only the owner can delete their post
        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Override
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        return mapToPostResponse(post);
    }

    private PostResponse mapToPostResponse(Post post) {
        return new PostResponse(
            post.getId(),
            post.getTitle(),
            post.getPromptText(),
            post.getModelInfo(),
            post.getAttachmentUrl(),
            post.getUpvoteCount(),
            post.isExplore(),
            post.getStatus(),
            post.getUser() != null ? post.getUser().getUsername() : null,
            post.getCreatedAt()
        );
    }

	@Override
	@Transactional
	public List<PostResponse> getPostsByUserId(Long userId) {
		// TODO Auto-generated method stub
		return postRepository.findByUserId(userId).stream()
                .map(this::mapToPostResponse)
                .collect(Collectors.toList());
	}
}