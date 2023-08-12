package com.example.securebusiness.service.impl;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.AccountSettingsForm;
import com.example.securebusiness.form.UpdatePasswordForm;
import com.example.securebusiness.model.AuthenticationToken;
import com.example.securebusiness.model.Role;
import com.example.securebusiness.model.User;
import com.example.securebusiness.repository.AuthenticationTokenRepository;
import com.example.securebusiness.repository.RoleRepository;
import com.example.securebusiness.repository.UserRepository;
import com.example.securebusiness.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import static com.example.securebusiness.enums.RoleType.ROLE_USER;
import static com.example.securebusiness.mapper.UserDTOMapper.fromUser;
import static com.example.securebusiness.utils.FileStorageUtils.FOLDER_PATH;
import static com.example.securebusiness.utils.SecurityConstant.RESET_PASSWORD_TOKEN_EXPIRATION_TIME_DAY;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationTokenRepository authenticationTokenRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDTO createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new ApiException("email is already taken");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setNotLocked(true);
        user.setImageUrl("image_d.png");
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        addRoleToUser(ROLE_USER.name(), user.getEmail());
        return fromUser(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User with id" + userId + "not found"));
    }

    @Override
    public UserDTO getUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User with id" + userId + "not found"));
        return fromUser(user);
    }

    @Override
    public UserDTO getUserDtoByEmail(String email) {
        return fromUser(userRepository.findByEmail(email).get());
    }

    @Override
    public UserDTO getUserDtoByPhoneNumber(String phoneNumber) {
        return fromUser(userRepository.findByPhone(phoneNumber).get());
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("email: " + email + " not found"));
    }

    @Override
    public UserDTO updateUserDetails(UserDTO userDto, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("user " + id + "not found"));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setTitle(userDto.getTitle());
        user.setBio(userDto.getBio());
        return fromUser(userRepository.save(user));
    }

    @Override
    public UserDTO updateUserPassword(User user, UpdatePasswordForm updatePasswordForm) {
        if (!updatePasswordForm.getNewPassword().equals(updatePasswordForm.getConfirmNewPassword()))
            throw new ApiException("new password and verification password has to be the same");
        if (!checkIfValidOldPassword(updatePasswordForm.getCurrentPassword(), user.getPassword()))
            throw new ApiException("Wrong Current Password");
        user.setPassword(passwordEncoder.encode(updatePasswordForm.getNewPassword()));
        return fromUser(userRepository.save(user));
    }

    @Override
    public UserDTO updateUserRole(User user, String roleName) {
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);
        log.info("{} update his role to {}", user.getEmail(), user.getRole().getName());
        return fromUser(userRepository.save(user));
    }

    @Override
    public UserDTO updateUserAccountSettings(User user, AccountSettingsForm accountSettingsForm) {
        user.setEnabled(accountSettingsForm.isEnabled());
        user.setNotLocked(accountSettingsForm.isNotLocked());
        return fromUser(userRepository.save(user));
    }

    @Override
    public UserDTO toggleMfa(User user) {
        if (user.getPhone().isBlank())
            throw new ApiException("you have to add your phone number before enabling MFA");
        user.setUsingMfa(!user.isUsingMfa());
        return fromUser(userRepository.save(user));
    }

    @Override
    public UserDTO updateProfileImage(User user, MultipartFile file) {
        String filePath = FOLDER_PATH + file.getOriginalFilename();
        user.setImageUrl(file.getOriginalFilename());
        try {
            file.transferTo(new File(filePath));
        } catch (IOException exception) {
            throw new ApiException("folder couldn't be uploaded" + exception.getMessage());
        }
        return fromUser(userRepository.save(user));
    }

    @Override
    public byte[] downloadImageProfile(User user, String imageUrl) throws IOException {
        String filePath = FOLDER_PATH + imageUrl;
        byte[] image = Files.readAllBytes(new File(filePath).toPath());
        return image;
    }

    @Override
    public void createAuthenticationToken(User user, String token) {
        AuthenticationToken authenticationToken = authenticationTokenRepository
                .findByUser(user)
                .orElseGet(AuthenticationToken::new);
        authenticationToken.setUser(user);
        authenticationToken.setToken(token);
        authenticationToken.setExpirationDate(LocalDateTime.now().plusDays(RESET_PASSWORD_TOKEN_EXPIRATION_TIME_DAY));

        authenticationTokenRepository.save(authenticationToken);
    }


    private boolean checkIfValidOldPassword(final String password, final String currentPassword) {
        return passwordEncoder.matches(password, currentPassword);
    }

    private void addRoleToUser(String roleName, String email) {
        User user = userRepository.findByEmail(email).get();
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);
        log.info("assign {} to {}", role.getName(), user.getEmail());
        userRepository.save(user);
    }
}
