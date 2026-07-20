package com.promptlab.server.service;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UpvoteRepository upvoteRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, UpvoteRepository upvoteRepository) {
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
        post.setContent(request.content());
        post.setAuthor(currentUser);
        post.setCreatedAt(LocalDateTime.now());
        
        Post savedPost = postRepository.save(post);

        return new PostResponse(
                savedPost.getId(), 
                savedPost.getContent(), 
                currentUser.getUsername(), 
                savedPost.getCreatedAt()
        );
    }

    public List<PostResponse> getAllPosts() {
        // Fetches all posts and maps them to secure DTOs
        return postRepository.findAll().stream()
                .map(post -> new PostResponse(
                        post.getId(),
                        post.getContent(),
                        post.getAuthor().getUsername(),
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
        } else {
            Upvote upvote = new Upvote();
            upvote.setUser(currentUser);
            upvote.setPost(post);
            upvoteRepository.save(upvote);
        }
    }
}