package com.incubyte.car_dealership_inventory_system.auth;

import com.incubyte.car_dealership_inventory_system.auth.dto.AuthenticationResponse;
import com.incubyte.car_dealership_inventory_system.auth.dto.RegistrationRequest;

public interface AuthService {
    AuthenticationResponse registerUser(RegistrationRequest request);
}