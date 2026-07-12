package com.incubyte.car_dealership_inventory_system.exception;

/**
 * Thrown when a purchase request exceeds available stock. Maps to HTTP 409.
 */
public class InsufficientStockException extends BaseException {

    public InsufficientStockException(String make, String model, int available, int requested) {
        super(String.format(
                "Insufficient stock for %s %s: requested %d, available %d",
                make, model, requested, available
        ), "INSUFFICIENT_STOCK");
    }

    public InsufficientStockException(String message) {
        super(message, "INSUFFICIENT_STOCK");
    }
}
