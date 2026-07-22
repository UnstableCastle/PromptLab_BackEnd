package com.promptlab.server.service;

import com.promptlab.server.dto.AuthenticationRequest;
import com.promptlab.server.dto.AuthenticationResponse;
import com.promptlab.server.dto.RefreshTokenRequest;
import com.promptlab.server.dto.RegisterRequest;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
}