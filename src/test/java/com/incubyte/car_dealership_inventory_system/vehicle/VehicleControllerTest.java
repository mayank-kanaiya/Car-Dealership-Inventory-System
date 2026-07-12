package com.incubyte.car_dealership_inventory_system.vehicle;

import tools.jackson.databind.ObjectMapper;
import com.incubyte.car_dealership_inventory_system.controller.VehicleController;
import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import com.incubyte.car_dealership_inventory_system.exception.DuplicateVehicleException;
import com.incubyte.car_dealership_inventory_system.exception.GlobalExceptionHandler;
import com.incubyte.car_dealership_inventory_system.service.CustomUserDetailsService;
import com.incubyte.car_dealership_inventory_system.service.JwtService;
import com.incubyte.car_dealership_inventory_system.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private VehicleRequest createValidVehicleRequest() {
        return new VehicleRequest(
                "Toyota",
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );
    }

    // =========================================================================
    // POST /api/vehicles — SUCCESS TESTS
    // =========================================================================

    @Test
    @DisplayName("Should create a vehicle and return 201 when request is valid")
    void shouldCreateVehicleSuccessfully() throws Exception {
        VehicleRequest request = createValidVehicleRequest();

        UUID vehicleId = UUID.randomUUID();
        VehicleResponse response = new VehicleResponse(
                vehicleId,
                "Toyota",
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"))
                .andExpect(jsonPath("$.category").value("SEDAN"))
                .andExpect(jsonPath("$.price").value(28000.00))
                .andExpect(jsonPath("$.quantityInStock").value(10));

        verify(vehicleService).addVehicle(request);
    }

    @Test
    @DisplayName("Should create a vehicle with minimum valid fields")
    void shouldCreateVehicleWithMinimumFields() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Honda",
                "Civic",
                VehicleCategory.SEDAN,
                new BigDecimal("22000.00"),
                0
        );

        UUID vehicleId = UUID.randomUUID();
        VehicleResponse response = new VehicleResponse(
                vehicleId,
                "Honda",
                "Civic",
                VehicleCategory.SEDAN,
                new BigDecimal("22000.00"),
                0
        );

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.make").value("Honda"))
                .andExpect(jsonPath("$.model").value("Civic"))
                .andExpect(jsonPath("$.quantityInStock").value(0));

        verify(vehicleService).addVehicle(request);
    }

    @Test
    @DisplayName("Should create a vehicle with SUV category")
    void shouldCreateVehicleWithSuvCategory() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Ford",
                "Explorer",
                VehicleCategory.SUV,
                new BigDecimal("45000.00"),
                5
        );

        UUID vehicleId = UUID.randomUUID();
        VehicleResponse response = new VehicleResponse(
                vehicleId,
                "Ford",
                "Explorer",
                VehicleCategory.SUV,
                new BigDecimal("45000.00"),
                5
        );

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("SUV"));
    }

    @Test
    @DisplayName("Should create a vehicle with PICKUP_TRUCK category")
    void shouldCreateVehicleWithPickupTruckCategory() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Ford",
                "F-150",
                VehicleCategory.PICKUP_TRUCK,
                new BigDecimal("55000.00"),
                3
        );

        UUID vehicleId = UUID.randomUUID();
        VehicleResponse response = new VehicleResponse(
                vehicleId,
                "Ford",
                "F-150",
                VehicleCategory.PICKUP_TRUCK,
                new BigDecimal("55000.00"),
                3
        );

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("PICKUP_TRUCK"));
    }

    @Test
    @DisplayName("Should create a vehicle with large price and quantity")
    void shouldCreateVehicleWithLargeValues() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Porsche",
                "911",
                VehicleCategory.SPORTS_CAR,
                new BigDecimal("150000.00"),
                50
        );

        UUID vehicleId = UUID.randomUUID();
        VehicleResponse response = new VehicleResponse(
                vehicleId,
                "Porsche",
                "911",
                VehicleCategory.SPORTS_CAR,
                new BigDecimal("150000.00"),
                50
        );

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(150000.00))
                .andExpect(jsonPath("$.quantityInStock").value(50));
    }

    @Test
    @DisplayName("Should create a vehicle with fractional price")
    void shouldCreateVehicleWithFractionalPrice() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Tesla",
                "Model Y",
                VehicleCategory.ELECTRIC_SUV,
                new BigDecimal("49999.99"),
                15
        );

        UUID vehicleId = UUID.randomUUID();
        VehicleResponse response = new VehicleResponse(
                vehicleId,
                "Tesla",
                "Model Y",
                VehicleCategory.ELECTRIC_SUV,
                new BigDecimal("49999.99"),
                15
        );

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.price").value(49999.99));
    }

    // =========================================================================
    // POST /api/vehicles — VALIDATION FAILURE TESTS
    // =========================================================================

    @Test
    @DisplayName("Should return 400 when make is blank")
    void shouldReturnBadRequestWhenMakeIsBlank() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "",
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.make").value("Make is required"));
    }

    @Test
    @DisplayName("Should return 400 when model is blank")
    void shouldReturnBadRequestWhenModelIsBlank() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Toyota",
                "",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.model").value("Model is required"));
    }

    @Test
    @DisplayName("Should return 400 when category is null")
    void shouldReturnBadRequestWhenCategoryIsNull() throws Exception {
        String requestJson = """
                {
                    "make": "Toyota",
                    "model": "Camry",
                    "category": null,
                    "price": 28000.00,
                    "quantityInStock": 10
                }
                """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.category").value("Category is required"));
    }

    @Test
    @DisplayName("Should return 400 when category is invalid enum value")
    void shouldReturnBadRequestWhenCategoryIsInvalid() throws Exception {
        String requestJson = """
                {
                    "make": "Toyota",
                    "model": "Camry",
                    "category": "INVALID_CATEGORY",
                    "price": 28000.00,
                    "quantityInStock": 10
                }
                """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when price is null")
    void shouldReturnBadRequestWhenPriceIsNull() throws Exception {
        String requestJson = """
                {
                    "make": "Toyota",
                    "model": "Camry",
                    "category": "SEDAN",
                    "price": null,
                    "quantityInStock": 10
                }
                """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price is required"));
    }

    @Test
    @DisplayName("Should return 400 when price is zero")
    void shouldReturnBadRequestWhenPriceIsZero() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Toyota",
                "Camry",
                VehicleCategory.SEDAN,
                BigDecimal.ZERO,
                10
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price must be positive"));
    }

    @Test
    @DisplayName("Should return 400 when price is negative")
    void shouldReturnBadRequestWhenPriceIsNegative() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Toyota",
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("-100.00"),
                10
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price must be positive"));
    }

    @Test
    @DisplayName("Should return 400 when quantity is null")
    void shouldReturnBadRequestWhenQuantityIsNull() throws Exception {
        String requestJson = """
                {
                    "make": "Toyota",
                    "model": "Camry",
                    "category": "SEDAN",
                    "price": 28000.00,
                    "quantityInStock": null
                }
                """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantityInStock").value("Quantity is required"));
    }

    @Test
    @DisplayName("Should return 400 when quantity is negative")
    void shouldReturnBadRequestWhenQuantityIsNegative() throws Exception {
        VehicleRequest request = new VehicleRequest(
                "Toyota",
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                -1
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.quantityInStock").value("Quantity cannot be negative"));
    }

    @Test
    @DisplayName("Should return 400 when all required fields are missing")
    void shouldReturnBadRequestWhenAllFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.make").value("Make is required"))
                .andExpect(jsonPath("$.model").value("Model is required"))
                .andExpect(jsonPath("$.category").value("Category is required"))
                .andExpect(jsonPath("$.price").value("Price is required"))
                .andExpect(jsonPath("$.quantityInStock").value("Quantity is required"));
    }

    @Test
    @DisplayName("Should return 400 when request body is empty JSON array")
    void shouldReturnBadRequestWhenBodyIsJsonArray() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when request body is malformed JSON")
    void shouldReturnBadRequestWhenBodyIsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when price has too many decimal places")
    void shouldReturnBadRequestWhenPriceHasTooManyDecimals() throws Exception {
        String requestJson = """
                {
                    "make": "Toyota",
                    "model": "Camry",
                    "category": "SEDAN",
                    "price": 28000.123,
                    "quantityInStock": 10
                }
                """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.price").value("Price format invalid"));
    }

    @Test
    @DisplayName("Should return 400 when make exceeds maximum length")
    void shouldReturnBadRequestWhenMakeExceedsMaxLength() throws Exception {
        String longMake = "A".repeat(81);

        VehicleRequest request = new VehicleRequest(
                longMake,
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.make").value("Make must not exceed 80 characters"));
    }

    @Test
    @DisplayName("Should return 400 when model exceeds maximum length")
    void shouldReturnBadRequestWhenModelExceedsMaxLength() throws Exception {
        String longModel = "B".repeat(81);

        VehicleRequest request = new VehicleRequest(
                "Toyota",
                longModel,
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.model").value("Model must not exceed 80 characters"));
    }

    @Test
    @DisplayName("Should return 400 when Content-Type is not JSON")
    void shouldReturnBadRequestWhenContentTypeIsNotJson() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not json"))
                .andExpect(status().isUnsupportedMediaType());
    }

    // =========================================================================
    // POST /api/vehicles — BUSINESS EXCEPTION TESTS
    // =========================================================================

    @Test
    @DisplayName("Should return 409 when vehicle with same make, model already exists")
    void shouldReturnConflictWhenVehicleAlreadyExists() throws Exception {
        VehicleRequest request = createValidVehicleRequest();

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenThrow(new DuplicateVehicleException("Vehicle already exists with the same make, model, and category"));

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Vehicle already exists with the same make, model, and category"));
    }

    @Test
    @DisplayName("Should delegate to VehicleService when adding a valid vehicle")
    void shouldDelegateToVehicleService() throws Exception {
        VehicleRequest request = createValidVehicleRequest();

        UUID vehicleId = UUID.randomUUID();
        VehicleResponse response = new VehicleResponse(
                vehicleId,
                "Toyota",
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );

        when(vehicleService.addVehicle(any(VehicleRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(vehicleService).addVehicle(request);
    }

    @Test
    @DisplayName("Should create a vehicle with all valid category types")
    void shouldCreateVehicleWithAllCategoryTypes() throws Exception {
        for (VehicleCategory category : VehicleCategory.values()) {
            VehicleRequest request = new VehicleRequest(
                    "TestMake",
                    "TestModel",
                    category,
                    new BigDecimal("10000.00"),
                    1
            );

            UUID vehicleId = UUID.randomUUID();
            VehicleResponse response = new VehicleResponse(
                    vehicleId,
                    "TestMake",
                    "TestModel",
                    category,
                    new BigDecimal("10000.00"),
                    1
            );

            when(vehicleService.addVehicle(any(VehicleRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.category").value(category.name()));

            org.mockito.Mockito.reset(vehicleService);
        }
    }
}
