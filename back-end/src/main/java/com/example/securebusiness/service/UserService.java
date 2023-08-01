package com.example.securebusiness.service;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.form.UpdatePasswordForm;
import com.example.securebusiness.model.User;

public interface UserService {
    UserDTO createUser(User user);

    User getUserById(Long userId);

    UserDTO getUserDto(String email);
    User getUser(String email);

    UserDTO updateUserDetails(UserDTO userDto, Long id);

    UserDTO updateUserPassword(User user, UpdatePasswordForm updatePasswordForm);

}
