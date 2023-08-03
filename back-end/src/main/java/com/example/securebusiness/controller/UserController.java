package com.example.securebusiness.controller;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.AccountSettingsForm;
import com.example.securebusiness.form.LoginForm;
import com.example.securebusiness.form.UpdatePasswordForm;
import com.example.securebusiness.model.HttpResponse;
import com.example.securebusiness.model.User;
import com.example.securebusiness.model.UserPrincipal;
import com.example.securebusiness.provider.TokenProvider;
import com.example.securebusiness.service.RoleService;
import com.example.securebusiness.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.time.LocalTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
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
                        .data(of("user", userDTO,
                                "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                                "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                        .message("Login successful")
                        .status(OK)
                        .build();
    }

    @GetMapping("profile")
    public HttpResponse profile(@AuthenticationPrincipal User user) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.getUserDto(user.getEmail()),
                        "roles", roleService.getRoles()
                ))
                .message("profile retrieved")
                .status(OK)
                .build();
    }

    @PutMapping("{id}")
    public HttpResponse updateUserDetails(@RequestBody @Valid UserDTO userDto, @PathVariable Long id) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.updateUserDetails(userDto, id),
                        "roles", roleService.getRoles()
                ))
                .message("user details updated")
                .status(OK)
                .build();
    }

    @PutMapping("updatePassword")
    public HttpResponse updateUserPassword(@AuthenticationPrincipal User user, @RequestBody @Valid UpdatePasswordForm updatePasswordForm) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.updateUserPassword(user, updatePasswordForm),
                        "roles", roleService.getRoles()
                ))
                .message("update user password")
                .status(OK)
                .build();
    }

    @PutMapping("updateUserRole")
    public HttpResponse updateUserRole(@AuthenticationPrincipal User user, @RequestBody String roleName) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.updateUserRole(user, roleName),
                        "roles", roleService.getRoles()
                ))
                .message("updated user role")
                .build();
    }

    @PutMapping("updateUserAccountSettings")
    public HttpResponse updateAccountSettings(@AuthenticationPrincipal User user, @RequestBody AccountSettingsForm accountSettingsForm) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.updateUserAccountSettings(user, accountSettingsForm),
                        "roles", roleService.getRoles()
                ))
                .message("updated Account Settings")
                .build();
    }

    @PutMapping("toggleMfa")
    public HttpResponse toggleMfa(@AuthenticationPrincipal User user) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.toggleMfa(user),
                        "roles", roleService.getRoles()
                ))
                .message("updated Mfa")
                .build();
    }

    @PutMapping("uploadImage")
    public HttpResponse uploadProfileImage(@AuthenticationPrincipal User user, @RequestParam("image") MultipartFile multipartFile) throws IOException {

        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.updateProfileImage(user, multipartFile),
                        "roles", roleService.getRoles()
                ))
                .message("upload profile image")
                .build();
    }

    @GetMapping(value = "downloadImage/{imageUrl}", produces = IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> downloadProfileImage(@AuthenticationPrincipal User user, @PathVariable String imageUrl) throws IOException {

        return ResponseEntity.status(OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(userService.downloadImageProfile(user, imageUrl));
    }

    private UserDTO authenticate(String email, String password) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            return getLoggedInUser(authenticate);
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
