package com.example.securebusiness.repository;

import com.example.securebusiness.model.AuthenticationToken;
import com.example.securebusiness.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Long> {
   Optional<AuthenticationToken> findByUser(User user);
   Optional<AuthenticationToken> findByToken(String token);
}
