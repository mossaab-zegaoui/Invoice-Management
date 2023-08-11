package com.example.securebusiness.utils;

public class SecurityConstant {
    public static final String SECRET_KEY = "pTmvJanGDA4Go4V4xjmKpWLrtCUmOzO0";
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 3_600_000L; // 1 day
    public static final long REFRESH_TOKEN_EXPIRATION_TIME = 38_880_000_000L; // 30 days
    public static final int RESET_PASSWORD_TOKEN_EXPIRATION_TIME_DAY = 1 ; // 1 day

    public static final String TOKEN_HEADER = "Bearer ";
    public static final String FORBIDDEN_MESSAGE = "you need to login to access this page";
    public static final String ACCESS_DENIED_MESSAGE = "you don't have permission to access this page";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String Issuer = "Mossaab Zegaoui";
    public static final String AUTHORITIES = "authorities";
    public static final String ApplicationAudience = "users";
    public static final String[] PUBLIC_URLS = {
            "/api/v1/users/refresh-token",
            "/api/v1/users/register",
            "/api/v1/users/login",
            "/api/v1/users/sms/verify",
            "/api/v1/users/reset-password",
            "/api/v1/users/reset-password/validate",
            "/api/v1/users/update-password"

    };

}