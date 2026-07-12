package com.incubyte.car_dealership_inventory_system.exception;

/**
 * Root of the custom exception hierarchy, carrying a machine-readable error code
 * alongside the human-readable message.
 */
public abstract class BaseException extends RuntimeException {

    private final String errorCode;

    protected BaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
