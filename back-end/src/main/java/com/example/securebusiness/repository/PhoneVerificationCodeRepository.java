package com.example.securebusiness.repository;

import com.example.securebusiness.model.PhoneVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneVerificationCodeRepository extends JpaRepository<PhoneVerificationCode, String> {
    Optional<PhoneVerificationCode> findByPhoneNumber(String phoneNumber);

}
