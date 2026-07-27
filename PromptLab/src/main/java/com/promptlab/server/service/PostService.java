package com.promptlab.server.service;

import com.promptlab.server.dto.*;
import com.promptlab.server.entity.User;
import org.springframework.data.domain.Page;
import java.util.List; // Add this import

public interface PostService {
    
    // --- NEW DRAFT-FIRST METHODS ---
    PostResponse createDraftPost(User user);
    Object finalizeDraft(Long postId, User user, PostRequest request, String attachmentUrl);
    
    // --- EXISTING METHODS ---
    Page<PostResponse> getAllPosts(int page, int size);
    Object updatePost(Long postId, User user, PostRequest request);
    void deletePost(Long postId, User user);
    Object getPostById(Long postId);
    Page<PostResponse> searchPosts(String keyword, int page, int size);
    
    // --- NEW METHOD ---
    List<PostResponse> getPostsByUserId(Long userId);
}