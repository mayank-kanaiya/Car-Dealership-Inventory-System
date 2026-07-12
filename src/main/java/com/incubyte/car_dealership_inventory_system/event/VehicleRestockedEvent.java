package com.incubyte.car_dealership_inventory_system.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event published when a vehicle restock completes.
 */
public record VehicleRestockedEvent(
        String vehicleId,
        String make,
        String model,
        int quantityAdded,
        int newStockLevel,
        BigDecimal price,
        String restockedBy,
        LocalDateTime timestamp
) {}
