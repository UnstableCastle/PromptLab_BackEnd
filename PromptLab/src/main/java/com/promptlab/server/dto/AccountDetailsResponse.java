package com.promptlab.server.dto;

public record AccountDetailsResponse(
    Long id,
    String username,
    String email,
    String role
) {}