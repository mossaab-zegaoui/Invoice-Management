package com.example.securebusiness.form;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePasswordForm {
    @NotBlank(message = "currentPassword can not empty")
    private String currentPassword;
    @NotBlank(message = "newPassword can not empty")
    private String newPassword;
    @NotBlank(message = "confirmNewPassword can not empty")
    private String confirmNewPassword;
}
