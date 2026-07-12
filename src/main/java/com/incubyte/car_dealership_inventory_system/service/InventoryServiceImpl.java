package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.mapper.VehicleMapper;
import com.incubyte.car_dealership_inventory_system.dto.request.InventoryRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.event.VehiclePurchasedEvent;
import com.incubyte.car_dealership_inventory_system.event.VehicleRestockedEvent;
import com.incubyte.car_dealership_inventory_system.exception.InsufficientStockException;
import com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException;
import com.incubyte.car_dealership_inventory_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Handles purchase (stock decrement with availability validation) and restock
 * (stock increment) operations. Each operation publishes a corresponding
 * Spring application event ({@link VehiclePurchasedEvent} or
 * {@link VehicleRestockedEvent}) so that downstream listeners (e.g. audit
 * logging, notification services) can react without coupling to this service.
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * {@inheritDoc}
     *
     * <p>Validates that the vehicle has sufficient stock before decrementing.
     * On success a {@link VehiclePurchasedEvent} is published, carrying the
     * current user for audit purposes.</p>
     */
    @Override
    @Transactional
    public VehicleResponse purchase(UUID vehicleId, InventoryRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", vehicleId.toString()));

        // Guard: reject the transaction if available stock is less than the requested quantity.
        if (vehicle.getQuantityInStock() < request.quantity()) {
            throw new InsufficientStockException(
                    vehicle.getMake(), vehicle.getModel(),
                    vehicle.getQuantityInStock(), request.quantity()
            );
        }

        vehicle.setQuantityInStock(vehicle.getQuantityInStock() - request.quantity());
        Vehicle saved = vehicleRepository.save(vehicle);

        // Resolve the identity of the caller from the security context so
        // the event carries an accurate audit trail. Falls back to "system"
        // when invoked outside an authenticated session (e.g. scheduled jobs).
        String currentUser = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "system";

        eventPublisher.publishEvent(new VehiclePurchasedEvent(
                saved.getId().toString(),
                saved.getMake(),
                saved.getModel(),
                request.quantity(),
                saved.getQuantityInStock(),
                saved.getPrice(),
                currentUser,
                LocalDateTime.now()
        ));

        return vehicleMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Publishes a {@link VehicleRestockedEvent} after persisting the
     * updated stock level.</p>
     */
    @Override
    @Transactional
    public VehicleResponse restock(UUID vehicleId, InventoryRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", vehicleId.toString()));

        vehicle.setQuantityInStock(vehicle.getQuantityInStock() + request.quantity());
        Vehicle saved = vehicleRepository.save(vehicle);

        // Resolve the caller's identity for the event payload, mirroring the
        // purchase flow so restock operations are equally auditable.
        String currentUser = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName()
                : "system";

        eventPublisher.publishEvent(new VehicleRestockedEvent(
                saved.getId().toString(),
                saved.getMake(),
                saved.getModel(),
                request.quantity(),
                saved.getQuantityInStock(),
                saved.getPrice(),
                currentUser,
                LocalDateTime.now()
        ));

        return vehicleMapper.toResponse(saved);
    }
}
