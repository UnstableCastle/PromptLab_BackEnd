package com.promptlab.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.promptlab.server.entity.User;
import com.promptlab.server.service.FollowService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/users")
public class UserController {

    private final FollowService followService;

    public UserController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Void> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        followService.followUser(user, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        followService.unfollowUser(user, userId);
        return ResponseEntity.ok().build();
    }
}