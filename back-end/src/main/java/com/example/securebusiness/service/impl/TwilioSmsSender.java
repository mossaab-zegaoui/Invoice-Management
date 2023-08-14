package com.example.securebusiness.service.impl;

import com.example.securebusiness.config.TwilioConfig;
import com.example.securebusiness.model.PhoneVerificationCode;
import com.example.securebusiness.model.SmsRequest;
import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.repository.PhoneVerificationCodeRepository;
import com.example.securebusiness.repository.UserRepository;
import com.example.securebusiness.service.SmsSender;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("twilio")
@AllArgsConstructor
@Slf4j
public class TwilioSmsSender implements SmsSender {
    private final TwilioConfig twilioConfig;
    private final PhoneVerificationCodeRepository phoneVerificationCodeRepository;

    @Override
    public void sendSms(SmsRequest smsRequest) {
        if (!isValidPhoneNumber(smsRequest.getPhoneNumber()))
            throw new ApiException("phone number: " + smsRequest.getPhoneNumber() + " is invalid");
        PhoneVerificationCode phoneVerificationCode = phoneVerificationCodeRepository
                .findByPhoneNumber(smsRequest.getPhoneNumber())
                .orElseGet(PhoneVerificationCode::new);
        String otpMessage = generateOTP();
        phoneVerificationCode.setPhoneNumber(smsRequest.getPhoneNumber());
        phoneVerificationCode.setVerificationCode(otpMessage);
        phoneVerificationCode.setExpirationDate(LocalDateTime.now().plusMinutes(30));

        sendVerificationCode(phoneVerificationCode);

    }

    @Override
    public void validateOtp(SmsRequest smsRequest) {
        PhoneVerificationCode code = phoneVerificationCodeRepository
                .findByPhoneNumber(smsRequest.getPhoneNumber())
                .orElseThrow(() -> new ApiException("Code is invalid please try again"));
        if (!isOtpValid(code, smsRequest.getOTP())) {
            throw new ApiException("Code is invalid please try again");
        }
        phoneVerificationCodeRepository.delete(code);
    }

    private void sendVerificationCode(PhoneVerificationCode phoneVerificationCode) {
        PhoneNumber to = new PhoneNumber(phoneVerificationCode.getPhoneNumber());
        PhoneNumber from = new PhoneNumber(twilioConfig.getTrialNumber());
        saveVerificationCode(phoneVerificationCode);
        MessageCreator creator = Message.creator(to, from, phoneVerificationCode.getVerificationCode());
//        creator.create();
        log.info("Sending verification code {} to phone number: {}", phoneVerificationCode.getVerificationCode(), phoneVerificationCode.getPhoneNumber());

    }

    private void saveVerificationCode(PhoneVerificationCode code) {
        phoneVerificationCodeRepository.save(code);
    }

    private String generateOTP() {
        return new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
    }

    private boolean isOtpValid(PhoneVerificationCode code, String otp) {
        return code.getVerificationCode().equals(otp) && code.getExpirationDate().isAfter(LocalDateTime.now());
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
