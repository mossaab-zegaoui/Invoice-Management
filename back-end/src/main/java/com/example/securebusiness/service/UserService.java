package com.example.securebusiness.service;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.form.AccountSettingsForm;
import com.example.securebusiness.form.UpdatePasswordForm;
import com.example.securebusiness.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    UserDTO createUser(User user);

    User getUserById(Long userId);

    UserDTO getUserDtoByEmail(String email);
    UserDTO getUserDtoByPhoneNumber(String phoneNumber);

    User getUser(String email);

    UserDTO updateUserDetails(UserDTO userDto, Long id);

    UserDTO updateUserPassword(User user, UpdatePasswordForm updatePasswordForm);

    UserDTO updateUserRole(User user, String roleName);

    UserDTO updateUserAccountSettings(User user, AccountSettingsForm accountSettingsForm);

    UserDTO toggleMfa(User user);

    UserDTO updateProfileImage(User user, MultipartFile multipartFile) throws IOException;

    byte[] downloadImageProfile(User user, String imageUrl) throws IOException;

    void createPasswordResetToken(User user, String token);
}
