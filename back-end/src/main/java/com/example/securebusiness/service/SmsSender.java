package com.example.securebusiness.service;

import com.example.securebusiness.model.SmsRequest;

public interface SmsSender {
    void sendSms(SmsRequest smsRequest);

    void validateOtp(SmsRequest smsRequest);
}
