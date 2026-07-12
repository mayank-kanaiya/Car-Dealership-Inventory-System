package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.exception.DuplicateVehicleException;
import com.incubyte.car_dealership_inventory_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setCategory(request.category());
        vehicle.setPrice(request.price());
        vehicle.setQuantityInStock(request.quantityInStock());

        Vehicle saved = vehicleRepository.save(vehicle);

        return new VehicleResponse(
                saved.getId(),
                saved.getMake(),
                saved.getModel(),
                saved.getCategory(),
                saved.getPrice(),
                saved.getQuantityInStock()
        );
    }
}
