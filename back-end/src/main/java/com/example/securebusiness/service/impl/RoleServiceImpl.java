package com.example.securebusiness.service.impl;

import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.model.Role;
import com.example.securebusiness.model.User;
import com.example.securebusiness.repository.UserRepository;
import com.example.securebusiness.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final UserRepository userRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("user with id: " + id + " not found"));
        return user.getRole();
    }
}
