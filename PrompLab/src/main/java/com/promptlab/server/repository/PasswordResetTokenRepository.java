package com.promptlab.server.repository;

import com.promptlab.server.entity.PasswordResetToken;
import com.promptlab.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByUser(User user);
    Optional<PasswordResetToken> findByOtp(String otp);
    long deleteByUser(User user); 
}