package com.example.securebusiness.filter;

import com.example.securebusiness.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.example.securebusiness.utils.SecurityConstant.PUBLIC_URLS;
import static com.example.securebusiness.utils.SecurityConstant.TOKEN_HEADER;
import static org.apache.logging.log4j.util.Strings.EMPTY;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.example.securebusiness.utils.ExceptionUtils.processError;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    protected static final String EMAIL_KEY = "email";
    protected static final String TOKEN_KEY = "token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String token = getToken(request);
            Long userId = getUserId(request);
            if (tokenProvider.isTokenValid(userId, token)) {
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(userId, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Token is valid");
            } else {
                log.info("Token is NOT valid");
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            processError(request, response, exception);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) == null ||
                !request.getHeader(AUTHORIZATION).startsWith(TOKEN_HEADER) ||
                request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD) ||
                asList(PUBLIC_URLS).contains(request.getRequestURI());
    }

    private Long getUserId(HttpServletRequest request) {
        return tokenProvider.getSubject(getToken(request), request);
    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_HEADER))
                .map(token -> token.replace(TOKEN_HEADER, EMPTY)).get();
    }
}
