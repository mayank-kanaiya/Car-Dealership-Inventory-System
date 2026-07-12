package com.incubyte.car_dealership_inventory_system.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Captures login credentials (email and password).
 */
public record AuthenticationRequest(
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
