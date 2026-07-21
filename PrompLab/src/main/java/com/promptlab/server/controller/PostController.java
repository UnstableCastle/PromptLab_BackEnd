package com.promptlab.server.controller;

import com.promptlab.server.dto.PostRequest;
import com.promptlab.server.dto.PostResponse;
import com.promptlab.server.service.impl.PostServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostServiceImpl postServiceImpl;

    // Manual constructor injection
    public PostController(PostServiceImpl postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    // Endpoint to create a new post
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postServiceImpl.createPost(request));
    }

    // Endpoint to fetch a general feed of posts
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postServiceImpl.getAllPosts());
    }

    // Endpoint to toggle an upvote on a specific post
    @PostMapping("/{postId}/upvote")
    public ResponseEntity<Void> upvotePost(@PathVariable Long postId) {
        postServiceImpl.upvotePost(postId);
        return ResponseEntity.ok().build(); // Returns a 200 OK with no body
    }
}