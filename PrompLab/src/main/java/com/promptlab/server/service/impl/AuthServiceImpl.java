package com.promptlab.server.service.impl;

import java.time.Instant;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.promptlab.server.dto.*;
import com.promptlab.server.entity.*;
import com.promptlab.server.repository.*;
import com.promptlab.server.security.JwtService;
import com.promptlab.server.service.AuthService;
import com.promptlab.server.service.EmailService;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           JwtService jwtService, AuthenticationManager authenticationManager,
                           RefreshTokenRepository refreshTokenRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists.");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .isPrivate(false)
                .isSuspended(false)
                .build();

        userRepository.save(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found."));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshTokenEntity = createOrUpdateRefreshToken(user);

        return new AuthenticationResponse(
                accessToken, 
                refreshTokenEntity.getToken(), 
                "Bearer", 
                user.getUsername(), 
                user.getEmail(), 
                user.getRole().name()
        );
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        return refreshTokenRepository.findByToken(request.refreshToken())
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateAccessToken(user);
                    RefreshToken refreshTokenEntity = createOrUpdateRefreshToken(user);
                    return new AuthenticationResponse(
                            accessToken, 
                            refreshTokenEntity.getToken(), 
                            "Bearer", 
                            user.getUsername(), 
                            user.getEmail(), 
                            user.getRole().name()
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is invalid or missing!"));
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenRepository.deleteByToken(request.refreshToken());
        refreshTokenRepository.flush();
    }

    private RefreshToken createOrUpdateRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());
        
        refreshToken.setUser(user);
        refreshToken.setToken(jwtService.generateRefreshToken());
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    //-------------------------------------------------------------------
    // reset password
    
    @Override
    public void sendPasswordResetOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush(); // Forces delete execution before inserting the new OTP

        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setOtp(otp);
        resetToken.setUser(user);
        resetToken.setExpiryDate(Instant.now().plusMillis(300000));
        passwordResetTokenRepository.save(resetToken);

        emailService.sendEmail(
            user.getEmail(), 
            "Password Reset OTP Code", 
            "Your OTP code for password reset is: " + otp + ". It expires in 5 minutes."
        );
    }

    @Override
    public void verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("No active OTP request found for this user."));

        if (!resetToken.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP code.");
        }

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}