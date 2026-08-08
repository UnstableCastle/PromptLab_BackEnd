package com.promptlab.server.controller;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.promptlab.server.dto.UserProfileResponse;
import com.promptlab.server.entity.RefreshToken; // Ensure this matches your package
import com.promptlab.server.entity.User;
import com.promptlab.server.payload.ApiResponse;
import com.promptlab.server.service.UserService;
import com.promptlab.server.service.FollowService;
import com.promptlab.server.security.JwtService; 
import com.promptlab.server.repository.RefreshTokenRepository; // Adjust to your service if preferred

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final FollowService followService;
    private final JwtService jwtService; 
    private final RefreshTokenRepository refreshTokenRepository; // Injected to update database

    public UserController(UserService userService, 
                          FollowService followService, 
                          JwtService jwtService, 
                          RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.followService = followService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
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
        
        // 1. Update user details in the database via service
        UserProfileResponse updatedUser = userService.updateUserWithFile(id, username, email, bio, profilePictureFile, currentUsername);

        // 2. Generate fresh tokens and update database tracking
        if (updatedUser != null && updatedUser.getUsername() != null) {
            User newlyUpdatedUserEntity = userService.findByUsername(updatedUser.getUsername());
            
            if (newlyUpdatedUserEntity != null) {
                // Generate new access token matching UserDetails
                String newAccessToken = jwtService.generateAccessToken(newlyUpdatedUserEntity); 
                
                // Create or update the refresh token inside the database
                RefreshToken savedRefreshToken = createOrUpdateRefreshToken(newlyUpdatedUserEntity);
                String newRefreshToken = savedRefreshToken.getToken();
                
                // Attach tokens to response DTO so frontend can sync them instantly
                updatedUser.setAccessToken(newAccessToken);
                updatedUser.setRefreshToken(newRefreshToken);
            }
        }

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

    /**
     * Helper method to create or update the refresh token in the database
     */
    private RefreshToken createOrUpdateRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());
        
        refreshToken.setUser(user);
        refreshToken.setToken(jwtService.generateRefreshToken());
        
        return refreshTokenRepository.save(refreshToken);
    }
}