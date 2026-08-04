package com.promptlab.server.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UpvoteService upvoteService;
    private final FileService fileService;

    public PostController(PostService postService,
                          UpvoteService upvoteService,
                          FileService fileService) {
        this.postService = postService;
        this.upvoteService = upvoteService;
        this.fileService = fileService;
    }

    @PostMapping("/init")
    public ResponseEntity<ApiResponse<PostResponse>> initializeDraft(
            @AuthenticationPrincipal User user) {

        PostResponse draftPost = postService.createDraftPost(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Draft initialized", draftPost));
    }

    @PutMapping(value = "/{postId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Object>> submitPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestPart("postDetails") PostRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        String attachmentUrl = null;
        String originalFilename = null;

        if (file != null && !file.isEmpty()) {
            originalFilename = file.getOriginalFilename();
            attachmentUrl = fileService.uploadPostFile(postId, user, file);
        }

        Object finalizedPost = postService.finalizeDraft(postId, user, request, attachmentUrl, originalFilename);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post published successfully", finalizedPost));
    }

    // ================= DOWNLOAD FILE =================

    @GetMapping("/{postId}/download")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long postId) throws Exception {
        
        com.promptlab.server.entity.Post post = postService.getPostEntity(postId);

        String fileUrl = post.getAttachmentUrl();
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new RuntimeException("No attachment found for this post.");
        }

        String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
        Path filePath = Paths.get(relativePath).normalize().toAbsolutePath();

        if (!Files.exists(filePath)) {
            throw new RuntimeException("File is missing from the server.");
        }

        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        String downloadName = post.getOriginalFilename() != null ? post.getOriginalFilename() : "downloaded_file";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + downloadName + "\"")
                .body(resource);
    }

    // =====================================================

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getAllPosts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponse> posts = postService.getAllPosts(user, page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Feed fetched successfully", posts));
    }

    @GetMapping("/explore")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getExplorePosts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponse> posts = postService.getExplorePosts(user, page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Explore feed fetched successfully", posts));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> searchPosts(
            @AuthenticationPrincipal User user,
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponse> posts = postService.searchPosts(keyword, user, page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Search results fetched", posts));
    }

    @Transactional(readOnly = true)
    @GetMapping("/detail/{postId}")
    public ResponseEntity<ApiResponse<Object>> getPostById(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {

        Object post = postService.getPostById(postId, user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post details fetched", post));
    }

    @PutMapping("/detail/{postId}")
    public ResponseEntity<ApiResponse<Object>> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody PostRequest request) {

        Object updatedPost = postService.updatePost(postId, user, request);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post updated successfully", updatedPost));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {

        postService.deletePost(postId, user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Post deleted successfully", null));
    }

    @PostMapping("/detail/{postId}/upvote")
    public ResponseEntity<ApiResponse<Void>> toggleUpvote(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user) {

        upvoteService.toggleUpvote(postId, user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Upvote toggled successfully", null));
    }

    @GetMapping("/user/{userId}/portfolio")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getPostsByUserId(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponse> userPosts = postService.getPostsByUserId(userId, user, page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "User portfolio fetched", userPosts));
    }

    @GetMapping("/me/portfolio")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getMyPosts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponse> userPosts = postService.getPostsByUserId(user.getId(), user, page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Your portfolio fetched", userPosts));
    }

    @GetMapping("/me/drafts")
    public ResponseEntity<ApiResponse<Page<PostResponse>>> getMyDrafts(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<PostResponse> drafts = postService.getUserDrafts(user.getId(), page, size);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Drafts fetched", drafts));
    }
}