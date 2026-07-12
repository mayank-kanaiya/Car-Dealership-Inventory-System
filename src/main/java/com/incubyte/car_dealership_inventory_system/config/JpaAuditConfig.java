package com.incubyte.car_dealership_inventory_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Enables JPA auditing and supplies the current authenticated user as the auditor.
 *
 * <p>When Spring Data persists or updates an entity extending {@link com.incubyte.car_dealership_inventory_system.entity.BaseEntity},
 * it calls the {@link AuditorAware} bean to resolve the {@code createdBy} / {@code updatedBy}
 * values from the security context.</p>
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName);
    }
}
