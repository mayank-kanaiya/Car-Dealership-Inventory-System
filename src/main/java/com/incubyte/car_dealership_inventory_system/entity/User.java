package com.incubyte.car_dealership_inventory_system.entity;

import java.util.UUID;

import com.incubyte.car_dealership_inventory_system.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an authenticated user of the dealership system.
 *
 * <p>Extends {@link BaseEntity} for audit tracking. The {@code role} field
 * defaults to {@link UserRole#USER} via {@link #applyDefaults()} if not
 * explicitly set before persisting.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String fullName;

    /** Unique login email address. Used as the username principal in authentication. */
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /** BCrypt-encoded password hash. Never stored in plaintext. */
    @Column(nullable = false)
    private String password;

    /** Authorization role for this user. Defaults to {@code USER} if not set. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    /**
     * JPA lifecycle callback that assigns a default role before the first insert.
     *
     * <p>If {@code role} was not explicitly set, it is defaulted to
     * {@link UserRole#USER} to ensure every persisted user has a valid role.</p>
     */
    @PrePersist
    private void applyDefaults() {
        if (role == null) {
            role = UserRole.USER;
        }
    }
}
