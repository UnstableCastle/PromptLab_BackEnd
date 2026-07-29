package com.promptlab.server.controller;

import java.security.Principal;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.promptlab.server.dto.*;
import com.promptlab.server.payload.ApiResponse;
import com.promptlab.server.service.AuthService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.authenticate(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", response));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Logged out successfully"));
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestParam String email) {
        authService.sendPasswordResetOtp(email);
        return ResponseEntity.ok(new ApiResponse<>(true, "OTP sent to your email address."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.verifyOtpAndResetPassword(request.email(), request.otp(), request.newPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Password has been successfully reset."));
    }
    
    //-------------- 
    
//    @PostMapping("/verify-account")
//    public ResponseEntity<ApiResponse<Void>> verifyAccount(@Valid @RequestBody VerificationRequest request) {
//        authService.verifyAccount(request.email(), request.otp());
//        return ResponseEntity.ok(new ApiResponse<>(true, "Account verified successfully. You may now log in."));
//    }


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountDetailsResponse>> getCurrentUser(Principal principal) {
        AccountDetailsResponse user = authService.getAccountDetails(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Account details fetched successfully", user));
    }
}