package com.incubyte.car_dealership_inventory_system.security;

public class ExpiredTokenException extends RuntimeException {

    public ExpiredTokenException(String message) {
        super(message);
    }
}