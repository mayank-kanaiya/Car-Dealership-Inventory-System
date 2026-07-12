package com.incubyte.car_dealership_inventory_system.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardized error response record with factory methods for common error scenarios
 * (business errors, validation errors).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        String errorCode,
        List<FieldError> details
) {

    /**
     * Creates a generic error response without a machine-readable error code.
     */
    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                null,
                null
        );
    }

    /**
     * Creates a detailed error response with a machine-readable error code.
     */
    public static ApiErrorResponse of(HttpStatus status, String message, String path, String errorCode) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                errorCode,
                null
        );
    }

    /**
     * Creates a validation-error response containing a list of per-field errors.
     * Automatically sets status to 400 and errorCode to "VALIDATION_ERROR".
     */
    public static ApiErrorResponse ofValidation(String path, List<FieldError> errors) {
        return new ApiErrorResponse(
                LocalDateTime.now(),
                400,
                "Validation Failed",
                "One or more fields are invalid",
                path,
                "VALIDATION_ERROR",
                errors
        );
    }

    public record FieldError(String field, String message, Object rejectedValue) {}
}
