package com.incubyte.car_dealership_inventory_system.inventory;

import tools.jackson.databind.ObjectMapper;
import com.incubyte.car_dealership_inventory_system.controller.InventoryController;
import com.incubyte.car_dealership_inventory_system.dto.request.InventoryRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.VehicleResponse;
import com.incubyte.car_dealership_inventory_system.enums.VehicleCategory;
import com.incubyte.car_dealership_inventory_system.exception.GlobalExceptionHandler;
import com.incubyte.car_dealership_inventory_system.exception.InsufficientStockException;
import com.incubyte.car_dealership_inventory_system.exception.ResourceNotFoundException;
import com.incubyte.car_dealership_inventory_system.service.CustomUserDetailsService;
import com.incubyte.car_dealership_inventory_system.service.InventoryService;
import com.incubyte.car_dealership_inventory_system.service.JwtService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableMethodSecurity
@Import(GlobalExceptionHandler.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private VehicleResponse createVehicleResponse(UUID id, String make, String model,
                                                   VehicleCategory category, BigDecimal price,
                                                   Integer quantityInStock) {
        return new VehicleResponse(id, make, model, category, price, quantityInStock, "/images/default-vehicle.svg");
    }

    private InventoryRequest createInventoryRequest(Integer quantity) {
        return new InventoryRequest(quantity);
    }

    // =========================================================================
    // POST /api/v1/vehicles/:id/purchase
    // =========================================================================

    @Nested
    @DisplayName("POST /api/v1/vehicles/{id}/purchase")
    class PurchaseVehicle {

        @Test
        @WithMockUser
        @DisplayName("Should purchase vehicle and return 200 with updated stock")
        void shouldPurchaseVehicleSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(2);
            VehicleResponse response = createVehicleResponse(
                    id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 8);

            when(inventoryService.purchase(eq(id), any(InventoryRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.make").value("Toyota"))
                    .andExpect(jsonPath("$.model").value("Camry"))
                    .andExpect(jsonPath("$.quantityInStock").value(8));

            verify(inventoryService).purchase(eq(id), any(InventoryRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 404 when vehicle to purchase is not found")
        void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(1);

            when(inventoryService.purchase(eq(id), any(InventoryRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Vehicle not found with id: " + id));

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Vehicle not found with id: " + id));

            verify(inventoryService).purchase(eq(id), any(InventoryRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 409 when stock is insufficient for purchase")
        void shouldReturnConflictWhenInsufficientStock() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(100);

            when(inventoryService.purchase(eq(id), any(InventoryRequest.class)))
                    .thenThrow(new InsufficientStockException(
                            "Insufficient stock for vehicle Toyota Camry: requested 100, available 5"));

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value(
                            "Insufficient stock for vehicle Toyota Camry: requested 100, available 5"));

            verify(inventoryService).purchase(eq(id), any(InventoryRequest.class));
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when quantity is null")
        void shouldReturnBadRequestWhenQuantityIsNull() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {
                        "quantity": null
                    }
                    """;

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity is required"));

            verify(inventoryService, never()).purchase(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when quantity is zero")
        void shouldReturnBadRequestWhenQuantityIsZero() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(0);

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity must be at least 1"));

            verify(inventoryService, never()).purchase(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when quantity is negative")
        void shouldReturnBadRequestWhenQuantityIsNegative() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(-5);

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity must be at least 1"));

            verify(inventoryService, never()).purchase(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when request body is invalid JSON")
        void shouldReturnBadRequestWhenBodyIsInvalidJson() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when id is not a valid UUID")
        void shouldReturnBadRequestWhenIdIsNotValidUuid() throws Exception {
            InventoryRequest request = createInventoryRequest(1);

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("Should return 400 when quantity field is missing from body")
        void shouldReturnBadRequestWhenQuantityFieldIsMissing() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {}
                    """;

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity is required"));

            verify(inventoryService, never()).purchase(any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("Should call purchase service with correct parameters")
        void shouldCallPurchaseServiceWithCorrectParameters() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(3);
            VehicleResponse response = createVehicleResponse(
                    id, "Honda", "Civic", VehicleCategory.SEDAN,
                    new BigDecimal("25000.00"), 7);

            when(inventoryService.purchase(eq(id), any(InventoryRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/vehicles/{id}/purchase", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(inventoryService).purchase(eq(id), any(InventoryRequest.class));
        }
    }

    // =========================================================================
    // POST /api/v1/vehicles/:id/restock
    // =========================================================================

    @Nested
    @DisplayName("POST /api/v1/vehicles/{id}/restock")
    class RestockVehicle {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should restock vehicle and return 200 with updated stock")
        void shouldRestockVehicleSuccessfully() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(10);
            VehicleResponse response = createVehicleResponse(
                    id, "Toyota", "Camry", VehicleCategory.SEDAN,
                    new BigDecimal("28000.00"), 20);

            when(inventoryService.restock(eq(id), any(InventoryRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.quantityInStock").value(20));

            verify(inventoryService).restock(eq(id), any(InventoryRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 404 when vehicle to restock is not found")
        void shouldReturnNotFoundWhenVehicleDoesNotExist() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(5);

            when(inventoryService.restock(eq(id), any(InventoryRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Vehicle not found with id: " + id));

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Vehicle not found with id: " + id));

            verify(inventoryService).restock(eq(id), any(InventoryRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when quantity is null")
        void shouldReturnBadRequestWhenQuantityIsNull() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {
                        "quantity": null
                    }
                    """;

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity is required"));

            verify(inventoryService, never()).restock(any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when quantity is zero")
        void shouldReturnBadRequestWhenQuantityIsZero() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(0);

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity must be at least 1"));

            verify(inventoryService, never()).restock(any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when quantity is negative")
        void shouldReturnBadRequestWhenQuantityIsNegative() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(-3);

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity must be at least 1"));

            verify(inventoryService, never()).restock(any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when request body is invalid JSON")
        void shouldReturnBadRequestWhenBodyIsInvalidJson() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{invalid}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when id is not a valid UUID")
        void shouldReturnBadRequestWhenIdIsNotValidUuid() throws Exception {
            InventoryRequest request = createInventoryRequest(5);

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 400 when quantity field is missing from body")
        void shouldReturnBadRequestWhenQuantityFieldIsMissing() throws Exception {
            UUID id = UUID.randomUUID();
            String requestJson = """
                    {}
                    """;

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.details[?(@.field == 'quantity')].message").value("Quantity is required"));

            verify(inventoryService, never()).restock(any(), any());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("Should return 403 when non-admin user tries to restock")
        void shouldReturnForbiddenWhenNonAdminTriesToRestock() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(5);

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message").value("You do not have permission to perform this action"));

            verify(inventoryService, never()).restock(any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should call restock service with correct parameters")
        void shouldCallRestockServiceWithCorrectParameters() throws Exception {
            UUID id = UUID.randomUUID();
            InventoryRequest request = createInventoryRequest(15);
            VehicleResponse response = createVehicleResponse(
                    id, "Honda", "Civic", VehicleCategory.SEDAN,
                    new BigDecimal("25000.00"), 20);

            when(inventoryService.restock(eq(id), any(InventoryRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());

            verify(inventoryService).restock(eq(id), any(InventoryRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("Should return 415 when content type is not JSON")
        void shouldReturnUnsupportedMediaTypeWhenContentTypeIsNotJson() throws Exception {
            UUID id = UUID.randomUUID();

            mockMvc.perform(post("/api/v1/vehicles/{id}/restock", id)
                            .contentType(MediaType.TEXT_PLAIN)
                            .content("5"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }
}
