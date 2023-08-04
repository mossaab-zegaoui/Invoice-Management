package com.example.securebusiness.config;

import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TwilioInitializer {
    private final TwilioConfig twilioConfig;

    public TwilioInitializer(TwilioConfig twilioConfig) {

        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
        log.info("initializing ... with account sid: {}", twilioConfig.getAccountSid());
    }

}
