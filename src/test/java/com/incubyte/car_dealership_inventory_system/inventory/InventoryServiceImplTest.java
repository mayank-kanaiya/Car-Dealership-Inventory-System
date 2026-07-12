package com.incubyte.car_dealership_inventory_system.inventory;

import com.incubyte.car_dealership_inventory_system.dto.mapper.VehicleMapper;
import com.incubyte.car_dealership_inventory_system.dto.request.InventoryRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.entity.Vehicle;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import com.incubyte.car_dealership_inventory_system.exception.InsufficientStockException;
import com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException;
import com.incubyte.car_dealership_inventory_system.repository.VehicleRepository;
import com.incubyte.car_dealership_inventory_system.service.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VehicleMapper vehicleMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Vehicle vehicle;
    private UUID vehicleId;
    private VehicleResponse vehicleResponse;

    @BeforeEach
    void setUp() {
        vehicleId = UUID.randomUUID();
        vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setCategory(VehicleCategory.SEDAN);
        vehicle.setPrice(new BigDecimal("28000.00"));
        vehicle.setQuantityInStock(10);
        vehicle.setImageUrl("/images/default-vehicle.svg");

        vehicleResponse = new VehicleResponse(
                vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                new BigDecimal("28000.00"), 10, "/images/default-vehicle.svg"
        );
    }

    private void stubMapper(Vehicle v, VehicleResponse resp) {
        when(vehicleMapper.toResponse(v)).thenReturn(resp);
    }

    // =========================================================================
    // purchase
    // =========================================================================

    @Nested
    @DisplayName("Purchase")
    class Purchase {

        @Test
        @DisplayName("Should decrease stock when purchase is valid")
        void shouldDecreaseStockWhenPurchaseIsValid() {
            InventoryRequest request = new InventoryRequest(3);
            VehicleResponse expectedResponse = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 7, "/images/default-vehicle.svg"
            );

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(expectedResponse);

            VehicleResponse response = inventoryService.purchase(vehicleId, request);

            assertNotNull(response);
            assertEquals(7, vehicle.getQuantityInStock());
            verify(vehicleRepository).findById(vehicleId);
            verify(vehicleRepository).save(vehicle);
        }

        @Test
        @DisplayName("Should decrease stock to zero when purchasing all available stock")
        void shouldDecreaseStockToZeroWhenPurchasingAll() {
            InventoryRequest request = new InventoryRequest(10);
            VehicleResponse expectedResponse = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 0, "/images/default-vehicle.svg"
            );

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(expectedResponse);

            VehicleResponse response = inventoryService.purchase(vehicleId, request);

            assertNotNull(response);
            assertEquals(0, vehicle.getQuantityInStock());
            verify(vehicleRepository).save(vehicle);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when vehicle does not exist")
        void shouldThrowWhenVehicleDoesNotExist() {
            InventoryRequest request = new InventoryRequest(1);

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> inventoryService.purchase(vehicleId, request)
            );

            assertEquals("Vehicle not found with id: " + vehicleId, exception.getMessage());
            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when requested quantity exceeds stock")
        void shouldThrowWhenInsufficientStock() {
            InventoryRequest request = new InventoryRequest(20);

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

            InsufficientStockException exception = assertThrows(
                    InsufficientStockException.class,
                    () -> inventoryService.purchase(vehicleId, request)
            );

            assertEquals(
                    "Insufficient stock for Toyota Camry: requested 20, available 10",
                    exception.getMessage()
            );
            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InsufficientStockException when stock is zero")
        void shouldThrowWhenStockIsZero() {
            vehicle.setQuantityInStock(0);
            InventoryRequest request = new InventoryRequest(1);

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));

            InsufficientStockException exception = assertThrows(
                    InsufficientStockException.class,
                    () -> inventoryService.purchase(vehicleId, request)
            );

            assertEquals(
                    "Insufficient stock for Toyota Camry: requested 1, available 0",
                    exception.getMessage()
            );
            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return response with correct fields after purchase")
        void shouldReturnResponseWithCorrectFields() {
            InventoryRequest request = new InventoryRequest(3);
            VehicleResponse expectedResponse = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 7, "/images/default-vehicle.svg"
            );

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(expectedResponse);

            VehicleResponse response = inventoryService.purchase(vehicleId, request);

            assertEquals(vehicleId, response.id());
            assertEquals("Toyota", response.make());
            assertEquals("Camry", response.model());
            assertEquals(VehicleCategory.SEDAN, response.category());
            assertEquals(new BigDecimal("28000.00"), response.price());
            assertEquals(7, response.quantityInStock());
        }
    }

    // =========================================================================
    // restock
    // =========================================================================

    @Nested
    @DisplayName("Restock")
    class Restock {

        @Test
        @DisplayName("Should increase stock when restock is valid")
        void shouldIncreaseStockWhenRestockIsValid() {
            InventoryRequest request = new InventoryRequest(5);
            VehicleResponse expectedResponse = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 15, "/images/default-vehicle.svg"
            );

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(expectedResponse);

            VehicleResponse response = inventoryService.restock(vehicleId, request);

            assertNotNull(response);
            assertEquals(15, vehicle.getQuantityInStock());
            verify(vehicleRepository).findById(vehicleId);
            verify(vehicleRepository).save(vehicle);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when vehicle does not exist")
        void shouldThrowWhenVehicleDoesNotExist() {
            InventoryRequest request = new InventoryRequest(5);

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> inventoryService.restock(vehicleId, request)
            );

            assertEquals("Vehicle not found with id: " + vehicleId, exception.getMessage());
            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should return response with correct fields after restock")
        void shouldReturnResponseWithCorrectFields() {
            InventoryRequest request = new InventoryRequest(5);
            VehicleResponse expectedResponse = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 15, "/images/default-vehicle.svg"
            );

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(expectedResponse);

            VehicleResponse response = inventoryService.restock(vehicleId, request);

            assertEquals(vehicleId, response.id());
            assertEquals("Toyota", response.make());
            assertEquals("Camry", response.model());
            assertEquals(VehicleCategory.SEDAN, response.category());
            assertEquals(new BigDecimal("28000.00"), response.price());
            assertEquals(15, response.quantityInStock());
        }

        @Test
        @DisplayName("Should handle restock when current stock is zero")
        void shouldHandleRestockWhenStockIsZero() {
            vehicle.setQuantityInStock(0);
            InventoryRequest request = new InventoryRequest(10);
            VehicleResponse expectedResponse = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10, "/images/default-vehicle.svg"
            );

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(expectedResponse);

            VehicleResponse response = inventoryService.restock(vehicleId, request);

            assertNotNull(response);
            assertEquals(10, vehicle.getQuantityInStock());
        }

        @Test
        @DisplayName("Should accumulate stock on multiple restocks")
        void shouldAccumulateStockOnMultipleRestocks() {
            InventoryRequest firstRestock = new InventoryRequest(5);
            InventoryRequest secondRestock = new InventoryRequest(3);

            VehicleResponse resp15 = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 15, "/images/default-vehicle.svg"
            );
            VehicleResponse resp18 = new VehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 18, "/images/default-vehicle.svg"
            );

            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicle));
            when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
            when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(resp15, resp18);

            inventoryService.restock(vehicleId, firstRestock);
            assertEquals(15, vehicle.getQuantityInStock());

            inventoryService.restock(vehicleId, secondRestock);
            assertEquals(18, vehicle.getQuantityInStock());

            verify(vehicleRepository, org.mockito.Mockito.times(2)).save(vehicle);
        }
    }
}
