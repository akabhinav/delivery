package com.quickserve.dto;

import com.quickserve.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating/updating users
 */
public record UserRequest(
        @NotBlank(message = "Name is required") String name,
        @Email(message = "Valid email is required") String email,
        @NotBlank(message = "Phone is required") String phone,
        @NotBlank(message = "Password is required") String password,
        @NotNull(message = "Role is required") UserRole role,
        String address,
        Double latitude,
        Double longitude
) {}
