package com.example.securebusiness.service.impl;

import com.example.securebusiness.dto.UserDTO;
import com.example.securebusiness.exception.ApiException;
import com.example.securebusiness.form.UpdatePasswordForm;
import com.example.securebusiness.mapper.UserDTOMapper;
import com.example.securebusiness.model.Role;
import com.example.securebusiness.model.User;
import com.example.securebusiness.repository.RoleRepository;
import com.example.securebusiness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDTOMapper userDTOMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User USER_1;
    private UserDTO USER_DTO;
    private Role ROLE_USER;
    private Role ROLE_ADMIN;

    @BeforeEach
    void setupService() {
        ROLE_USER = new Role(1L, "ROLE_USER", "READ:USER, READ:CUSTOMER");
        ROLE_ADMIN = new Role(1L, "ROLE_ADMIN", "READ:USER, READ:CUSTOMER, UPDATE:CUSTOMER");
        USER_1 = User.builder()
                .id(1L)
                .email("mossaab@gmail.com")
                .password("123")
                .firstName("mosssaab")
                .lastName("zegaoui")
                .imageUrl("image_1.png")
                .role(ROLE_USER)
                .build();
        USER_DTO = UserDTO.builder()
                .id(1L)
                .email("mossaab@gmail.com")
                .firstName("mosssaab")
                .lastName("zegaoui")
                .imageUrl("image_1.png")
                .roleName("ROLE_USER")
                .permission("READ:USER, READ:CUSTOMER")
                .build();
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        String email = "mossaab@gmail.com";
        String rawPassword = "123";
        String encodedPassword = "X5GTR6";

        UserDTO expectedUserDto = new UserDTO();
        BeanUtils.copyProperties(USER_1, expectedUserDto);
        expectedUserDto.setPermission(USER_1.getRole().getPermission());
        expectedUserDto.setRoleName(USER_1.getRole().getName());
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.ofNullable(USER_1));
        when(roleRepository.findByName("ROLE_USER")).thenReturn(ROLE_USER);
        when(userRepository.save(USER_1)).thenReturn(USER_1);


        try (MockedStatic<UserDTOMapper> mockedStatic = mockStatic(UserDTOMapper.class)) {
            mockedStatic.when(() -> UserDTOMapper.fromUser(USER_1)).thenReturn(expectedUserDto);
//
            UserDTO acutalUserDto = userService.createUser(USER_1);
//
            assertNotNull(acutalUserDto);
            assertEquals(USER_1.getPassword(), encodedPassword);
            assertTrue(USER_1.isNotLocked());
            assertThat(acutalUserDto)
                    .usingRecursiveComparison()
                    .ignoringFields("createdAt")
                    .isEqualTo(expectedUserDto);
        }
    }

    @Test
    void createUser_shouldThrowApiException() {
        User USER_2 = User.builder()
                .id(2L)
                .email("mossaab@gmail.com")
                .password("1234")
                .firstName("khalid")
                .lastName("Iraqi")
                .build();
        when(userRepository.existsByEmail("mossaab@gmail.com")).thenReturn(true);
//
        ApiException apiException = assertThrows(ApiException.class, () -> userService.createUser(USER_2));
        String expectedMessage = "email is already taken";
        String actualMessage = apiException.getMessage();
//
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getUserById_shouldReturnUser() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(USER_1));
//
        User savedUser = userService.getUserById(id);
//
        assertNotNull(savedUser);
        assertEquals(savedUser, USER_1);
    }

    @Test
    void getUserById_shouldThrowApiException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
        ApiException apiException = assertThrows(ApiException.class, () -> userService.getUserById(1L));
        String expectedMessage = "User with id" + 1L + "not found";
        String actualMessage = apiException.getMessage();
//
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void getUserDtoById_shouldReturnUserDto() {
//
        Long id = 1L;
        UserDTO expectedUserDto = new UserDTO();
        BeanUtils.copyProperties(USER_1, expectedUserDto);
        expectedUserDto.setRoleName(USER_1.getRole().getName());
        expectedUserDto.setPermission(USER_1.getRole().getPermission());
        when(userRepository.findById(id)).thenReturn(Optional.of(USER_1));
        try (MockedStatic<UserDTOMapper> mockedStatic = mockStatic(UserDTOMapper.class)) {
            mockedStatic.when(() -> UserDTOMapper.fromUser(USER_1)).thenReturn(expectedUserDto);
//
            UserDTO actualUserDto = userService.getUserDtoById(id);
//
            assertNotNull(actualUserDto);
            assertThat(actualUserDto).usingRecursiveComparison()
                    .isEqualTo(expectedUserDto);
        }
    }

    @Test
    void getUserDtoByEmail_shouldReturnUserDto() {
        String email = "mossaab@gmail.com";
        UserDTO expectedUserDto = new UserDTO();
        BeanUtils.copyProperties(USER_1, expectedUserDto);
        expectedUserDto.setRoleName(USER_1.getRole().getName());
        expectedUserDto.setPermission(USER_1.getRole().getPermission());
        try (MockedStatic<UserDTOMapper> mockedStatic = mockStatic(UserDTOMapper.class)) {
            mockedStatic.when(() -> UserDTOMapper.fromUser(USER_1)).thenReturn(expectedUserDto);
            when(userRepository.findByEmail(email)).thenReturn(Optional.of(USER_1));
//
            UserDTO actualUserDto = userService.getUserDtoByEmail(email);
//
            assertNotNull(actualUserDto);
            assertThat(actualUserDto).usingRecursiveComparison()
                    .isEqualTo(expectedUserDto);
        }
    }

    @Test
    void getUserDtoByPhoneNumber_shouldReturnUser() {
        USER_1.setPhone("+212712345678");
        UserDTO userDto = new UserDTO();
        userDto.setId(USER_1.getId());
        userDto.setEmail(USER_1.getEmail());
        userDto.setFirstName(USER_1.getFirstName());
        userDto.setLastName(USER_1.getLastName());
        userDto.setImageUrl(USER_1.getImageUrl());
        userDto.setRoleName(USER_1.getRole().getName());
        userDto.setPhone("+212712345678");
        userDto.setPermission(USER_1.getRole().getPermission());
        when(userRepository.findByPhone("+212712345678")).thenReturn(Optional.of(USER_1));
//
        UserDTO actualUserDto = userService.getUserDtoByPhoneNumber("+212712345678");
//
        assertNotNull(actualUserDto);
        assertThat(actualUserDto).usingRecursiveComparison()
                .isEqualTo(userDto);
    }

    @Test
    void getUser_shouldReturnUser() {
        String email = "mosssaab@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(USER_1));
//
        User actualUser = userService.getUser(email);
//
        assertNotNull(actualUser);
        assertEquals(USER_1, actualUser);
    }

    @Test
    void getUser_shouldThrowApiException() {
        String email = "mosssaab@gmail.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
//
        ApiException apiException = assertThrows(ApiException.class, () -> userService.getUser(email));
        String expectedMessage = "email: " + email + " not found";
        String actualMessage = apiException.getMessage();
//
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserDetails_shouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
//
        ApiException apiException = assertThrows(ApiException.class, () -> userService.updateUserDetails(null, 1L));
        String expectedMessage = "user " + 1L + "not found";
        String actualMessage = apiException.getMessage();
//
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserDetails_shouldThrowEntityAlreadyExists() {
        String email = "zegaoui@gmail.com";
        UserDTO userDto = new UserDTO();
        userDto.setEmail(email);
        when(userRepository.findById(1L)).thenReturn(Optional.of(USER_1));
        when(userRepository.existsByEmail(email)).thenReturn(true);
//
        ApiException apiException = assertThrows(ApiException.class, () -> userService.updateUserDetails(userDto, 1L));
        String expectedMessage = "email already exists";
        String actualMessage = apiException.getMessage();
//
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserDetails_shouldReturnUpdatedUserDto() {
        Long id = 1L;
        UserDTO userDto = new UserDTO();
        userDto.setId(id);
        userDto.setFirstName("khalid");
        userDto.setLastName("Alaoui");
        userDto.setEmail("khalid@gmail.com");
        userDto.setPhone("+212696453725");
        userDto.setBio("random text");
        userDto.setPermission("READ:USER, READ:CUSTOMER");
        userDto.setRoleName("ROLE_USER");
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(USER_1));
        when(userRepository.existsByEmail("khalid@gmail.com")).thenReturn(false);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(USER_1);

//
        UserDTO actualUserDto = userService.updateUserDetails(userDto, 1L);
//
        assertThat(actualUserDto)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "imageUrl")
                .isEqualTo(userDto);
    }


    @Test
    void updateUserPassword_shouldThrowApiException_UnMatchPasswords() {
        UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm("X123", "AZ147", "AZ89");
        ApiException apiException = assertThrows(ApiException.class, () -> userService.updateUserPassword(null, updatePasswordForm));
        String expectedMessage = "new password and verification password has to be the same";
        String actualMessage = apiException.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updateUserPassword_shouldThrowApiException_WrongCurrentPassword() {
        UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm("X123", "AZ147", "AZ147");
        User user = new User();
        user.setPassword("RW365");
        when(passwordEncoder.matches(updatePasswordForm.getCurrentPassword(), user.getPassword())).thenReturn(false);
//
        ApiException apiException = assertThrows(ApiException.class,
                () -> userService.updateUserPassword(user, updatePasswordForm));
        String expectedMessage = "Wrong Current Password";
        String actualMessage = apiException.getMessage();
//
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void updatePassword_shouldReturnUpdatedUserDto() {
        UpdatePasswordForm updatePasswordForm = new UpdatePasswordForm("X123", "AZ147", "AZ147");
        String encodedPassword = "WPGTR62FREF5ER";
        UserDTO expectedUserDto = userDTOMapper.fromUser(USER_1);
        when(passwordEncoder.matches(updatePasswordForm.getCurrentPassword(), USER_1.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(updatePasswordForm.getNewPassword())).thenReturn(encodedPassword);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(USER_1);
//
        UserDTO actualUserDto = userService.updateUserPassword(USER_1, updatePasswordForm);
//
        assertEquals(USER_1.getPassword(), encodedPassword);
        assertThat(actualUserDto)
                .usingRecursiveComparison()
                .isEqualTo(expectedUserDto);
    }

    @Test
    void updateUserRole_shouldReturnUserDto() {
        User user = User.builder()
                .id(1L)
                .email("mossaab@gmail.com")
                .password("123")
                .firstName("mosssaab")
                .lastName("zegaoui")
                .imageUrl("image_1.png")
                .role(ROLE_ADMIN)
                .build();
        USER_DTO.setRoleName("ROLE_ADMIN");
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(ROLE_ADMIN);
        when(userRepository.save(any(User.class))).thenReturn(user);
        try (MockedStatic<UserDTOMapper> mockedStatic = mockStatic(UserDTOMapper.class)) {
            mockedStatic.when(() -> UserDTOMapper.fromUser(user)).thenReturn(USER_DTO);
            UserDTO actualUserDto = userService.updateUserRole(USER_1, "ROLE_ADMIN");
            assertThat(actualUserDto).isNotNull();
            assertEquals(actualUserDto.getRoleName(), "ROLE_ADMIN");
        }
    }

    @Test
    void toggleMfa_EnabledMfa() {
        ApiException apiException = assertThrows(ApiException.class, () -> userService.toggleMfa(USER_1));
        String actualMessage = apiException.getMessage();
        String expectedMessage = "you have to add your phone number before enabling MFA";
        assertEquals(expectedMessage, actualMessage);
    }
}