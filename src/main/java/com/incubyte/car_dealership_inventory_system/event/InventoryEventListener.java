package com.incubyte.car_dealership_inventory_system.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Listens for inventory domain events and handles side-effect logging (purchase audit,
 * low-stock alerts, restock audit) without coupling to the core business logic.
 */
@Component
@Slf4j
public class InventoryEventListener {

    /**
     * Logs a purchase audit entry whenever a vehicle is purchased.
     */
    @EventListener
    public void handlePurchase(VehiclePurchasedEvent event) {
        log.info("Vehicle purchased: {} {} x{} by {} at {}",
                event.make(), event.model(),
                event.quantityPurchased(), event.purchasedBy(),
                event.timestamp());
    }

    /**
     * Logs a warning when a purchase causes stock to drop below the low-stock threshold ( < 5 ).
     */
    @EventListener
    public void handleLowStock(VehiclePurchasedEvent event) {
        if (event.remainingStock() < 5) {
            log.warn("LOW STOCK ALERT: {} {} — only {} left",
                    event.make(), event.model(), event.remainingStock());
        }
    }

    /**
     * Logs a restock audit entry whenever a vehicle is restocked.
     */
    @EventListener
    public void handleRestock(VehicleRestockedEvent event) {
        log.info("Vehicle restocked: {} {} x{} by {} — new stock: {}",
                event.make(), event.model(),
                event.quantityAdded(), event.restockedBy(),
                event.newStockLevel());
    }
}
