package com.incubyte.car_dealership_inventory_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Captures new user registration input (full name, email, password with min 8 chars).
 */
public record RegistrationRequest(
        @NotBlank(message = "Full Name is required")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {
}
