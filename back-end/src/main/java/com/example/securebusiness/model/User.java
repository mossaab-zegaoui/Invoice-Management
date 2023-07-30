package com.example.securebusiness.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "firstName can not be empty")
    private String firstName;
    @NotBlank(message = "lastName can not be empty")
    private String lastName;
    @NotBlank(message = "email can not be empty")
    @Email(message = "invalid email address")
    private String email;
    @NotBlank(message = "password can not be empty")
    private String password;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private String imageUrl;
    private boolean isEnabled;
    private boolean isNotLocked;
    private boolean isUsingMfa;
    private LocalDateTime createdAt;
    @ManyToOne
    private Role role;
}
