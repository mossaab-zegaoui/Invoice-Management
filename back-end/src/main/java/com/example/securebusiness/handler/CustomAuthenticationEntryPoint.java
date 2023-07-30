package com.example.securebusiness.handler;

import com.example.securebusiness.model.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
                .timeStamp(now().toString())
                .reason("You need to log in to access this resource")
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, httpResponse);
        out.flush();
    }
}
