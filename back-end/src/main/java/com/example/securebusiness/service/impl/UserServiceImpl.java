package com.example.securebusiness.service.impl;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.UpdatePasswordForm;
import com.example.securebusiness.mapper.UserDTOMapper;
import com.example.securebusiness.model.Role;
import com.example.securebusiness.model.User;
import com.example.securebusiness.repository.RoleRepository;
import com.example.securebusiness.repository.UserRepository;
import com.example.securebusiness.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.securebusiness.enums.RoleType.ROLE_USER;
import static com.example.securebusiness.mapper.UserDTOMapper.fromUser;
import static com.example.securebusiness.mapper.UserDTOMapper.toUser;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDTO createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new ApiException("email is already taken");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setNotLocked(true);
        user.setEnabled(true);
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
    public UserDTO getUserDto(String email) {
        return fromUser(userRepository.findByEmail(email).get());
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("user with email: " + email + " not found"));
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

    private boolean checkIfValidOldPassword(final String password, final String currentPassword) {
        return passwordEncoder.matches(password, currentPassword);
    }

    private void addRoleToUser(String roleName, String email) {
        User user = userRepository.findByEmail(email).get();
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);
        userRepository.save(user);
    }
}
