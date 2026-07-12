package com.incubyte.car_dealership_inventory_system.exception;

/**
 * Thrown when a requested entity does not exist in the database. Maps to HTTP 404.
 */
public class ResourceNotFoundException extends BaseException {

    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s not found with id: %s", resource, id), "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }
}
