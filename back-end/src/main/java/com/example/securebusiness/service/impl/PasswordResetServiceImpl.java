package com.example.securebusiness.service.impl;

import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.PasswordDto;
import com.example.securebusiness.model.PasswordResetToken;
import com.example.securebusiness.model.User;
import com.example.securebusiness.repository.PasswordResetTokenRepository;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${client-port}")
    private String port;

    @Override
    public void resetPassword(String email, HttpServletRequest request) {
        User user = userService.getUser(email);
        String token = generateToken();
        userService.createPasswordResetToken(user, token);
        String appUrl = "http://" + request.getServerName() + ":" + port + request.getContextPath();
        emailService.sendResetEmail(appUrl, token, user);
    }

    @Override
    public void validateResetPassword(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ApiException("token is invalid"));
        if (passwordResetToken.getExpirationDate().isBefore(LocalDateTime.now()))
            throw new ApiException("reset password link has expired");
        log.info("reset password Token is valid");
    }

    @Override
    public void updatePassword(PasswordDto passwordDto) {
        PasswordResetToken passwordResetToken =
                passwordResetTokenRepository.findByToken(passwordDto.getToken())
                        .orElseThrow(() -> new ApiException("Token is invalid"));
        if (isPasswordValid(passwordDto.getNewPassword(), passwordDto.getConfirmationPassword())) {
            User user = userService.getUser(passwordResetToken.getUser().getEmail());
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


    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
