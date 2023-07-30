package com.example.securebusiness.mapper;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.model.Role;
import com.example.securebusiness.model.User;
import org.springframework.beans.BeanUtils;

public class UserDTOMapper {
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

    public static User toUser(UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return user;
    }
}
