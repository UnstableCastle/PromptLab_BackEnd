package com.promptlab.server.service;

import com.promptlab.server.dto.*;
import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.User;

import java.util.List;
import org.springframework.data.domain.Page;

public interface PostService {
    
    PostResponse createDraftPost(User user);
    
    Object finalizeDraft(Long postId, User user, PostRequest request, String attachmentUrl, String originalFilename);
    
    Page<PostResponse> getAllPosts(User currentUser, int page, int size);
    
    Object updatePost(Long postId, User user, PostRequest request);
   
    void deletePost(Long postId, User user);
    
    Page<PostResponse> searchPosts(String keyword, User currentUser, int page, int size);
    
    List<PostResponse> getPostByUserId(Long userId, User currentUser);
    
    Page<PostResponse> getPostsByUserId(Long userId, User currentUser, int page, int size); 
    
    Page<PostResponse> getExplorePosts(User currentUser, int page, int size);
    
    Page<PostResponse> getUserDrafts(Long userId, int page, int size);
    
    Post getPostEntity(Long postId);
    
    Object getPostById(Long postId, User user);
}