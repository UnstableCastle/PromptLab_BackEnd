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
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("Logged out successfully");
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.sendPasswordResetOtp(email);
        return ResponseEntity.ok("OTP sent to your email address.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.verifyOtpAndResetPassword(request.email(), request.otp(), request.newPassword());
        return ResponseEntity.ok("Password has been successfully reset.");
    }
    
    //-------------- 
    
    @PostMapping("/verify-account")
    public ResponseEntity<ApiResponse<Void>> verifyAccount(@Valid @RequestBody VerificationRequest request) {
        authService.verifyAccount(request.email(), request.otp());
        return ResponseEntity.ok(new ApiResponse<>(true, "Account verified successfully. You may now log in."));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, 
            Principal principal) { 
        
        authService.changePassword(principal.getName(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Password updated successfully."));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AccountDetailsResponse>> getCurrentUser(Principal principal) {
        AccountDetailsResponse user = authService.getAccountDetails(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Account details fetched successfully", user));
    }
}