package com.incubyte.car_dealership_inventory_system.auth;

import com.incubyte.car_dealership_inventory_system.auth.dto.AuthenticationResponse;
import com.incubyte.car_dealership_inventory_system.auth.dto.RegistrationRequest;
import com.incubyte.car_dealership_inventory_system.entity.User;
import com.incubyte.car_dealership_inventory_system.enums.UserRole;
import com.incubyte.car_dealership_inventory_system.exception.UserAlreadyExistsException;
import com.incubyte.car_dealership_inventory_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthenticationResponse registerUser(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("Email already in use");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER); // Default role

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(user.getEmail());

        return new AuthenticationResponse("User registered successfully", jwtToken);
    }
}