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
    private final UpvoteRepository upvoteRepository;

    public PostServiceImpl(PostRepository postRepository, UpvoteRepository upvoteRepository) {
        this.postRepository = postRepository;
        this.upvoteRepository = upvoteRepository;
    }

    @Override
    @Transactional
    public PostResponse createDraftPost(User user) {
        Post draft = Post.builder()
                .title("Untitled Draft") 
                .promptText("Draft content...") 
                .modelInfo("")
                .attachmentUrl(null)
                .user(user)
                .upvoteCount(0)
                .isExplore(false)
                .status("DRAFT")
                .build();

        Post savedDraft = postRepository.save(draft);
        return mapToPostResponse(savedDraft, user);
    }

    @Override
    @Transactional
    public Object finalizeDraft(Long postId, User user, PostRequest request, String attachmentUrl, String originalFilename) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Unauthorized to finalize this draft");
        }

        post.setTitle(request.title());
        post.setPromptText(request.promptText());
        post.setModelInfo(request.modelInfo());
        post.setStatus("PUBLISHED"); 
        
        if (attachmentUrl != null) {
            post.setAttachmentUrl(attachmentUrl);
            post.setOriginalFilename(originalFilename); 
        }

        Post updatedPost = postRepository.save(post);
        return mapToPostResponse(updatedPost, user);
    }

    @Override
    public Page<PostResponse> getAllPosts(User currentUser, int page, int size) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(post -> mapToPostResponse(post, currentUser));
    }

    @Override
    public Page<PostResponse> searchPosts(String keyword, User currentUser, int page, int size) {
        return postRepository.searchPosts(keyword, PageRequest.of(page, size))
                .map(post -> mapToPostResponse(post, currentUser));
    }

    @Override
    @Transactional
    public Object updatePost(Long postId, User user, PostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        post.setTitle(request.title());
        post.setPromptText(request.promptText());
        post.setModelInfo(request.modelInfo());
        post.setAttachmentUrl(request.attachmentUrl());

        Post updatedPost = postRepository.save(post);
        return mapToPostResponse(updatedPost, user);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        boolean isOwner = post.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Override
    public Object getPostById(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
        
        return mapToPostResponse(post, currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getPostsByUserId(Long userId, User currentUser, int page, int size) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .map(post -> mapToPostResponse(post, currentUser));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getExplorePosts(User currentUser, int page, int size) {
        return postRepository.findByIsExploreTrueOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(post -> mapToPostResponse(post, currentUser));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getUserDrafts(Long userId, int page, int size) {
        return postRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, "DRAFT", PageRequest.of(page, size))
                .map(post -> mapToPostResponse(post, null));
    }

    private PostResponse mapToPostResponse(Post post, User currentUser) {
        boolean hasUpvoted = false;
        if (currentUser != null) {
            hasUpvoted = upvoteRepository.existsByUserAndPost(currentUser, post);
        }
        return PostResponse.fromEntity(post, hasUpvoted);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostByUserId(Long userId, User currentUser) {
        return postRepository.findByUserId(userId).stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Override
    public Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));
    }
}