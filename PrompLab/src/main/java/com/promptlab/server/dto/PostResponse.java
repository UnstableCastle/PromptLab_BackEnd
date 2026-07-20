package com.promptlab.server.dto;

import java.time.LocalDateTime;

public record PostResponse(

    Long id,

    String title,

    String promptText,

    String modelInfo,

    String attachmentUrl,

    Integer upvoteCount,

    boolean isExplore,

    String authorUsername,

    LocalDateTime createdAt

) {
}