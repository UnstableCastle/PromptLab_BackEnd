package com.promptlab.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @AuthenticationPrincipal User user,
            @RequestBody PostRequest request) {
        PostResponse response = postService.createPost(user, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Post created successfully", response));
    }

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

    @PostMapping("/{postId}/upload")
    public ResponseEntity<ApiResponse<String>> uploadFile(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        String fileUrl = fileService.uploadPostFile(postId, user, file);
        return ResponseEntity.ok(new ApiResponse<>(true, "File uploaded successfully", fileUrl));
    }
}