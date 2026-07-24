package com.promptlab.server.controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.dto.UserUpdateRequest;
import com.promptlab.server.entity.User;
import com.promptlab.server.payload.ApiResponse;
import com.promptlab.server.service.UserService;
import com.promptlab.server.service.FollowService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    // Inject both services into the controller
    public UserController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    // ==========================================
    //        PROFILE & CRUD OPERATIONS
    // ==========================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> getAllUsers(Principal principal) {
        String currentUsername = (principal != null) ? principal.getName() : null;
        List<UserProfileResponse> users = userService.getAllUsers(currentUsername);
        return ResponseEntity.ok(new ApiResponse<>(true, "All users fetched successfully", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(
            @PathVariable Long id, 
            Principal principal) {
        String currentUsername = (principal != null) ? principal.getName() : null;
        UserProfileResponse userProfile = userService.getUserById(id, currentUsername);
        return ResponseEntity.ok(new ApiResponse<>(true, "User fetched successfully", userProfile));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserByUsername(
            @PathVariable String username, 
            Principal principal) {
        String currentUsername = (principal != null) ? principal.getName() : null;
        UserProfileResponse userProfile = userService.getUserProfileByUsername(username, currentUsername);
        return ResponseEntity.ok(new ApiResponse<>(true, "User profile fetched successfully", userProfile));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateRequest request,
            Principal principal) {
        String currentUsername = (principal != null) ? principal.getName() : null;
        UserProfileResponse updatedUser = userService.updateUser(id, request, currentUsername);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }

    // ==========================================
    //          SOCIAL / FOLLOW OPERATIONS
    // ==========================================

    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        
        followService.followUser(user, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User followed successfully", null));
    }

    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User user) {
        
        followService.unfollowUser(user, userId);
        return ResponseEntity.ok(new ApiResponse<>(true, "User unfollowed successfully", null));
    }
}