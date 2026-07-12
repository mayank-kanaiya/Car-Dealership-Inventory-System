package com.incubyte.car_dealership_inventory_system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * JPA auditing superclass that auto-tracks who created/modified a record and when.
 *
 * <p>Any entity extending this class will automatically have {@code createdAt},
 * {@code updatedAt}, {@code createdBy}, and {@code updatedBy} populated by
 * Spring Data JPA auditing via {@link AuditingEntityListener}.</p>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    /** Timestamp when the record was first persisted. Set once and never updated. */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last modification to the record. Updated on every save. */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /** Principal name of the user who created the record. Set once and never updated. */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    /** Principal name of the user who last modified the record. Updated on every save. */
    @LastModifiedBy
    private String updatedBy;
}
