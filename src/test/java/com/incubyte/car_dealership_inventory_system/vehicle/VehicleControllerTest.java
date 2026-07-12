package com.incubyte.car_dealership_inventory_system.vehicle;

import tools.jackson.databind.ObjectMapper;
import com.incubyte.car_dealership_inventory_system.controller.VehicleController;
import com.incubyte.car_dealership_inventory_system.dto.request.VehicleRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import com.incubyte.car_dealership_inventory_system.exception.DuplicateVehicleException;
import com.incubyte.car_dealership_inventory_system.exception.GlobalExceptionHandler;
import com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException;
import com.incubyte.car_dealership_inventory_system.service.CustomUserDetailsService;
import com.incubyte.car_dealership_inventory_system.service.JwtService;
import com.incubyte.car_dealership_inventory_system.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableMethodSecurity
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

    @MockitoBean
    private com.incubyte.car_dealership_inventory_system.service.ImageService imageService;

    private VehicleRequest createValidVehicleRequest() {
        return new VehicleRequest(
                "Toyota",
                "Camry",
                VehicleCategory.SEDAN,
                new BigDecimal("28000.00"),
                10
        );
    }

    private VehicleResponse createVehicleResponse(UUID id, String make, String model,
                                                   VehicleCategory category, BigDecimal price,
                                                   Integer quantity) {
        return new VehicleResponse(id, make, model, category, price, quantity, "/images/default-vehicle.svg");
    }

    // =========================================================================
    // POST /api/vehicles
    // =========================================================================

    @Nested
    @DisplayName("POST /api/vehicles")
    class CreateVehicle {

        @Test
        @DisplayName("Should create a vehicle and return 201 when request is valid")
        void shouldCreateVehicleSuccessfully() throws Exception {
            VehicleRequest request = createValidVehicleRequest();
            UUID vehicleId = UUID.randomUUID();
            VehicleResponse response = createVehicleResponse(
                    vehicleId, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10
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
        @DisplayName("Should return 409 when vehicle already exists")
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
        @DisplayName("Should return 400 when make is blank")
        void shouldReturnBadRequestWhenMakeIsBlank() throws Exception {
            VehicleRequest request = new VehicleRequest(
                    "", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10
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
                    "Toyota", "", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10
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
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    BigDecimal.ZERO, 10
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
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("-100.00"), 10
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
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), -1
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
                    longMake, "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10
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
                    "Toyota", longModel, VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10
            );

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.model").value("Model must not exceed 80 characters"));
        }

        @Test
        @DisplayName("Should return 415 when Content-Type is not JSON")
        void shouldReturnUnsupportedMediaTypeWhenContentTypeIsNotJson() throws Exception {
            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("not json"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }

    // =========================================================================
    // GET /api/vehicles
    // =========================================================================

    @Nested
    @DisplayName("GET /api/vehicles")
    class GetAllVehicles {

        @Test
        @DisplayName("Should return 200 with empty list when no vehicles exist")
        void shouldReturnEmptyListWhenNoVehicles() throws Exception {
            when(vehicleService.getAllVehicles())
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            verify(vehicleService).getAllVehicles();
        }

        @Test
        @DisplayName("Should return 200 with list of all vehicles")
        void shouldReturnAllVehicles() throws Exception {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            List<VehicleResponse> vehicles = List.of(
                    createVehicleResponse(id1, "Toyota", "Camry", VehicleCategory.SEDAN,
                            new BigDecimal("28000.00"), 10),
                    createVehicleResponse(id2, "Honda", "Civic", VehicleCategory.SEDAN,
                            new BigDecimal("22000.00"), 5)
            );

            when(vehicleService.getAllVehicles())
                    .thenReturn(vehicles);

            mockMvc.perform(get("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(id1.toString()))
                    .andExpect(jsonPath("$[0].make").value("Toyota"))
                    .andExpect(jsonPath("$[0].model").value("Camry"))
                    .andExpect(jsonPath("$[1].id").value(id2.toString()))
                    .andExpect(jsonPath("$[1].make").value("Honda"))
                    .andExpect(jsonPath("$[1].model").value("Civic"));

            verify(vehicleService).getAllVehicles();
        }

        @Test
        @DisplayName("Should return vehicles with all response fields populated")
        void shouldReturnVehiclesWithAllFields() throws Exception {
            UUID id = UUID.randomUUID();
            List<VehicleResponse> vehicles = List.of(
                    createVehicleResponse(id, "Ford", "F-150", VehicleCategory.PICKUP_TRUCK,
                            new BigDecimal("55000.00"), 3)
            );

            when(vehicleService.getAllVehicles())
                    .thenReturn(vehicles);

            mockMvc.perform(get("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(id.toString()))
                    .andExpect(jsonPath("$[0].make").value("Ford"))
                    .andExpect(jsonPath("$[0].model").value("F-150"))
                    .andExpect(jsonPath("$[0].category").value("PICKUP_TRUCK"))
                    .andExpect(jsonPath("$[0].price").value(55000.00))
                    .andExpect(jsonPath("$[0].quantityInStock").value(3));
        }

        @Test
        @DisplayName("Should return vehicles of different categories")
        void shouldReturnVehiclesOfDifferentCategories() throws Exception {
            List<VehicleResponse> vehicles = List.of(
                    createVehicleResponse(UUID.randomUUID(), "Toyota", "Camry",
                            VehicleCategory.SEDAN, new BigDecimal("28000.00"), 10),
                    createVehicleResponse(UUID.randomUUID(), "Ford", "Explorer",
                            VehicleCategory.SUV, new BigDecimal("45000.00"), 5),
                    createVehicleResponse(UUID.randomUUID(), "Ford", "F-150",
                            VehicleCategory.PICKUP_TRUCK, new BigDecimal("55000.00"), 3),
                    createVehicleResponse(UUID.randomUUID(), "Tesla", "Model Y",
                            VehicleCategory.ELECTRIC_SUV, new BigDecimal("49999.99"), 15),
                    createVehicleResponse(UUID.randomUUID(), "Porsche", "911",
                            VehicleCategory.SPORTS_CAR, new BigDecimal("150000.00"), 2)
            );

            when(vehicleService.getAllVehicles())
                    .thenReturn(vehicles);

            mockMvc.perform(get("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(5))
                    .andExpect(jsonPath("$[0].category").value("SEDAN"))
                    .andExpect(jsonPath("$[1].category").value("SUV"))
                    .andExpect(jsonPath("$[2].category").value("PICKUP_TRUCK"))
                    .andExpect(jsonPath("$[3].category").value("ELECTRIC_SUV"))
                    .andExpect(jsonPath("$[4].category").value("SPORTS_CAR"));
        }

        @Test
        @DisplayName("Should return single vehicle in list")
        void shouldReturnSingleVehicleInList() throws Exception {
            UUID id = UUID.randomUUID();
            List<VehicleResponse> vehicles = List.of(
                    createVehicleResponse(id, "Honda", "Civic", VehicleCategory.SEDAN,
                            new BigDecimal("22000.00"), 5)
            );

            when(vehicleService.getAllVehicles())
                    .thenReturn(vehicles);

            mockMvc.perform(get("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].make").value("Honda"));
        }

        @Test
        @DisplayName("Should preserve order of vehicles in response")
        void shouldPreserveOrderOfVehicles() throws Exception {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            UUID id3 = UUID.randomUUID();
            List<VehicleResponse> vehicles = List.of(
                    createVehicleResponse(id1, "Alpha", "A1", VehicleCategory.SEDAN,
                            new BigDecimal("10000.00"), 1),
                    createVehicleResponse(id2, "Beta", "B1", VehicleCategory.SUV,
                            new BigDecimal("20000.00"), 2),
                    createVehicleResponse(id3, "Gamma", "G1", VehicleCategory.MINIVAN,
                            new BigDecimal("30000.00"), 3)
            );

            when(vehicleService.getAllVehicles())
                    .thenReturn(vehicles);

            mockMvc.perform(get("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].make").value("Alpha"))
                    .andExpect(jsonPath("$[1].make").value("Beta"))
                    .andExpect(jsonPath("$[2].make").value("Gamma"));
        }
    }

    // =========================================================================
    // GET /api/vehicles/:id
    // =========================================================================

    @Nested
    @DisplayName("GET /api/vehicles/{id}")
    class GetVehicleById {

        @Test
        @DisplayName("Should return 200 with vehicle when found")
        void shouldReturnVehicleWhenFound() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse response = createVehicleResponse(
                    id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10
            );

            when(vehicleService.getVehicleById(id))
                    .thenReturn(response);

            mockMvc.perform(get("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.make").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Camry"))
                    .andExpect(jsonPath("$.category").value("SEDAN"))
                    .andExpect(jsonPath("$.price").value(28000.00))
                    .andExpect(jsonPath("$.quantityInStock").value(10));

            verify(vehicleService).getVehicleById(id);
        }

        @Test
        @DisplayName("Should return 404 when vehicle not found")
        void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();

            when(vehicleService.getVehicleById(id))
                    .thenThrow(new ResourceNotFoundException("Vehicle not found with id: " + id));

            mockMvc.perform(get("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Vehicle not found with id: " + id));

            verify(vehicleService).getVehicleById(id);
        }

        @Test
        @DisplayName("Should return vehicle with SUV category")
        void shouldReturnVehicleWithSuvCategory() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse response = createVehicleResponse(
                    id, "Ford", "Explorer", VehicleCategory.SUV,
                    new BigDecimal("45000.00"), 5
            );

            when(vehicleService.getVehicleById(id))
                    .thenReturn(response);

            mockMvc.perform(get("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.category").value("SUV"));
        }

        @Test
        @DisplayName("Should return 400 when id is not a valid UUID")
        void shouldReturnBadRequestWhenIdIsNotValidUuid() throws Exception {
            mockMvc.perform(get("/api/vehicles/{id}", "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    // =========================================================================
    // PUT /api/vehicles/:id
    // =========================================================================

    @Nested
    @DisplayName("PUT /api/vehicles/{id}")
    class UpdateVehicle {

        @Test
        @DisplayName("Should update vehicle and return 200 when valid")
        void shouldUpdateVehicleSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("30000.00"), 15
            );

            VehicleResponse response = createVehicleResponse(
                    id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("30000.00"), 15
            );

            when(vehicleService.updateVehicle(eq(id), any(VehicleRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.make").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Camry"))
                    .andExpect(jsonPath("$.category").value("SEDAN"))
                    .andExpect(jsonPath("$.price").value(30000.00))
                    .andExpect(jsonPath("$.quantityInStock").value(15));

            verify(vehicleService).updateVehicle(id, request);
        }

        @Test
        @DisplayName("Should return 404 when vehicle to update is not found")
        void shouldReturnNotFoundWhenVehicleToUpdateDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = createValidVehicleRequest();

            when(vehicleService.updateVehicle(eq(id), any(VehicleRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Vehicle not found with id: " + id));

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Vehicle not found with id: " + id));

            verify(vehicleService).updateVehicle(id, request);
        }

        @Test
        @DisplayName("Should return 400 when make is blank on update")
        void shouldReturnBadRequestWhenMakeIsBlankOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("30000.00"), 15
            );

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.make").value("Make is required"));
        }

        @Test
        @DisplayName("Should return 400 when model is blank on update")
        void shouldReturnBadRequestWhenModelIsBlankOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "Toyota", "", VehicleCategory.SEDAN,
                    new BigDecimal("30000.00"), 15
            );

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.model").value("Model is required"));
        }

        @Test
        @DisplayName("Should return 400 when category is null on update")
        void shouldReturnBadRequestWhenCategoryIsNullOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {
                        "make": "Toyota",
                        "model": "Camry",
                        "category": null,
                        "price": 30000.00,
                        "quantityInStock": 15
                    }
                    """;

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.category").value("Category is required"));
        }

        @Test
        @DisplayName("Should return 400 when category is invalid enum on update")
        void shouldReturnBadRequestWhenCategoryIsInvalidOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {
                        "make": "Toyota",
                        "model": "Camry",
                        "category": "INVALID",
                        "price": 30000.00,
                        "quantityInStock": 15
                    }
                    """;

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price is null on update")
        void shouldReturnBadRequestWhenPriceIsNullOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {
                        "make": "Toyota",
                        "model": "Camry",
                        "category": "SEDAN",
                        "price": null,
                        "quantityInStock": 15
                    }
                    """;

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").value("Price is required"));
        }

        @Test
        @DisplayName("Should return 400 when price is zero on update")
        void shouldReturnBadRequestWhenPriceIsZeroOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    BigDecimal.ZERO, 15
            );

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").value("Price must be positive"));
        }

        @Test
        @DisplayName("Should return 400 when price is negative on update")
        void shouldReturnBadRequestWhenPriceIsNegativeOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("-5000.00"), 15
            );

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").value("Price must be positive"));
        }

        @Test
        @DisplayName("Should return 400 when quantity is null on update")
        void shouldReturnBadRequestWhenQuantityIsNullOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {
                        "make": "Toyota",
                        "model": "Camry",
                        "category": "SEDAN",
                        "price": 30000.00,
                        "quantityInStock": null
                    }
                    """;

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantityInStock").value("Quantity is required"));
        }

        @Test
        @DisplayName("Should return 400 when quantity is negative on update")
        void shouldReturnBadRequestWhenQuantityIsNegativeOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("30000.00"), -1
            );

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.quantityInStock").value("Quantity cannot be negative"));
        }

        @Test
        @DisplayName("Should return 400 when all fields are missing on update")
        void shouldReturnBadRequestWhenAllFieldsAreMissingOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(put("/api/vehicles/{id}", id)
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
        @DisplayName("Should return 400 when request body is malformed JSON on update")
        void shouldReturnBadRequestWhenBodyIsMalformedJsonOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid json}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when price has too many decimal places on update")
        void shouldReturnBadRequestWhenPriceHasTooManyDecimalsOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {
                        "make": "Toyota",
                        "model": "Camry",
                        "category": "SEDAN",
                        "price": 30000.123,
                        "quantityInStock": 15
                    }
                    """;

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.price").value("Price format invalid"));
        }

        @Test
        @DisplayName("Should return 400 when make exceeds maximum length on update")
        void shouldReturnBadRequestWhenMakeExceedsMaxLengthOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            String longMake = "A".repeat(81);

            VehicleRequest request = new VehicleRequest(
                    longMake, "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("30000.00"), 15
            );

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.make").value("Make must not exceed 80 characters"));
        }

        @Test
        @DisplayName("Should return 400 when model exceeds maximum length on update")
        void shouldReturnBadRequestWhenModelExceedsMaxLengthOnUpdate() throws Exception {
            UUID id = UUID.randomUUID();
            String longModel = "B".repeat(81);

            VehicleRequest request = new VehicleRequest(
                    "Toyota", longModel, VehicleCategory.SEDAN,
                    new BigDecimal("30000.00"), 15
            );

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.model").value("Model must not exceed 80 characters"));
        }

        @Test
        @DisplayName("Should return 400 when id is not a valid UUID")
        void shouldReturnBadRequestWhenIdIsNotValidUuid() throws Exception {
            VehicleRequest request = createValidVehicleRequest();

            mockMvc.perform(put("/api/vehicles/{id}", "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should update vehicle with changed category")
        void shouldUpdateVehicleWithChangedCategory() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "Ford", "Explorer", VehicleCategory.SUV,
                    new BigDecimal("45000.00"), 5
            );

            VehicleResponse response = createVehicleResponse(
                    id, "Ford", "Explorer", VehicleCategory.SUV,
                    new BigDecimal("45000.00"), 5
            );

            when(vehicleService.updateVehicle(eq(id), any(VehicleRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.category").value("SUV"))
                    .andExpect(jsonPath("$.make").value("Ford"));
        }

        @Test
        @DisplayName("Should update vehicle with zero quantity")
        void shouldUpdateVehicleWithZeroQuantity() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleRequest request = new VehicleRequest(
                    "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 0
            );

            VehicleResponse response = createVehicleResponse(
                    id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 0
            );

            when(vehicleService.updateVehicle(eq(id), any(VehicleRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/api/vehicles/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantityInStock").value(0));
        }
    }

    // =========================================================================
    // DELETE /api/vehicles/:id
    // =========================================================================

    @Nested
    @DisplayName("DELETE /api/vehicles/{id}")
    class DeleteVehicle {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should delete vehicle and return 204 when admin")
        void shouldDeleteVehicleWhenAdmin() throws Exception {
            UUID id = UUID.randomUUID();

            doNothing().when(vehicleService).deleteVehicle(id);

            mockMvc.perform(delete("/api/vehicles/{id}", id))
                    .andExpect(status().isNoContent())
                    .andExpect(header().doesNotExist("Content-Type"));

            verify(vehicleService).deleteVehicle(id);
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 403 when non-admin user tries to delete")
        void shouldReturnForbiddenWhenNonAdminTriesToDelete() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(delete("/api/vehicles/{id}", id))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value("Access denied"));

            verify(vehicleService, never()).deleteVehicle(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when vehicle to delete is not found")
        void shouldReturnNotFoundWhenVehicleToDeleteDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();

            doThrow(new ResourceNotFoundException("Vehicle not found with id: " + id))
                    .when(vehicleService).deleteVehicle(id);

            mockMvc.perform(delete("/api/vehicles/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Vehicle not found with id: " + id));

            verify(vehicleService).deleteVehicle(id);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when id is not a valid UUID")
        void shouldReturnBadRequestWhenIdIsNotValidUuid() throws Exception {
            mockMvc.perform(delete("/api/vehicles/{id}", "not-a-uuid"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should call service with correct vehicle id")
        void shouldCallServiceWithCorrectId() throws Exception {
            UUID id = UUID.randomUUID();

            doNothing().when(vehicleService).deleteVehicle(id);

            mockMvc.perform(delete("/api/vehicles/{id}", id))
                    .andExpect(status().isNoContent());

            verify(vehicleService).deleteVehicle(id);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should not return response body on successful delete")
        void shouldNotReturnResponseBodyOnDelete() throws Exception {
            UUID id = UUID.randomUUID();

            doNothing().when(vehicleService).deleteVehicle(id);

            mockMvc.perform(delete("/api/vehicles/{id}", id))
                    .andExpect(status().isNoContent())
                    .andExpect(header().doesNotExist("Content-Type"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 with correct error message for different vehicle id")
        void shouldReturn404WithCorrectMessageForDifferentId() throws Exception {
            UUID id = UUID.randomUUID();

            doThrow(new ResourceNotFoundException("Vehicle not found with id: " + id))
                    .when(vehicleService).deleteVehicle(id);

            mockMvc.perform(delete("/api/vehicles/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Vehicle not found with id: " + id));
        }
    }

    // =========================================================================
    // GET /api/vehicles/search
    // =========================================================================

    @Nested
    @DisplayName("GET /api/vehicles/search")
    class SearchVehicles {

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with matching vehicles when searching by make")
        void shouldReturnVehiclesWhenSearchingByMake() throws Exception {
            VehicleResponse vehicle1 = createVehicleResponse(
                    UUID.randomUUID(), "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 5);
            VehicleResponse vehicle2 = createVehicleResponse(
                    UUID.randomUUID(), "Toyota", "Corolla", VehicleCategory.HATCHBACK,
                    new BigDecimal("22000.00"), 8);

            when(vehicleService.searchVehicles(eq("Toyota"), eq(null), eq(null),
                    eq(null), eq(null)))
                    .thenReturn(List.of(vehicle1, vehicle2));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("make", "Toyota"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].make").value("Toyota"))
                    .andExpect(jsonPath("$[1].model").value("Corolla"));

            verify(vehicleService).searchVehicles(eq("Toyota"), eq(null), eq(null),
                    eq(null), eq(null));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with matching vehicles when searching by model")
        void shouldReturnVehiclesWhenSearchingByModel() throws Exception {
            VehicleResponse vehicle = createVehicleResponse(
                    UUID.randomUUID(), "Honda", "Civic", VehicleCategory.SEDAN,
                    new BigDecimal("25000.00"), 3);

            when(vehicleService.searchVehicles(eq(null), eq("Civic"), eq(null),
                    eq(null), eq(null)))
                    .thenReturn(List.of(vehicle));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("model", "Civic"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].model").value("Civic"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with matching vehicles when searching by category")
        void shouldReturnVehiclesWhenSearchingByCategory() throws Exception {
            VehicleResponse suv1 = createVehicleResponse(
                    UUID.randomUUID(), "Toyota", "RAV4", VehicleCategory.SUV,
                    new BigDecimal("35000.00"), 4);
            VehicleResponse suv2 = createVehicleResponse(
                    UUID.randomUUID(), "Honda", "CR-V", VehicleCategory.SUV,
                    new BigDecimal("32000.00"), 6);

            when(vehicleService.searchVehicles(eq(null), eq(null), eq(VehicleCategory.SUV),
                    eq(null), eq(null)))
                    .thenReturn(List.of(suv1, suv2));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("category", "SUV"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].category").value("SUV"))
                    .andExpect(jsonPath("$[1].category").value("SUV"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with matching vehicles when searching by minPrice")
        void shouldReturnVehiclesWhenSearchingByMinPrice() throws Exception {
            VehicleResponse vehicle = createVehicleResponse(
                    UUID.randomUUID(), "BMW", "X5", VehicleCategory.SUV,
                    new BigDecimal("65000.00"), 2);

            when(vehicleService.searchVehicles(eq(null), eq(null), eq(null),
                    eq(new BigDecimal("50000.00")), eq(null)))
                    .thenReturn(List.of(vehicle));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("minPrice", "50000.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].make").value("BMW"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with matching vehicles when searching by maxPrice")
        void shouldReturnVehiclesWhenSearchingByMaxPrice() throws Exception {
            VehicleResponse vehicle = createVehicleResponse(
                    UUID.randomUUID(), "Honda", "Civic", VehicleCategory.SEDAN,
                    new BigDecimal("25000.00"), 7);

            when(vehicleService.searchVehicles(eq(null), eq(null), eq(null),
                    eq(null), eq(new BigDecimal("30000.00"))))
                    .thenReturn(List.of(vehicle));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("maxPrice", "30000.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].model").value("Civic"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with matching vehicles when searching by price range")
        void shouldReturnVehiclesWhenSearchingByPriceRange() throws Exception {
            VehicleResponse vehicle1 = createVehicleResponse(
                    UUID.randomUUID(), "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 5);
            VehicleResponse vehicle2 = createVehicleResponse(
                    UUID.randomUUID(), "Honda", "Accord", VehicleCategory.SEDAN,
                    new BigDecimal("32000.00"), 3);

            when(vehicleService.searchVehicles(eq(null), eq(null), eq(null),
                    eq(new BigDecimal("25000.00")), eq(new BigDecimal("35000.00"))))
                    .thenReturn(List.of(vehicle1, vehicle2));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("minPrice", "25000.00")
                            .param("maxPrice", "35000.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with matching vehicles when searching with multiple filters")
        void shouldReturnVehiclesWhenSearchingWithMultipleFilters() throws Exception {
            VehicleResponse vehicle = createVehicleResponse(
                    UUID.randomUUID(), "Toyota", "RAV4", VehicleCategory.SUV,
                    new BigDecimal("35000.00"), 4);

            when(vehicleService.searchVehicles(eq("Toyota"), eq(null), eq(VehicleCategory.SUV),
                    eq(new BigDecimal("30000.00")), eq(new BigDecimal("40000.00"))))
                    .thenReturn(List.of(vehicle));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("make", "Toyota")
                            .param("category", "SUV")
                            .param("minPrice", "30000.00")
                            .param("maxPrice", "40000.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].make").value("Toyota"))
                    .andExpect(jsonPath("$[0].model").value("RAV4"))
                    .andExpect(jsonPath("$[0].category").value("SUV"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with empty list when no vehicles match")
        void shouldReturnEmptyListWhenNoVehiclesMatch() throws Exception {
            when(vehicleService.searchVehicles(eq("Ferrari"), eq(null), eq(null),
                    eq(null), eq(null)))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/vehicles/search")
                            .param("make", "Ferrari"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 with all vehicles when no search params provided")
        void shouldReturnAllVehiclesWhenNoSearchParamsProvided() throws Exception {
            VehicleResponse vehicle1 = createVehicleResponse(
                    UUID.randomUUID(), "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 5);
            VehicleResponse vehicle2 = createVehicleResponse(
                    UUID.randomUUID(), "Honda", "Civic", VehicleCategory.HATCHBACK,
                    new BigDecimal("22000.00"), 8);

            when(vehicleService.searchVehicles(eq(null), eq(null), eq(null),
                    eq(null), eq(null)))
                    .thenReturn(List.of(vehicle1, vehicle2));

            mockMvc.perform(get("/api/vehicles/search"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @WithMockUser
        @DisplayName("Should call service with correct parameters")
        void shouldCallServiceWithCorrectParameters() throws Exception {
            when(vehicleService.searchVehicles(any(), any(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/vehicles/search")
                            .param("make", "Toyota")
                            .param("model", "Camry")
                            .param("category", "SEDAN")
                            .param("minPrice", "20000.00")
                            .param("maxPrice", "30000.00"))
                    .andExpect(status().isOk());

            verify(vehicleService).searchVehicles(
                    eq("Toyota"), eq("Camry"), eq(VehicleCategory.SEDAN),
                    eq(new BigDecimal("20000.00")), eq(new BigDecimal("30000.00")));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return empty list when minPrice is greater than maxPrice")
        void shouldReturnEmptyListWhenMinPriceExceedsMaxPrice() throws Exception {
            when(vehicleService.searchVehicles(eq(null), eq(null), eq(null),
                    eq(new BigDecimal("50000.00")), eq(new BigDecimal("10000.00"))))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/vehicles/search")
                            .param("minPrice", "50000.00")
                            .param("maxPrice", "10000.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return empty list when maxPrice is negative and no vehicles match")
        void shouldReturnEmptyListWhenMaxPriceIsNegative() throws Exception {
            when(vehicleService.searchVehicles(eq(null), eq(null), eq(null),
                    eq(null), eq(new BigDecimal("-500.00"))))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/vehicles/search")
                            .param("maxPrice", "-500.00"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when minPrice is not a valid number")
        void shouldReturnBadRequestWhenMinPriceIsNotValidNumber() throws Exception {
            mockMvc.perform(get("/api/vehicles/search")
                            .param("minPrice", "abc"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when category is invalid enum value")
        void shouldReturnBadRequestWhenCategoryIsInvalidEnumValue() throws Exception {
            mockMvc.perform(get("/api/vehicles/search")
                            .param("category", "INVALID_CATEGORY"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return response body with correct structure")
        void shouldReturnResponseBodyWithCorrectStructure() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse vehicle = createVehicleResponse(
                    id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 5);

            when(vehicleService.searchVehicles(eq("Toyota"), eq(null), eq(null),
                    eq(null), eq(null)))
                    .thenReturn(List.of(vehicle));

            mockMvc.perform(get("/api/vehicles/search")
                            .param("make", "Toyota"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].id").value(id.toString()))
                    .andExpect(jsonPath("$[0].make").value("Toyota"))
                    .andExpect(jsonPath("$[0].model").value("Camry"))
                    .andExpect(jsonPath("$[0].category").value("SEDAN"))
                    .andExpect(jsonPath("$[0].price").value(28000.00))
                    .andExpect(jsonPath("$[0].quantityInStock").value(5));
        }
    }

    // =========================================================================
    // POST /api/vehicles/:id/image
    // =========================================================================

    @Nested
    @DisplayName("POST /api/vehicles/{id}/image")
    class UploadVehicleImage {

        @Test
        @WithMockUser
        @DisplayName("Should upload image and return 200 with updated vehicle")
        void shouldUploadImageSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse response = createVehicleResponse(
                    id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10);
            response = new VehicleResponse(id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10,
                    "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test-image.jpg");

            when(vehicleService.uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class)))
                    .thenReturn(response);

            byte[] imageContent = "fake-image-content".getBytes();
            mockMvc.perform(multipart("/api/vehicles/{id}/image", id)
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "file", "test-image.jpg", "image/jpeg", imageContent)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.imageUrl").value(
                            "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test-image.jpg"));

            verify(vehicleService).uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when vehicle to upload image is not found")
        void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();

            when(vehicleService.uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class)))
                    .thenThrow(new ResourceNotFoundException("Vehicle not found with id: " + id));

            byte[] imageContent = "fake-image-content".getBytes();
            mockMvc.perform(multipart("/api/vehicles/{id}/image", id)
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "file", "test-image.jpg", "image/jpeg", imageContent)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("Vehicle not found with id: " + id));

            verify(vehicleService).uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when no file is provided")
        void shouldReturnBadRequestWhenNoFileProvided() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(multipart("/api/vehicles/{id}/image", id))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when id is not a valid UUID")
        void shouldReturnBadRequestWhenIdIsNotValidUuid() throws Exception {
            byte[] imageContent = "fake-image-content".getBytes();
            mockMvc.perform(multipart("/api/vehicles/{id}/image", "not-a-uuid")
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "file", "test-image.jpg", "image/jpeg", imageContent)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 when uploading PNG image")
        void shouldUploadPngImageSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse response = new VehicleResponse(id, "Honda", "Civic", VehicleCategory.SEDAN,
                    new BigDecimal("25000.00"), 5,
                    "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test-image.png");

            when(vehicleService.uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class)))
                    .thenReturn(response);

            byte[] imageContent = "fake-png-content".getBytes();
            mockMvc.perform(multipart("/api/vehicles/{id}/image", id)
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "file", "test-image.png", "image/png", imageContent)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imageUrl").value(
                            "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test-image.png"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 200 when uploading WebP image")
        void shouldUploadWebPImageSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse response = new VehicleResponse(id, "BMW", "X5", VehicleCategory.SUV,
                    new BigDecimal("65000.00"), 3,
                    "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test-image.webp");

            when(vehicleService.uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class)))
                    .thenReturn(response);

            byte[] imageContent = "fake-webp-content".getBytes();
            mockMvc.perform(multipart("/api/vehicles/{id}/image", id)
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "file", "test-image.webp", "image/webp", imageContent)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imageUrl").value(
                            "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test-image.webp"));
        }

        @Test
        @WithMockUser
        @DisplayName("Should call upload service with correct parameters")
        void shouldCallUploadServiceWithCorrectParameters() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse response = new VehicleResponse(id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10,
                    "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test-image.jpg");

            when(vehicleService.uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class)))
                    .thenReturn(response);

            byte[] imageContent = "fake-image-content".getBytes();
            mockMvc.perform(multipart("/api/vehicles/{id}/image", id)
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "file", "test-image.jpg", "image/jpeg", imageContent)))
                    .andExpect(status().isOk());

            verify(vehicleService).uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should update existing image URL when uploading new image")
        void shouldUpdateExistingImageUrlWhenUploadingNewImage() throws Exception {
            UUID id = UUID.randomUUID();
            VehicleResponse response = new VehicleResponse(id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 10,
                    "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/new-image.jpg");

            when(vehicleService.uploadVehicleImage(eq(id), any(org.springframework.web.multipart.MultipartFile.class)))
                    .thenReturn(response);

            byte[] imageContent = "new-image-content".getBytes();
            mockMvc.perform(multipart("/api/vehicles/{id}/image", id)
                            .file(new org.springframework.mock.web.MockMultipartFile(
                                    "file", "new-image.jpg", "image/jpeg", imageContent)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.imageUrl").value(
                            "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/new-image.jpg"));
        }
    }
}
