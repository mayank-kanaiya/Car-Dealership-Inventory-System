package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.AuthenticationRequest;
import com.incubyte.car_dealership_inventory_system.dto.request.RegistrationRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.AuthenticationResponse;
import com.incubyte.car_dealership_inventory_system.entity.User;
import com.incubyte.car_dealership_inventory_system.enums.UserRole;
import com.incubyte.car_dealership_inventory_system.exception.UserAlreadyExistsException;
import com.incubyte.car_dealership_inventory_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements authentication operations. Registration checks for duplicate
 * emails, hashes the password with BCrypt via {@link PasswordEncoder},
 * persists the user, and returns a JWT. Login delegates credential
 * verification to Spring's {@link AuthenticationManager}, which consults
 * the {@code CustomUserDetailsService} chain.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * {@inheritDoc}
     *
     * <p>The raw password is BCrypt-hashed before the entity is persisted
     * so that plaintext credentials are never stored.</p>
     */
    @Override
    @Transactional
    public AuthenticationResponse registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        // Hash the plaintext password before persisting — raw passwords must never hit the database.
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getEmail());

        return new AuthenticationResponse("User registered successfully", jwtToken);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Delegates to Spring's {@link AuthenticationManager} which triggers
     * the configured authentication provider chain (in our case,
     * {@code DaoAuthenticationProvider} backed by {@code CustomUserDetailsService}).
     * An exception is thrown automatically if the credentials are invalid.</p>
     */
    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        String jwtToken = jwtService.generateToken(request.email());

        return new AuthenticationResponse("Login successful", jwtToken);
    }
}
