package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;

public interface VehicleService {
    VehicleResponse addVehicle(VehicleRequest request);
}
