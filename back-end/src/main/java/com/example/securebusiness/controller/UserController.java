package com.example.securebusiness.controller;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.LoginForm;
import com.example.securebusiness.model.HttpResponse;
import com.example.securebusiness.model.User;
import com.example.securebusiness.model.UserPrincipal;
import com.example.securebusiness.provider.TokenProvider;
import com.example.securebusiness.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.example.securebusiness.mapper.UserDTOMapper.toUser;
import static java.time.LocalTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    @PostMapping("register")
    public HttpResponse register(@RequestBody @Valid User user) {
        UserDTO userDTO = userService.createUser(user);
        return HttpResponse.builder()
                .timeStamp(now().toString())
                .data(of("user", userDTO))
                .message("user created")
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("login")
    public HttpResponse login(@RequestBody @Valid LoginForm loginForm) {
        UserDTO userDTO = authenticate(loginForm.getEmail(), loginForm.getPassword());
        return
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(of("user", userDTO, "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO))
                                , "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                        .message("Login successful")
                        .status(OK)
                        .build();
    }


    private UserDTO authenticate(String email, String password) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDTO loggedInUser = getLoggedInUser(authenticate);
            return loggedInUser;
        } catch (AuthenticationException exception) {
            throw new ApiException("Wrong credentials");
        }
    }

    public static UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

    private UserPrincipal getUserPrincipal(UserDTO userDTO) {
        return new UserPrincipal(userService.getUser(userDTO.getEmail()));
    }
}
