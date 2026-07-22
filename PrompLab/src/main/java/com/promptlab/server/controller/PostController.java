package com.promptlab.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.promptlab.server.dto.*;
import com.promptlab.server.entity.User;
import com.promptlab.server.service.*;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final UpvoteService upvoteService;

    public PostController(PostService postService, UpvoteService upvoteService) {
        this.postService = postService;
        this.upvoteService = upvoteService;
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
}