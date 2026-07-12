package com.incubyte.car_dealership_inventory_system.exception;

/**
 * Thrown when a JWT token has expired. Maps to HTTP 401.
 */
public class ExpiredTokenException extends BaseException {

    public ExpiredTokenException(String message) {
        super(message, "EXPIRED_TOKEN");
    }
}
