package com.promptlab.server.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // The identifier can be either a username (from JWT) or an email (from Login request)
        User user = userRepository.findByUsername(identifier)
                .orElseGet(() -> userRepository.findByEmail(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identifier)));

        if (user.isSuspended()) {
            throw new DisabledException("Your account has been suspended.");
        }

        return user;
    }
}