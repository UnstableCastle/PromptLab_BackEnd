package com.promptlab.server.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String profilePicture;
    private boolean isPrivate;
    private String role;
    private LocalDateTime createdAt;
    
    private long followersCount;
    private long followingCount;
    private long postsCount;
    
    private boolean followedByCurrentUser;

    // --- NEW TOKEN FIELDS ---
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

    // Properly mapped 8-argument constructor used by UserServiceImpl
    public UserProfileResponse(String username, String bio, String profilePicture, boolean isPrivate,
            long followersCount, long followingCount, long postsCount, boolean followedByCurrentUser) {
        this.username = username;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.isPrivate = isPrivate;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.postsCount = postsCount;
        this.followedByCurrentUser = followedByCurrentUser;
    }
}