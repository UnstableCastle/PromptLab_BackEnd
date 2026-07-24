package com.promptlab.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.promptlab.server.dto.*;
import com.promptlab.server.entity.User;
import com.promptlab.server.service.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UpvoteService upvoteService;
    private final FileService fileService; // Added for the multipart file upload

    public PostController(PostService postService, UpvoteService upvoteService, FileService fileService) {
        this.postService = postService;
        this.upvoteService = upvoteService;
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal User user,
            @RequestBody PostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(user, request));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @PostMapping("/{postId}/upvote")
    public ResponseEntity<Void> toggleUpvote(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        upvoteService.toggleUpvote(postId, user);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // NEW ENDPOINTS ADDED FROM image_dfe2f9.jpg
    // ==========================================

    @PutMapping("/{postId}")
    public ResponseEntity<Object> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody PostRequest request) {
        // Authenticated by AccessToken via @AuthenticationPrincipal
        return ResponseEntity.ok(postService.updatePost(postId, user, request));
    }
   
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {
        // Authenticated by AccessToken via @AuthenticationPrincipal
        postService.deletePost(postId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/upload")
    public ResponseEntity<String> uploadFile(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        // Authenticated by AccessToken via @AuthenticationPrincipal
        // Pass the file to a service to handle saving it locally or to cloud storage (like AWS S3)
        String fileUrl = fileService.uploadPostFile(postId, user, file);
        return ResponseEntity.ok("File uploaded successfully: " + fileUrl);
    }
}