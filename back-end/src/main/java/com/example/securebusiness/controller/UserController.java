package com.example.securebusiness.controller;

import com.example.securebusiness.form.PasswordDto;
import com.example.securebusiness.model.SmsRequest;
import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.AccountSettingsForm;
import com.example.securebusiness.form.LoginForm;
import com.example.securebusiness.form.UpdatePasswordForm;
import com.example.securebusiness.model.HttpResponse;
import com.example.securebusiness.model.User;
import com.example.securebusiness.model.UserPrincipal;
import com.example.securebusiness.provider.TokenProvider;
import com.example.securebusiness.service.EmailService;
import com.example.securebusiness.service.PasswordResetService;
import com.example.securebusiness.service.RoleService;
import com.example.securebusiness.service.UserService;
import com.example.securebusiness.service.impl.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
import java.util.UUID;

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
    private final SmsService smsService;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final PasswordResetService passwordResetService;

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
        return userDTO.isUsingMfa() ? sendVerificationCode(userDTO) : sendResponse(userDTO);

    }


    @GetMapping("profile")
    public HttpResponse profile(@AuthenticationPrincipal User user) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userService.getUserDtoByEmail(user.getEmail()),
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


    @PostMapping("sms/verify")
    public HttpResponse verifyCode(@RequestBody @Valid SmsRequest smsRequest) {
        smsService.validateOtp(smsRequest);
        return sendResponse(userService.getUserDtoByPhoneNumber(smsRequest.getPhoneNumber()));
    }

    @GetMapping("reset-password")
    public HttpResponse resetPassword(HttpServletRequest request, @RequestParam("email") @NotBlank String email) {
        passwordResetService.resetPassword(email, request);
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("verification link has been sent to your email")
                .status(OK)
                .build();
    }
    @GetMapping("reset-password/validate")
    public HttpResponse validateResetPassword(@RequestParam @NotBlank String token) {
        passwordResetService.validateResetPassword(token);
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("reset password token is valid")
                .status(OK)
                .build();
    }

    @PostMapping("update-password")
    public HttpResponse updatePassword(@RequestBody @Valid PasswordDto passwordDto) {
        passwordResetService.updatePassword(passwordDto);
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .message("password has been updated successfully")
                .status(OK)
                .build();
    }
    private HttpResponse sendVerificationCode(UserDTO userDTO) {
        smsService.sendSms(new SmsRequest(userDTO.getPhone(), null));
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userDTO))
                .message("verification code has been sent your phone")
                .status(OK)
                .build();
    }

    private UserDTO authenticate(String email, String password) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            return getLoggedInUser(authenticate);
        } catch (AuthenticationException exception) {
            throw new ApiException("Wrong credentials");
        }
    }

    private HttpResponse sendResponse(UserDTO userDTO) {
        return HttpResponse.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(of("user", userDTO,
                        "access_token", tokenProvider.createAccessToken(getUserPrincipal(userDTO)),
                        "refresh_token", tokenProvider.createRefreshToken(getUserPrincipal(userDTO))))
                .message("Login successful")
                .status(OK)
                .build();
    }

    public static UserDTO getLoggedInUser(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUser();
    }

    private UserPrincipal getUserPrincipal(UserDTO userDTO) {
        return new UserPrincipal(userService.getUser(userDTO.getEmail()));
    }
}
