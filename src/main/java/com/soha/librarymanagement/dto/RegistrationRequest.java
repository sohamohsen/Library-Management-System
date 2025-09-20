package com.soha.librarymanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Full name must not be blank")
    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String fullName;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email format is invalid")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    private String email;

    @Size(max = 40, message = "Phone number must not exceed 40 characters")
    private String phone;

    @NotNull(message = "Role ID is required")
    private Integer roleId;

    private boolean enabled = true;

    @Size(max = 64, message = "Username must not exceed 64 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters long")
    private String password;

    private Integer createdBy;
}