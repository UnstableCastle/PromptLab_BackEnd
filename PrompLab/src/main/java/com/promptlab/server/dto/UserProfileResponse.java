package com.promptlab.server.dto;

public record UserProfileResponse(
    String username,
    String bio,
    String profilePictureUrl
) {}