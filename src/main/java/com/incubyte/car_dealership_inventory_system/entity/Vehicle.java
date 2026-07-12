package com.incubyte.car_dealership_inventory_system.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a single vehicle listing in the dealership inventory.
 *
 * <p>Extends {@link BaseEntity} for automatic audit fields and uses optimistic
 * locking via {@code version} to prevent concurrent update conflicts.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicles")
public class Vehicle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Manufacturer name (e.g. "Toyota", "Ford"). */
    @Column(nullable = false, length = 80)
    private String make;

    /** Model name (e.g. "Camry", "Mustang"). */
    @Column(nullable = false, length = 80)
    private String model;

    /** Vehicle type classification — see {@link VehicleCategory} for supported values. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VehicleCategory category;

    /** List price in the dealership's base currency. */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** Current units available in inventory. May be zero or negative if back-ordered. */
    @Column(nullable = false)
    private Integer quantityInStock;

    /** Cloudinary URL pointing to the vehicle's primary image asset. */
    @Column(length = 500)
    private String imageUrl;

    /**
     * Optimistic locking version counter.
     *
     * <p>JPA increments this on every update; concurrent transactions operating
     * on stale data will trigger an {@code OptimisticLockException}.</p>
     */
    @Version
    private Long version;
}
