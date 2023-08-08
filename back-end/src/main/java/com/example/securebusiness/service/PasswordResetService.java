package com.example.securebusiness.service;

import com.example.securebusiness.form.PasswordDto;
import jakarta.servlet.http.HttpServletRequest;

public interface PasswordResetService {
    void resetPassword(String email, HttpServletRequest request);

    void validateResetPassword(String token);

    void updatePassword(PasswordDto passwordDto);
}
