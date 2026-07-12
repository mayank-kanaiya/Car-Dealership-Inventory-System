package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException;
import com.incubyte.car_dealership_inventory_system.repository.VehicleRepository;
import com.incubyte.car_dealership_inventory_system.repository.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ImageService imageService;

    @Value("${vehicle.default-image:/images/default-vehicle.svg}")
    private String defaultImagePath;

    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        applyFields(vehicle, request);
        vehicle.setImageUrl(defaultImagePath);
        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        return toResponse(vehicle);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(UUID id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        applyFields(vehicle, request);
        Vehicle updated = vehicleRepository.save(vehicle);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteVehicle(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
        imageService.deleteImage(vehicle.getImageUrl());
        vehicleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> searchVehicles(String make, String model, VehicleCategory category,
                                                 BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecification.hasMake(make))
                .and(VehicleSpecification.hasModel(model))
                .and(VehicleSpecification.hasCategory(category))
                .and(VehicleSpecification.priceBetween(minPrice, maxPrice));

        return vehicleRepository.findAll(spec)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public VehicleResponse uploadVehicleImage(UUID id, MultipartFile file) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        String oldImageUrl = vehicle.getImageUrl();
        String newImageUrl = imageService.uploadImage(file);

        vehicle.setImageUrl(newImageUrl);
        Vehicle saved = vehicleRepository.save(vehicle);

        if (oldImageUrl != null && !oldImageUrl.equals(defaultImagePath)) {
            imageService.deleteImage(oldImageUrl);
        }

        return toResponse(saved);
    }

    private void applyFields(Vehicle vehicle, VehicleRequest request) {
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setCategory(request.category());
        vehicle.setPrice(request.price());
        vehicle.setQuantityInStock(request.quantityInStock());
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
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
}
