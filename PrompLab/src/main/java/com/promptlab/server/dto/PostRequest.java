package com.promptlab.server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequest(

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    String title,

    @NotBlank(message = "Prompt text is required")
    String promptText,

    @Size(max = 100, message = "Model info must not exceed 100 characters")
    String modelInfo,

    @Size(max = 512, message = "Attachment URL must not exceed 512 characters")
    String attachmentUrl

) {
}