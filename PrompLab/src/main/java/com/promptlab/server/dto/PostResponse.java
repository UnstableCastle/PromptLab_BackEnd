package com.promptlab.server.dto;

import java.time.LocalDateTime;

public record PostResponse(
    Long id,
    String content,
    String authorUsername,
    LocalDateTime createdAt
) {}