package com.incubyte.car_dealership_inventory_system.dto.mapper;

import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts between Vehicle entity and DTOs (VehicleRequest, VehicleResponse)
 * to keep the API contract decoupled from the database model.
 */
@Component
public class VehicleMapper {

    /**
     * Maps a VehicleRequest DTO to a new Vehicle entity (without ID).
     */
    public Vehicle toEntity(VehicleRequest request) {
        return Vehicle.builder()
                .make(request.make())
                .model(request.model())
                .category(request.category())
                .price(request.price())
                .quantityInStock(request.quantityInStock())
                .build();
    }

    /**
     * Maps a Vehicle entity to a VehicleResponse DTO for API output.
     */
    public VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getCategory(),
                vehicle.getPrice(),
                vehicle.getQuantityInStock(),
                vehicle.getImageUrl()
        );
    }

    /**
     * Maps a list of Vehicle entities to a list of VehicleResponse DTOs.
     */
    public List<VehicleResponse> toResponseList(List<Vehicle> vehicles) {
        return vehicles.stream()
                .map(this::toResponse)
                .toList();
    }
}
