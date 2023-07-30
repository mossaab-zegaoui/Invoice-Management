package com.example.securebusiness.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    @NotBlank(message = "email can not be empty")
    private String email;
    @NotBlank(message = "password can not be empty")
    private String password;
}
