package com.promptlab.server.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    
    @Size(max = 150, message = "Bio cannot exceed 150 characters")
    String bio,

    String profilePicture,

    Boolean isPrivate

) {
}