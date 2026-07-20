package com.promptlab.server.controller;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Manual constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Endpoint to view a specific user's public profile
    @GetMapping("/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    // Endpoint to follow or unfollow a user
    @PostMapping("/{username}/follow")
    public ResponseEntity<Void> followUser(@PathVariable String username) {
        userService.followUser(username);
        return ResponseEntity.ok().build();
    }
    
    // Endpoint to report a user to the system admin
    @PostMapping("/{username}/report")
    public ResponseEntity<Void> reportUser(@PathVariable String username, @RequestBody String reason) {
        userService.reportUser(username, reason);
        return ResponseEntity.ok().build();
    }
}