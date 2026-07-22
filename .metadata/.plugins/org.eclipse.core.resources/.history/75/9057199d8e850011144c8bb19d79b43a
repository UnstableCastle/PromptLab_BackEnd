package com.promptlab.server.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.promptlab.server.dto.PostRequest;
import com.promptlab.server.dto.PostResponse;
import com.promptlab.server.service.PostService;
import com.promptlab.server.service.UpvoteService;

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
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        PostResponse createdPost = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    @PostMapping("/{postId}/upvote")
    public ResponseEntity<Void> toggleUpvote(@PathVariable Long postId) {
        upvoteService.toggleUpvote(postId);
        return ResponseEntity.ok().build();
    }
}