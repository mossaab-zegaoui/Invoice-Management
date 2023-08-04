package com.example.securebusiness.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class TwilioConfig {
    @Value("${twilio_account_sid}")
    private String accountSid;
    @Value("${twilio_auth_token}")
    private String authToken;
    @Value("${twilio_phone_number}")
    private String trialNumber;

}
