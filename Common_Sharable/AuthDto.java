package com.promptlab.server.dto;

public class AuthDto {
    
    public record RegisterRequest(
            String username,
            String email,
            String password
    ) {}

    public record LoginRequest(
            String username,
            String password
    ) {}

    public record AuthResponse(
            String token,
            String username,
            Long userId
    ) {}
}