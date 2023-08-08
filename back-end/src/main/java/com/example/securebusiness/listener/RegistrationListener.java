package com.example.securebusiness.listener;

import com.example.securebusiness.event.onRegistrationCompleteEvent;
import com.example.securebusiness.model.User;
import com.example.securebusiness.service.EmailService;
import com.example.securebusiness.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationListener implements ApplicationListener<onRegistrationCompleteEvent> {
    private final EmailService emailService;
    private final UserService userService;

    @Override
    public void onApplicationEvent(onRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(onRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createPasswordResetToken(user, token);
// Send a verification email
        String recipientEmail = user.getEmail();
        String confirmationUrl = event.getAppUrl() + "/registrationConfirm?token=" + token;
        log.info("confirmation url : {}", confirmationUrl);
        emailService.sendVerificationEmail(recipientEmail, confirmationUrl);
    }


}
