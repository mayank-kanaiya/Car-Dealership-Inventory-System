package com.incubyte.car_dealership_inventory_system.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.incubyte.car_dealership_inventory_system.entity.Vehicle;

/**
 * Extends both JpaRepository (CRUD operations) and JpaSpecificationExecutor
 * (dynamic search via Specifications).
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {
}
