package com.promptlab.server.controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.dto.UserUpdateRequest;
import com.promptlab.server.payload.ApiResponse;
import com.promptlab.server.service.UserService;
import com.promptlab.server.service.FollowService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FollowService followService;

    public UserController(UserService userService, FollowService followService) {
        this.userService = userService;
        this.followService = followService;
    }

    @GetMapping({"/getallusers"})
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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUser(
            @PathVariable Long id, 
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "bio", required = false) String bio,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePictureFile,
            Principal principal) {
        String currentUsername = (principal != null) ? principal.getName() : null;
        UserProfileResponse updatedUser = userService.updateUserWithFile(id, username, email, bio, profilePictureFile, currentUsername);
        return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id, 
            Principal principal) {
        
        String currentUsername = (principal != null) ? principal.getName() : null;
        if (currentUsername == null) {
             throw new RuntimeException("Unauthorized: Must be logged in to delete an account.");
        }

        userService.deleteUser(id, currentUsername);
        return ResponseEntity.ok(new ApiResponse<>(true, "User deleted successfully", null));
    }
}