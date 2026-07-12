package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;

import java.util.List;
import java.util.UUID;

public interface VehicleService {
    VehicleResponse addVehicle(VehicleRequest request);
    List<VehicleResponse> getAllVehicles();
    VehicleResponse getVehicleById(UUID id);
    VehicleResponse updateVehicle(UUID id, VehicleRequest request);
    void deleteVehicle(UUID id);
}
