package com.example.securebusiness.service.impl;

import com.example.securebusiness.model.User;
import com.example.securebusiness.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Value("${ui.app.url}")
    private String appUrl;

    @Async
    @Override
    public void sendResetEmail(String token, User user) {
        String resetURL = appUrl + "/reset-password/validate?token=" + token + "&email=" + user.getEmail();
        SimpleMailMessage email = new SimpleMailMessage();
        String subject = "Reset Password";
        email.setFrom(fromEmail);
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText("To Reset your password " + "\r\n" + "Please, click  the link below" + "\r\n" + resetURL);
        mailSender.send(email);
        log.info("reset password url: {}", resetURL);
    }

    @Async
    @Override
    public void sendVerificationEmail(String recipientEmail, String confirmationUrl) {
        SimpleMailMessage email = new SimpleMailMessage();
        String subject = "Registration Confirmation";
        email.setFrom(fromEmail);
        email.setTo(recipientEmail);
        email.setSubject(subject);
        email.setText("Thank you for registering with us" + "\r\n" + "Please, click the link below to complete your registration" +
                "\r\n" + confirmationUrl);
        mailSender.send(email);
        log.info("verification email\n\n {}", confirmationUrl);
    }
}
