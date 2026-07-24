package com.promptlab.server.dto;

public record ResetPasswordRequest(
    String email,
    String otp,
    String newPassword
) {}