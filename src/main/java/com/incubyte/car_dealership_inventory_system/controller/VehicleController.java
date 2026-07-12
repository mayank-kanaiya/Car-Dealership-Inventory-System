package com.incubyte.car_dealership_inventory_system.controller;

import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.PagedResponse;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import com.incubyte.car_dealership_inventory_system.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Exposes REST endpoints for vehicle CRUD, search with pagination,
 * image upload, and deletion (ADMIN only).
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * POST /api/v1/vehicles — Creates a new vehicle. Accessible to any authenticated user.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<VehicleResponse> addVehicle(
            @Valid @RequestBody VehicleRequest request
    ) {
        VehicleResponse response = vehicleService.addVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/v1/vehicles — Retrieves a paginated, sorted list of all vehicles.
     *
     * @param page      zero-based page index (default 0)
     * @param size      page size (default 20)
     * @param sortBy    field to sort by (default "id")
     * @param direction sort direction "asc" or "desc" (default "asc")
     */
    @GetMapping
    public ResponseEntity<PagedResponse<VehicleResponse>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        PagedResponse<VehicleResponse> vehicles = vehicleService.getAllVehicles(page, size, sortBy, direction);
        return ResponseEntity.ok(vehicles);
    }

    /**
     * GET /api/v1/vehicles/search — Searches vehicles by optional filters (make, model, category,
     * price range) with pagination. Only non-null parameters are applied as predicates.
     */
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<VehicleResponse>> searchVehicles(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) VehicleCategory category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PagedResponse<VehicleResponse> vehicles = vehicleService.searchVehicles(
                make, model, category, minPrice, maxPrice, page, size);
        return ResponseEntity.ok(vehicles);
    }

    /**
     * GET /api/v1/vehicles/{id} — Retrieves a single vehicle by its UUID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable UUID id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/v1/vehicles/{id} — Fully updates an existing vehicle. Accessible to any authenticated user.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable UUID id,
            @Valid @RequestBody VehicleRequest request
    ) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/vehicles/{id}/image — Uploads an image file for a vehicle and returns the updated
     * vehicle with its image URL.
     */
    @PostMapping("/{id}/image")
    public ResponseEntity<VehicleResponse> uploadVehicleImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file
    ) {
        VehicleResponse response = vehicleService.uploadVehicleImage(id, file);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/vehicles/{id} — Deletes a vehicle. Restricted to ADMIN role only.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
