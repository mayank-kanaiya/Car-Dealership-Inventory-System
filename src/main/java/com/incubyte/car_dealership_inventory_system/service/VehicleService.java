package com.incubyte.car_dealership_inventory_system.service;

import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.PagedResponse;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service interface for vehicle CRUD operations, including paginated retrieval,
 * multi-criteria search, and image management.
 */
public interface VehicleService {

    /**
     * Creates a new vehicle with a default placeholder image.
     *
     * @param request the vehicle details to persist
     * @return the created vehicle with its generated ID
     */
    VehicleResponse addVehicle(VehicleRequest request);

    /**
     * Retrieves a paginated, sorted list of all vehicles.
     *
     * @param page      zero-based page index
     * @param size      number of items per page
     * @param sortBy    the field name to sort by
     * @param direction sort direction, either {@code "asc"} or {@code "desc"}
     * @return a paged response containing vehicle data
     */
    PagedResponse<VehicleResponse> getAllVehicles(int page, int size, String sortBy, String direction);

    /**
     * Fetches a single vehicle by its unique identifier.
     *
     * @param id the vehicle's UUID
     * @return the matching vehicle
     * @throws com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException if no vehicle exists with the given ID
     */
    VehicleResponse getVehicleById(UUID id);

    /**
     * Updates all mutable fields of an existing vehicle.
     *
     * @param id      the vehicle's UUID
     * @param request the new values to apply
     * @return the updated vehicle
     * @throws com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException if the vehicle does not exist
     */
    VehicleResponse updateVehicle(UUID id, VehicleRequest request);

    /**
     * Deletes a vehicle and its associated image from storage.
     *
     * @param id the vehicle's UUID
     * @throws com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException if the vehicle does not exist
     */
    void deleteVehicle(UUID id);

    /**
     * Searches vehicles by an intersection of optional filters (make, model, category,
     * and price range). All supplied non-null filters are combined with AND logic.
     *
     * @param make     partial or full make to match (nullable)
     * @param model    partial or full model to match (nullable)
     * @param category vehicle category to filter by (nullable)
     * @param minPrice minimum price inclusive (nullable)
     * @param maxPrice maximum price inclusive (nullable)
     * @param page     zero-based page index
     * @param size     number of items per page
     * @return a paged response of matching vehicles
     */
    PagedResponse<VehicleResponse> searchVehicles(String make, String model, VehicleCategory category,
                                                    BigDecimal minPrice, BigDecimal maxPrice,
                                                    int page, int size);

    /**
     * Uploads a new image for an existing vehicle, replacing any previously assigned image.
     *
     * @param id   the vehicle's UUID
     * @param file the image file to upload
     * @return the vehicle with the updated image URL
     * @throws com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException if the vehicle does not exist
     */
    VehicleResponse uploadVehicleImage(UUID id, MultipartFile file);
}
