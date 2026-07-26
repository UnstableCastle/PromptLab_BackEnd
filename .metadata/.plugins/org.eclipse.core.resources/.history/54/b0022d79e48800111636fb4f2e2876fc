package com.promptlab.server.controller;

import com.promptlab.server.entity.User;
import com.promptlab.server.service.FollowService;
import com.promptlab.server.service.UserService; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService; 

    @PostMapping("/{targetUserId}/follow")
    public ResponseEntity<String> followUser(@PathVariable Long targetUserId, Authentication authentication) {
        // Fetch the currently authenticated user
        // Adjust this depending on how you extract the user from your security context/JWT
        String currentUsername = authentication.getName(); 
        User currentUser = userService.findByUsername(currentUsername); 

        followService.followUser(currentUser, targetUserId);
        
        return ResponseEntity.ok("Follow request processed successfully.");
    }

    @DeleteMapping("/{targetUserId}/unfollow")
    public ResponseEntity<String> unfollowUser(@PathVariable Long targetUserId, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        followService.unfollowUser(currentUser, targetUserId);
        
        return ResponseEntity.ok("Successfully unfollowed user.");
    }
}