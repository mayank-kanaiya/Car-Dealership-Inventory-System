package com.incubyte.car_dealership_inventory_system.dto.response;

/**
 * Wraps the authentication result message and JWT token, returned on register and login.
 */
public record AuthenticationResponse(
        String message,
        String token
) {
}
