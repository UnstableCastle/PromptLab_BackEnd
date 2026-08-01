package com.promptlab.server.dto;

public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    String username,
    String email,
    Long Id,
    String role,
    String bio,
    String profilePicture
) {}