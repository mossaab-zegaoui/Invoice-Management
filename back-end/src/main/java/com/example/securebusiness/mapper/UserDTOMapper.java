package com.example.securebusiness.mapper;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.model.Role;
import com.example.securebusiness.model.User;
import com.example.securebusiness.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
@AllArgsConstructor
public class UserDTOMapper {
    private final RoleRepository roleRepository;
    public static UserDTO fromUser(User user) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRoleName(user.getRole().getName());
        userDTO.setPermission(user.getRole().getPermission());
        return userDTO;
    }

    public static UserDTO fromUser(User user, Role role) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setRoleName(role.getName());
        userDTO.setPermission(userDTO.getPermission());
        return userDTO;
    }

    public static User toUser(UserDTO userDTO, Role role) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setRole(role);
        return user;
    }
}
