package com.promptlab.server.service.impl;

import com.promptlab.server.dto.PostRequest;
import com.promptlab.server.dto.PostResponse;
import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.User;
import com.promptlab.server.entity.Upvote;
import com.promptlab.server.repository.PostRepository;
import com.promptlab.server.repository.UserRepository;
import com.promptlab.server.repository.UpvoteRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UpvoteRepository upvoteRepository;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository, UpvoteRepository upvoteRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.upvoteRepository = upvoteRepository;
    }

    // Helper method to get the currently authenticated user
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public PostResponse createPost(PostRequest request) {
        User currentUser = getCurrentUser();

        Post post = new Post();
        
        // Mapped to the actual fields in your updated Post.java
        post.setPromptText(request.content()); 
        
        // IMPORTANT: Because 'title' is 'nullable = false' in your Post.java, you MUST set it.
        // This assumes your PostRequest DTO/Record has a title() method.
        post.setTitle(request.title()); 
        
        post.setUser(currentUser);
        
        // Note: post.setCreatedAt() is intentionally removed here. 
        // Your @CreationTimestamp annotation in Post.java handles it automatically!

        Post savedPost = postRepository.save(post);

        return new PostResponse(
                savedPost.getId(), 
                savedPost.getPromptText(), 
                currentUser.getUsername(), 
                savedPost.getCreatedAt()
        );
    }

    public List<PostResponse> getAllPosts() {
        // Fetches all posts and maps them to secure DTOs
        return postRepository.findAll().stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getPromptText(), // Updated to match Post.java
                        post.getUser().getUsername(), // Updated to match Post.java
                        post.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void upvotePost(Long postId) {
        User currentUser = getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Logic to toggle upvote (if already upvoted, remove it; otherwise, add it)
        boolean alreadyUpvoted = upvoteRepository.existsByUserAndPost(currentUser, post);
        
        if (alreadyUpvoted) {
            upvoteRepository.deleteByUserAndPost(currentUser, post);
            // Decrement the upvote count on the post entity
            post.setUpvoteCount(post.getUpvoteCount() - 1);
        } else {
            Upvote upvote = new Upvote();
            upvote.setUser(currentUser);
            upvote.setPost(post);
            upvoteRepository.save(upvote);
            // Increment the upvote count on the post entity
            post.setUpvoteCount(post.getUpvoteCount() + 1);
        }
        
        // Save the updated upvote count to the database
        postRepository.save(post); 
    }
}