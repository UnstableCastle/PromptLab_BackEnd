package com.promptlab.server.service;

import com.promptlab.server.dto.*;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
    AuthenticationResponse refreshToken(RefreshTokenRequest request);
    void logout(RefreshTokenRequest request);
}