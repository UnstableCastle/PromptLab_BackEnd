package com.promptlab.server.service;

import com.promptlab.server.dto.PostRequest;
import com.promptlab.server.dto.PostResponse;
import org.springframework.data.domain.Page; // Ensure this is imported

import java.util.List;

public interface PostService {
    
    PostResponse createPost(PostRequest request);
    
    List<PostResponse> getAllPosts();
    
    // Add the missing method signature here
    Page<PostResponse> getAllPosts(int page, int size);
    
    void upvotePost(Long postId);
}