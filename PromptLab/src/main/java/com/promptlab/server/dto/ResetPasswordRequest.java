package com.promptlab.server.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
		@NotBlank(message = "Please Enter your Email")
		String email,
		@NotBlank(message = "Check Your Email For OTP")
		String otp,
		@NotBlank(message = "Enter your New Password")
		String newPassword
) {}