package com.incubyte.car_dealership_inventory_system.exception;

/**
 * Thrown when attempting to create a vehicle that already exists. Maps to HTTP 409.
 */
public class DuplicateVehicleException extends BaseException {

    public DuplicateVehicleException(String message) {
        super(message, "DUPLICATE_VEHICLE");
    }
}
