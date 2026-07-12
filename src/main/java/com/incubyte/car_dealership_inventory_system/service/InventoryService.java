package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.InventoryRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;

import java.util.UUID;

public interface InventoryService {
    VehicleResponse purchase(UUID vehicleId, InventoryRequest request);
    VehicleResponse restock(UUID vehicleId, InventoryRequest request);
}
