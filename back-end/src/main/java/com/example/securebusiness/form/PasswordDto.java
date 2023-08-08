package com.example.securebusiness.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDto {
    @NotBlank(message = "newPassword can not be empty")
    private String newPassword;
    @NotBlank(message = "confirmationPassword can not be empty")
    private String confirmationPassword;
    @NotBlank(message = "token can not be empty")
    private String token;
}
