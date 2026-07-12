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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody RegistrationRequest request
    ) {
        AuthenticationResponse response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        AuthenticationResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
