package com.incubyte.car_dealership_inventory_system.repository;

import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class VehicleSpecification {

    private VehicleSpecification() {
    }

    public static Specification<Vehicle> hasMake(String make) {
        return (root, query, cb) ->
                make == null ? null : cb.equal(
                        cb.lower(root.get("make")), make.toLowerCase()
                );
    }

    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) ->
                model == null ? null : cb.equal(
                        cb.lower(root.get("model")), model.toLowerCase()
                );
    }

    public static Specification<Vehicle> hasCategory(VehicleCategory category) {
        return (root, query, cb) ->
                category == null ? null : cb.equal(root.get("category"), category);
    }

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
