package com.example.securebusiness.service.impl;

import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.PasswordDto;
import com.example.securebusiness.model.AuthenticationToken;
import com.example.securebusiness.model.User;
import com.example.securebusiness.repository.AuthenticationTokenRepository;
import com.example.securebusiness.repository.UserRepository;
import com.example.securebusiness.service.EmailService;
import com.example.securebusiness.service.PasswordResetService;
import com.example.securebusiness.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserService userService;
    private final EmailService emailService;
    private final AuthenticationTokenRepository authenticationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${client-port}")
    private String port;

    @Override
    public void resetPassword(String email, HttpServletRequest request) {
        User user = userService.getUser(email);
        String token = generateToken();
        userService.createAuthenticationToken(user, token);
        String appUrl = "http://" + request.getServerName() + ":" + port + request.getContextPath();
        emailService.sendResetEmail(appUrl, token, user);
    }

    @Override
    public void validateResetPassword(String token, String email) {
        AuthenticationToken authenticationToken = authenticationTokenRepository
                .findByUser(userService.getUser(email))
                .orElseThrow(() -> new ApiException("token is invalid"));
        if (!isTokenValid(authenticationToken, email)) return;
        log.info("Reset Password Token is Valid");
    }


    @Override
    public void enableUserAccount(String token, String email) {
        User user = userService.getUser(email);
        if (user.isEnabled())
            return;
        AuthenticationToken authenticationToken = authenticationTokenRepository
                .findByUser(userService.getUser(email))
                .orElseThrow(() -> new ApiException("token is invalid"));
        if (!isTokenValid(authenticationToken, email)) return;
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User is enabled");
    }

    @Override
    public void updatePassword(PasswordDto passwordDto) {
        AuthenticationToken authenticationToken =
                authenticationTokenRepository.findByToken(passwordDto.getToken())
                        .orElseThrow(() -> new ApiException("Token is invalid"));
        if (isPasswordValid(passwordDto.getNewPassword(), passwordDto.getConfirmationPassword())) {
            User user = userService.getUser(authenticationToken.getUser().getEmail());
            user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
            userRepository.save(user);
            log.info("{} has updated his password ", user.getEmail());
        }
    }

    private boolean isPasswordValid(String newPassword, String confirmationPassword) {
        if (!newPassword.equals(confirmationPassword))
            throw new ApiException("new password and confirmation password has to be the same");
        return true;
    }

    private boolean isTokenValid(AuthenticationToken token, String email) {
        if (token.getExpirationDate().isBefore(LocalDateTime.now()))
            throw new ApiException("reset password link has expired");
        if (!token.getUser().getEmail().equals(email))
            throw new ApiException("Authentication Token is Invalid ");
        return true;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
