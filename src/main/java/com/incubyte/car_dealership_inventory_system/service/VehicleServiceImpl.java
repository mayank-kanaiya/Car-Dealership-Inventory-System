package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.mapper.VehicleMapper;
import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.PagedResponse;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException;
import com.incubyte.car_dealership_inventory_system.repository.VehicleRepository;
import com.incubyte.car_dealership_inventory_system.repository.VehicleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implements vehicle CRUD with paginated retrieval, multi-criteria search via
 * JPA {@link Specification} composition, and image lifecycle management
 * delegated to {@link ImageService} backed by Cloudinary.
 */
@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ImageService imageService;
    private final VehicleMapper vehicleMapper;

    @Value("${vehicle.default-image:/images/default-vehicle.svg}")
    private String defaultImagePath;

    /**
     * {@inheritDoc}
     *
     * <p>New vehicles are assigned a configurable default placeholder image
     * until a real image is uploaded.</p>
     */
    @Override
    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request) {
        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setImageUrl(defaultImagePath);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(saved);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Returns an empty page when the page index exceeds available data.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VehicleResponse> getAllVehicles(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(pageable);

        Page<VehicleResponse> responsePage = vehiclePage.map(vehicleMapper::toResponse);
        return PagedResponse.of(responsePage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id.toString()));
        return vehicleMapper.toResponse(vehicle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public VehicleResponse updateVehicle(UUID id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id.toString()));
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setCategory(request.category());
        vehicle.setPrice(request.price());
        vehicle.setQuantityInStock(request.quantityInStock());
        Vehicle updated = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponse(updated);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cleans up the vehicle's hosted image before removing the entity.</p>
     */
    @Override
    @Transactional
    public void deleteVehicle(UUID id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id.toString()));
        imageService.deleteImage(vehicle.getImageUrl());
        vehicleRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Builds a dynamic query by chaining individual {@link Specification}
     * predicates. Each {@code VehicleSpecification} factory method returns
     * a Specification that matches a single criterion; the {@code .and()}
     * chain composes them so that only vehicles satisfying <em>all</em>
     * non-null filters are returned. Null filters are effectively no-ops
     * inside their respective factory methods, so the final query adapts
     * transparently to whichever parameters the caller supplies.</p>
     */
    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VehicleResponse> searchVehicles(String make, String model, VehicleCategory category,
                                                          BigDecimal minPrice, BigDecimal maxPrice,
                                                          int page, int size) {
        Specification<Vehicle> spec = Specification
                .where(VehicleSpecification.hasMake(make))
                .and(VehicleSpecification.hasModel(model))
                .and(VehicleSpecification.hasCategory(category))
                .and(VehicleSpecification.priceBetween(minPrice, maxPrice));

        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);
        Page<VehicleResponse> responsePage = vehiclePage.map(vehicleMapper::toResponse);
        return PagedResponse.of(responsePage);
    }

    /**
     * {@inheritDoc}
     *
     * <p>After persisting the new image URL, the previously stored image is
     * deleted from Cloudinary only if it is neither null nor the default
     * placeholder. This ensures the placeholder is never accidentally removed
     * from local storage while still cleaning up uploaded assets to avoid
     * orphaned files in the cloud.</p>
     */
    @Override
    @Transactional
    public VehicleResponse uploadVehicleImage(UUID id, MultipartFile file) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id.toString()));

        String oldImageUrl = vehicle.getImageUrl();
        String newImageUrl = imageService.uploadImage(file);

        vehicle.setImageUrl(newImageUrl);
        Vehicle saved = vehicleRepository.save(vehicle);

        // Only delete the old image if it was an uploaded Cloudinary asset,
        // not the default placeholder served from local resources.
        if (oldImageUrl != null && !oldImageUrl.equals(defaultImagePath)) {
            imageService.deleteImage(oldImageUrl);
        }

        return vehicleMapper.toResponse(saved);
    }
}
