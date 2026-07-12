package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.AuthenticationRequest;
import com.incubyte.car_dealership_inventory_system.dto.request.RegistrationRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.AuthenticationResponse;

/**
 * Service interface for user authentication, covering new account registration
 * and credential-based login. Both operations return a signed JWT.
 */
public interface AuthService {

    /**
     * Registers a new user with the given details, enforcing uniqueness of
     * the email address. Returns a JWT token upon successful registration.
     *
     * @param request the registration payload (full name, email, password)
     * @return an authentication response containing a success message and JWT
     * @throws com.incubyte.car_dealership_inventory_system.exception.UserAlreadyExistsException if the email is already registered
     */
    AuthenticationResponse registerUser(RegistrationRequest request);

    /**
     * Authenticates an existing user by verifying credentials through
     * Spring's {@code AuthenticationManager}. Returns a signed JWT on success.
     *
     * @param request the login payload (email and password)
     * @return an authentication response containing a success message and JWT
     * @throws org.springframework.security.authentication.BadCredentialsException if the credentials are invalid
     */
    AuthenticationResponse login(AuthenticationRequest request);
}
