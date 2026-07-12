package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.AuthenticationRequest;
import com.incubyte.car_dealership_inventory_system.dto.request.RegistrationRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.AuthenticationResponse;

public interface AuthService {
    AuthenticationResponse registerUser(RegistrationRequest request);
    AuthenticationResponse login(AuthenticationRequest request);
}
