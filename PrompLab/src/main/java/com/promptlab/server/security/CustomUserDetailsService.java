package com.promptlab.server.security;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.promptlab.server.entity.User;
import com.promptlab.server.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
    private final UserRepository userRepository;
    
    // MANUAL CONSTRUCTOR: This replaces @RequiredArgsConstructor
    // Spring Boot will automatically inject the repository here.
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with username: " + username));

        if (user.isSuspended()) {
            throw new DisabledException("Account is suspended.");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(new SimpleGrantedAuthority(user.getRole()))
                .build();
    }
}