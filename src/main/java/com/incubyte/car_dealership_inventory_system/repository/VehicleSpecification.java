package com.incubyte.car_dealership_inventory_system.repository;

import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * Implements the Strategy Pattern for dynamic vehicle search, providing composable
 * JPA Specification predicates that are null-safe (return null when parameter is not provided).
 */
public class VehicleSpecification {

    private VehicleSpecification() {
    }

    /**
     * Specification that filters vehicles by make (case-insensitive).
     * Returns null when make is null so it can be combined with other predicates.
     */
    public static Specification<Vehicle> hasMake(String make) {
        return (root, query, cb) ->
                make == null ? null : cb.equal(
                        cb.lower(root.get("make")), make.toLowerCase()
                );
    }

    /**
     * Specification that filters vehicles by model (case-insensitive).
     * Returns null when model is null so it can be combined with other predicates.
     */
    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) ->
                model == null ? null : cb.equal(
                        cb.lower(root.get("model")), model.toLowerCase()
                );
    }

    /**
     * Specification that filters vehicles by the exact VehicleCategory enum value.
     * Returns null when category is null so it can be combined with other predicates.
     */
    public static Specification<Vehicle> hasCategory(VehicleCategory category) {
        return (root, query, cb) ->
                category == null ? null : cb.equal(root.get("category"), category);
    }

    /**
     * Specification that filters vehicles within a price range (inclusive).
     * When only one bound is provided, uses greater-than or less-than accordingly.
     * Returns null when both minPrice and maxPrice are null.
     */
    public static Specification<Vehicle> priceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (minPrice != null && maxPrice != null) {
                return cb.between(root.get("price"), minPrice, maxPrice);
            }
            if (minPrice != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            }
            if (maxPrice != null) {
                return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return null;
        };
    }
}
