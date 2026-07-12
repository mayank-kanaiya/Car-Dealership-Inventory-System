package com.incubyte.car_dealership_inventory_system.dto.request;

import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Captures vehicle creation/update input with validation constraints.
 * Used as the @RequestBody for POST and PUT vehicle endpoints.
 */
public record VehicleRequest(
        @NotBlank(message = "Make is required")
        @Size(max = 80, message = "Make must not exceed 80 characters")
        String make,

        @NotBlank(message = "Model is required")
        @Size(max = 80, message = "Model must not exceed 80 characters")
        String model,

        @NotNull(message = "Category is required")
        VehicleCategory category,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        @Digits(integer = 10, fraction = 2, message = "Price format invalid")
        BigDecimal price,

        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantityInStock
) {
}
