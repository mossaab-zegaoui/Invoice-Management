package com.example.securebusiness.service.impl;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.exception.ApiException;
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

import static com.example.securebusiness.enums.RoleType.ROLE_USER;

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
        return UserDTOMapper.fromUser(user);
    }

    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User with id" + userId + "not found"));
    }

    @Override
    public UserDTO getUserDto(String email) {
        return UserDTOMapper.fromUser(userRepository.findByEmail(email).get());
    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("user with email: " + email + " not found"));
    }

    private void addRoleToUser(String roleName, String email) {
        User user = userRepository.findByEmail(email).get();
        Role role = roleRepository.findByName(roleName);
        user.setRole(role);
        userRepository.save(user);
    }
}
