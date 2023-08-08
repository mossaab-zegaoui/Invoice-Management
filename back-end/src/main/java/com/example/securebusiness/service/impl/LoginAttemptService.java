package com.example.securebusiness.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    public static final int MAX_ATTEMPT = 10;
    private LoadingCache<String, Integer,String> attemptsCache;
    private HttpServletRequest request;
    public LoginAttemptService() {
        super();
    }
    public void loginFailed(String remoteAddr) {


    }
}
