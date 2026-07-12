package com.incubyte.car_dealership_inventory_system.controller;

import com.incubyte.car_dealership_inventory_system.dto.request.AuthenticationRequest;
import com.incubyte.car_dealership_inventory_system.dto.request.RegistrationRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.AuthenticationResponse;
import com.incubyte.car_dealership_inventory_system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes public authentication endpoints for user registration and login.
 * No authentication is required to call these endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/v1/auth/register — Registers a new user account and returns a JWT token.
     *
     * @param request registration details (fullName, email, password with min 8 characters)
     * @return 201 Created with an AuthenticationResponse containing a success message and JWT
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegistrationRequest request
    ) {
        AuthenticationResponse response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/login — Authenticates an existing user and returns a JWT token.
     *
     * @param request login credentials (email and password)
     * @return 200 OK with an AuthenticationResponse containing a success message and JWT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
