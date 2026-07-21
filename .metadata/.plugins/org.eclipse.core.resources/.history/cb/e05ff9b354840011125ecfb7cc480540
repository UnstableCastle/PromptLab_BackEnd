package com.promptlab.server.service;

import com.promptlab.server.dto.AuthenticationRequest;
import com.promptlab.server.dto.AuthenticationResponse;
import com.promptlab.server.dto.RegisterRequest;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        // 1. Create a new user entity and encode the password
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        
        // 2. Save to the MySQL database
        userRepository.save(user);
        
        // 3. Generate the JWT token for the newly registered user
        String jwtToken = jwtService.generateToken(user);
        
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // 1. Authenticate user credentials via Spring Security
        // This will automatically fail and throw an exception if the password is wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        
        // 2. If we reach here, credentials are correct. Fetch the user.
        User user = userRepository.findByUsername(request.username())
                .orElseThrow();
                
        // 3. Generate and return the JWT token
        String jwtToken = jwtService.generateToken(user);
        
        return new AuthenticationResponse(jwtToken);
    }
}