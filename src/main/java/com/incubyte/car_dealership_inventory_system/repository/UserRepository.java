package com.incubyte.car_dealership_inventory_system.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.incubyte.car_dealership_inventory_system.entity.User;

/**
 * Provides User entity data access, with lookup by email for authentication.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
