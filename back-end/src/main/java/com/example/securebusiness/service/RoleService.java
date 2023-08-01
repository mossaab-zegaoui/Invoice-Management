package com.example.securebusiness.service;

import com.example.securebusiness.model.Role;

import java.util.List;

public interface RoleService {
    Role getRoleByUserId(Long id);

    List<Role> getRoles();
}
