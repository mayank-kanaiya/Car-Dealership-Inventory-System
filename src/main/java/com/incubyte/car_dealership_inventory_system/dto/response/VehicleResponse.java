package com.incubyte.car_dealership_inventory_system.dto.response;

import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * API-facing representation of a vehicle entity, returned on all vehicle endpoints.
 */
public record VehicleResponse(
        UUID id,
        String make,
        String model,
        VehicleCategory category,
        BigDecimal price,
        Integer quantityInStock,
        String imageUrl
) {
}
