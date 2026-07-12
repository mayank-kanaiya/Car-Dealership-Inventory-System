package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.InventoryRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.exception.InsufficientStockException;
import com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException;
import com.incubyte.car_dealership_inventory_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public VehicleResponse purchase(UUID vehicleId, InventoryRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));

        if (vehicle.getQuantityInStock() < request.quantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for vehicle " + vehicle.getMake() + " " + vehicle.getModel()
                            + ": requested " + request.quantity() + ", available " + vehicle.getQuantityInStock()
            );
        }

        vehicle.setQuantityInStock(vehicle.getQuantityInStock() - request.quantity());
        Vehicle saved = vehicleRepository.save(vehicle);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public VehicleResponse restock(UUID vehicleId, InventoryRequest request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));

        vehicle.setQuantityInStock(vehicle.getQuantityInStock() + request.quantity());
        Vehicle saved = vehicleRepository.save(vehicle);

        return toResponse(saved);
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getCategory(),
                vehicle.getPrice(),
                vehicle.getQuantityInStock()
        );
    }
}
