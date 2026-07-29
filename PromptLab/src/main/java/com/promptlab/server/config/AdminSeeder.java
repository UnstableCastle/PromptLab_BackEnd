package com.promptlab.server.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.promptlab.server.entity.Role;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.UserRepository;

@Configuration
public class AdminSeeder {

    @Bean // REQUIRED: This tells Spring to run this method on startup
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Fixed the typo in the email string below
            if (userRepository.findByEmail("promptlab.developer@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setUsername("Admin");
                admin.setEmail("promptlab.developer@gmail.com"); 
                admin.setPasswordHash(passwordEncoder.encode("admin123"));
                admin.setRole(Role.ROLE_ADMIN); // Ensure ROLE_ADMIN exists in your Role enum
                
                userRepository.save(admin);
                
                System.out.println("Admin created !!!");
            }
        };
    }
}