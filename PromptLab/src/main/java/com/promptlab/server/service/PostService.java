package com.promptlab.server.service;

import com.promptlab.server.dto.*;
import com.promptlab.server.entity.User;

import java.util.List;

import org.springframework.data.domain.Page;

public interface PostService {
    
    // --- DRAFT-FIRST METHODS ---
    PostResponse createDraftPost(User user);
    Object finalizeDraft(Long postId, User user, PostRequest request, String attachmentUrl);
    
    // --- EXISTING METHODS ---
    // Added User currentUser to check upvote status
    Page<PostResponse> getAllPosts(User currentUser, int page, int size);
    Object updatePost(Long postId, User user, PostRequest request);
    void deletePost(Long postId, User user);
    
    // Added User currentUser to check upvote status
    Object getPostById(Long postId, User currentUser);
    
    // Added User currentUser to check upvote status
    Page<PostResponse> searchPosts(String keyword, User currentUser, int page, int size);
 // Returns a List of posts for a specific user
    List<PostResponse> getPostByUserId(Long userId, User currentUser);
    // --- NEW / UPDATED METHODS ---
    // Added User currentUser to check upvote status
    Page<PostResponse> getPostsByUserId(Long userId, User currentUser, int page, int size); 
    
    // Added User currentUser to check upvote status
    Page<PostResponse> getExplorePosts(User currentUser, int page, int size);
    
    Page<PostResponse> getUserDrafts(Long userId, int page, int size);
}