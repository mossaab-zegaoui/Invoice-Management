package com.example.securebusiness.service;

import com.example.securebusiness.model.User;

public interface EmailService {
    void sendResetEmail(String url, String token, User user);

    void sendVerificationEmail(String recipientEmail, String confirmationUrl);

}
