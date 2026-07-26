package com.promptlab.server.controller;

import com.promptlab.server.entity.User;
import com.promptlab.server.payload.ApiResponse;
import com.promptlab.server.service.FollowService;
import com.promptlab.server.service.UserService; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService; 

    @PostMapping("/{targetUserId}/follow")
    public ResponseEntity<ApiResponse<Void>> followUser(@PathVariable Long targetUserId, Authentication authentication) {
        // Fetch the currently authenticated user
        String currentUsername = authentication.getName(); 
        User currentUser = userService.findByUsername(currentUsername); 

        followService.followUser(currentUser, targetUserId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Follow request processed successfully.", null));
    }

    @DeleteMapping("/{targetUserId}/unfollow")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(@PathVariable Long targetUserId, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        followService.unfollowUser(currentUser, targetUserId);
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Successfully unfollowed user.", null));
    }
}