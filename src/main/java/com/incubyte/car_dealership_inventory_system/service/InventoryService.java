package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.InventoryRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;

import java.util.UUID;

/**
 * Service interface for inventory-level operations: decrementing stock on
 * purchase and incrementing stock on restock.
 */
public interface InventoryService {

    /**
     * Decrements the vehicle's stock by the requested quantity, validating
     * that sufficient inventory is available before proceeding.
     *
     * @param vehicleId the UUID of the vehicle being purchased
     * @param request   contains the quantity to purchase
     * @return the vehicle with updated stock levels
     * @throws com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException  if the vehicle does not exist
     * @throws com.incubyte.car_dealership_inventory_system.exception.InsufficientStockException if requested quantity exceeds available stock
     */
    VehicleResponse purchase(UUID vehicleId, InventoryRequest request);

    /**
     * Increments the vehicle's stock by the requested quantity.
     *
     * @param vehicleId the UUID of the vehicle being restocked
     * @param request   contains the quantity to add
     * @return the vehicle with updated stock levels
     * @throws com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException if the vehicle does not exist
     */
    VehicleResponse restock(UUID vehicleId, InventoryRequest request);
}
