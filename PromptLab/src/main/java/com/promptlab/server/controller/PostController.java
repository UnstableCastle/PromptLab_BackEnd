package com.promptlab.server.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.promptlab.server.dto.*;
import com.promptlab.server.entity.User;
import com.promptlab.server.payload.ApiResponse;
import com.promptlab.server.service.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UpvoteService upvoteService;
    private final FileService fileService;

    public PostController(PostService postService, UpvoteService upvoteService, FileService fileService) {
        this.postService = postService;
        this.upvoteService = upvoteService;
        this.fileService = fileService;
    }

    // --- NEW DRAFT-FIRST WORKFLOW ENDPOINTS ---

    // 1. BACKGROUND INIT: Creates the blank draft instantly
    @PostMapping("/init")
    public ResponseEntity<ApiResponse<PostResponse>> initializeDraft(@AuthenticationPrincipal User user) {
        // Requires a new method in PostService to save a draft state
        PostResponse draftPost = postService.createDraftPost(user); 
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Draft initialized", draftPost));
    }

    // 2. SUBMIT: Accepts the JSON text data and the MultipartFile in one single request
    @PutMapping(value = "/{postId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> submitPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestPart("postDetails") PostRequest request, 
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String attachmentUrl = null;
        
        // Handle the file upload if the user attached one
        if (file != null && !file.isEmpty()) {
            attachmentUrl = fileService.uploadPostFile(postId, user, file);
        }

        // Requires a new method in PostService to apply the request data, set the URL, and change status to PUBLISHED
        Object finalizedPost = postService.finalizeDraft(postId, user, request, attachmentUrl);

        return ResponseEntity.ok(new ApiResponse<>(true, "Post published successfully", finalizedPost));
    }

    // --- EXISTING ENDPOINTS ---

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> posts = postService.getAllPosts(page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Posts fetched successfully", posts));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> searchPosts(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PostResponse> posts = postService.searchPosts(keyword, page, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Posts searched successfully", posts));
    }

    @Transactional
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> getPostById(@PathVariable Long postId) {
        Object post = postService.getPostById(postId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post fetched successfully", post));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody PostRequest request) {
        Object updatedPost = postService.updatePost(postId, user, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post updated successfully", updatedPost));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        postService.deletePost(postId, user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Post deleted successfully", null));
    }

    @PostMapping("/{postId}/upvote")
    public ResponseEntity<ApiResponse<Void>> toggleUpvote(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        upvoteService.toggleUpvote(postId, user);
        return ResponseEntity.ok(new ApiResponse<>(true, "Upvote toggled successfully", null));
    }
 // Add this new endpoint inside your PostController class, perhaps under the existing GET methods

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByUserId(@PathVariable Long userId) {
        List<PostResponse> userPosts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User posts fetched successfully", userPosts));
    }
    
    
}