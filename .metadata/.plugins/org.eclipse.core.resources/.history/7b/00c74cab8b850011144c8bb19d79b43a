package com.promptlab.server.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.promptlab.server.dto.AuthenticationRequest;
import com.promptlab.server.dto.AuthenticationResponse;
import com.promptlab.server.dto.RefreshTokenRequest;
import com.promptlab.server.dto.RegisterRequest;
import com.promptlab.server.entity.RefreshToken;
import com.promptlab.server.entity.Role;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.RefreshTokenRepository;
import com.promptlab.server.repository.UserRepository;
import com.promptlab.server.security.JwtService;
import com.promptlab.server.service.AuthService;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            RefreshTokenRepository refreshTokenRepository) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists.");
        }

        User user = new User();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));

        user.setRole(Role.ROLE_USER);
        user.setPrivate(false);
        user.setSuspended(false);

        userRepository.save(user);

        // Generate both Access and Refresh Tokens
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return new AuthenticationResponse(
                accessToken, 
                refreshToken.getToken(), 
                "Bearer", 
                user.getUsername(), 
                user.getRole().name()
        );
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(), 
                        request.password())); 

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Generate both Access and Refresh Tokens
        String accessToken = jwtService.generateToken(user);
        RefreshToken refreshToken = createRefreshToken(user);

        return new AuthenticationResponse(
                accessToken, 
                refreshToken.getToken(), 
                "Bearer", 
                user.getUsername(), 
                user.getRole().name()
        );
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.refreshToken();

        return refreshTokenRepository.findByToken(requestRefreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Token Rotation: Delete old refresh token and issue a new one
                    refreshTokenRepository.deleteByToken(requestRefreshToken);
                    
                    String accessToken = jwtService.generateToken(user);
                    RefreshToken newRefreshToken = createRefreshToken(user);

                    return new AuthenticationResponse(
                            accessToken,
                            newRefreshToken.getToken(),
                            "Bearer",
                            user.getUsername(),
                            user.getRole().name()
                    );
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        // Set refresh token expiration to 7 days (604800000 ms)
        refreshToken.setExpiryDate(Instant.now().plusMillis(604800000)); 
        refreshToken.setToken(UUID.randomUUID().toString());
        
        return refreshTokenRepository.save(refreshToken);
    }

    private RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new login request.");
        }
        return token;
    }

	@Override
	public void logout(RefreshTokenRequest request) {
		// TODO Auto-generated method stub
		refreshTokenRepository.deleteByToken(request.refreshToken());
	}
}