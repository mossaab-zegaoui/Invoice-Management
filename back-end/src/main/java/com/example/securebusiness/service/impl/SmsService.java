package com.example.securebusiness.service.impl;

import com.example.securebusiness.model.SmsRequest;
import com.example.securebusiness.service.SmsSender;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    public final SmsSender smsSender;

    public SmsService(@Qualifier("twilio") TwilioSmsSender smsSender) {
        this.smsSender = smsSender;
    }
    public void sendSms(SmsRequest smsRequest){
        smsSender.sendSms(smsRequest);
    }

    public void validateOtp(SmsRequest smsRequest) {
        smsSender.validateOtp(smsRequest);
    }
}
