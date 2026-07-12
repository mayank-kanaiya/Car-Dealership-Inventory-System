package com.incubyte.car_dealership_inventory_system.auth.dto;

public record AuthenticationResponse(
        String message,
        String token
) {
}