package com.promptlab.server.dto;

import com.promptlab.server.entity.Post;
import java.time.LocalDateTime;

public record PostResponse(
        Long id, 
        String title, 
        String promptText, 
        String modelInfo, 
        String attachmentUrl, 
        Integer upvoteCount, 
        Boolean isExplore, 
        Long userId,  
        String authorUsername,  
        String profilePicture,
        String status,          
        LocalDateTime createdAt,
        Boolean hasUpvoted
) {

    // Static factory method to convert a JPA entity to this DTO record with upvote tracking
    public static PostResponse fromEntity(Post post, Boolean hasUpvoted) {
        if (post == null) {
            return null;
        }
        
        // Safely extract user details to prevent NullPointerExceptions
        Long authorId = post.getUser() != null ? post.getUser().getId() : null;
        String authorName = post.getUser() != null ? post.getUser().getUsername() : null;
        String profilePicture = post.getUser().getProfilePicture();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getPromptText(),
                post.getModelInfo(),
                post.getAttachmentUrl(),
                post.getUpvoteCount(),
                post.isExplore(), 
                authorId,
                authorName,
                profilePicture,
                post.getStatus(),
                post.getCreatedAt(),
                hasUpvoted
        );
    }
}