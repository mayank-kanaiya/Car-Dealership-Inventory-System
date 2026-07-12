package com.incubyte.car_dealership_inventory_system.controller;

import com.incubyte.car_dealership_inventory_system.dto.request.InventoryRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Exposes REST endpoints for purchasing (any authenticated user) and restocking
 * (ADMIN only) vehicle inventory.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * POST /api/v1/vehicles/{id}/purchase — Purchases a quantity of a vehicle.
     * Accessible to any authenticated user. Decreases stock and publishes a VehiclePurchasedEvent.
     *
     * @param id      UUID of the vehicle to purchase
     * @param request contains the quantity (must be >= 1)
     */
    @PostMapping("/{id}/purchase")
    public ResponseEntity<VehicleResponse> purchase(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryRequest request
    ) {
        VehicleResponse response = inventoryService.purchase(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/vehicles/{id}/restock — Restocks a quantity of a vehicle.
     * Restricted to ADMIN role only. Increases stock and publishes a VehicleRestockedEvent.
     *
     * @param id      UUID of the vehicle to restock
     * @param request contains the quantity to add (must be >= 1)
     */
    @PostMapping("/{id}/restock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponse> restock(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryRequest request
    ) {
        VehicleResponse response = inventoryService.restock(id, request);
        return ResponseEntity.ok(response);
    }
}
