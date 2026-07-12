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

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/{id}/purchase")
    public ResponseEntity<VehicleResponse> purchase(
            @PathVariable UUID id,
            @Valid @RequestBody InventoryRequest request
    ) {
        VehicleResponse response = inventoryService.purchase(id, request);
        return ResponseEntity.ok(response);
    }

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
