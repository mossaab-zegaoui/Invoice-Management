package com.example.securebusiness.repository;

import com.example.securebusiness.model.PasswordResetToken;
import com.example.securebusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
   Optional<PasswordResetToken> findByUser(User user);
   Optional<PasswordResetToken> findByToken(String token);
}
