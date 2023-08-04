package com.example.securebusiness.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequest {
    @NotBlank(message = "phone number can not be empty")
    private String phoneNumber;
    @NotBlank(message = " OTP can not be empty")
    private String OTP;

    public SmsRequest(String phoneNumber, String OTP) {
        this.phoneNumber = phoneNumber;
        this.OTP = OTP;
    }

}
