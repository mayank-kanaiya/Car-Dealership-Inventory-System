package com.incubyte.car_dealership_inventory_system.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain event published when a vehicle purchase completes, carrying purchase details
 * for downstream consumers (audit logging, low-stock alerts).
 */
public record VehiclePurchasedEvent(
        String vehicleId,
        String make,
        String model,
        int quantityPurchased,
        int remainingStock,
        BigDecimal price,
        String purchasedBy,
        LocalDateTime timestamp
) {}
