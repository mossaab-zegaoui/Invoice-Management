package com.example.securebusiness.listener;

import com.example.securebusiness.event.onRegistrationCompleteEvent;
import com.example.securebusiness.model.User;
import com.example.securebusiness.service.EmailService;
import com.example.securebusiness.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationListener implements ApplicationListener<onRegistrationCompleteEvent> {
    private final EmailService emailService;
    private final UserService userService;
    @Value("${ui.app.url}")
    private String appUrl;

    @Override
    public void onApplicationEvent(onRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(onRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createAuthenticationToken(user, token);
// Send a verification email
        String recipientEmail = user.getEmail();
        String confirmationUrl =
                appUrl + "/register/validate?token=" + token + "&email=" + user.getEmail();
        emailService.sendVerificationEmail(recipientEmail, confirmationUrl);
    }


}
