package com.incubyte.car_dealership_inventory_system.exception;

/**
 * Thrown when a registration attempt uses an email that is already registered. Maps to HTTP 409.
 */
public class UserAlreadyExistsException extends BaseException {

    public UserAlreadyExistsException(String message) {
        super(message, "USER_ALREADY_EXISTS");
    }
}
