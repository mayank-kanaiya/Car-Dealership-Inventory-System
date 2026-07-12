package com.incubyte.car_dealership_inventory_system.auth;

import tools.jackson.databind.ObjectMapper;
import com.incubyte.car_dealership_inventory_system.controller.AuthController;
import com.incubyte.car_dealership_inventory_system.dto.request.AuthenticationRequest;
import com.incubyte.car_dealership_inventory_system.dto.request.RegistrationRequest;
import com.incubyte.car_dealership_inventory_system.dto.response.AuthenticationResponse;
import com.incubyte.car_dealership_inventory_system.exception.GlobalExceptionHandler;
import com.incubyte.car_dealership_inventory_system.exception.UserAlreadyExistsException;
import com.incubyte.car_dealership_inventory_system.service.AuthService;
import com.incubyte.car_dealership_inventory_system.service.CustomUserDetailsService;
import com.incubyte.car_dealership_inventory_system.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    // =========================================================================
    // REGISTRATION TESTS
    // =========================================================================

    @Test
    @DisplayName("Should register a user when the request is valid")
    void shouldRegisterUserSuccessfully() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John Doe",
                "john.doe@dealership.com",
                "password123"
        );

        when(authService.registerUser(any(RegistrationRequest.class)))
                .thenReturn(new AuthenticationResponse("User registered successfully", "jwt-token"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(authService).registerUser(request);
    }

    @Test
    @DisplayName("Should return 409 when the email is already registered")
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John Doe",
                "existing@dealership.com",
                "password123"
        );

        when(authService.registerUser(any(RegistrationRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Email already in use"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }

    @Test
    @DisplayName("Should return 400 when full name is blank")
    void shouldReturnBadRequestWhenFullNameIsBlank() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "",
                "john.doe@dealership.com",
                "password123"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0].field").value("fullName"))
                .andExpect(jsonPath("$.details[0].message").value("Full Name is required"));
    }

    @Test
    @DisplayName("Should return 400 when email is blank")
    void shouldReturnBadRequestWhenEmailIsBlank() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John Doe",
                "",
                "password123"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'email')].message").value("Email is required"));
    }

    @Test
    @DisplayName("Should return 400 when email format is invalid")
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John Doe",
                "not-an-email",
                "password123"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'email')].message").value("Email format must be valid"));
    }

    @Test
    @DisplayName("Should return 400 when password is blank")
    void shouldReturnBadRequestWhenPasswordIsBlank() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John Doe",
                "john.doe@dealership.com",
                null
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'password')].message").value("Password is required"));
    }

    @Test
    @DisplayName("Should return 400 when password is shorter than 8 characters")
    void shouldReturnBadRequestWhenPasswordIsTooShort() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John Doe",
                "john.doe@dealership.com",
                "short1"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'password')].message").value("Password must be at least 8 characters long"));
    }

    @Test
    @DisplayName("Should return 400 when required fields are missing from the payload")
    void shouldReturnBadRequestWhenFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'fullName')].message").value("Full Name is required"))
                .andExpect(jsonPath("$.details[?(@.field == 'email')].message").value("Email is required"))
                .andExpect(jsonPath("$.details[?(@.field == 'password')].message").value("Password is required"));
    }

    // =========================================================================
    // LOGIN TESTS
    // =========================================================================

    @Test
    @DisplayName("Should login a user and return a token when credentials are valid")
    void shouldLoginSuccessfully() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
                "john.doe@dealership.com",
                "password123"
        );

        when(authService.login(any(AuthenticationRequest.class)))
                .thenReturn(new AuthenticationResponse("Login successful", "valid-jwt-token"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("valid-jwt-token"));

        verify(authService).login(request);
    }

    @Test
    @DisplayName("Should return 401 Unauthorized when credentials are invalid")
    void shouldReturnUnauthorizedForBadCredentials() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
                "john.doe@dealership.com",
                "wrongpassword"
        );

        when(authService.login(any(AuthenticationRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when login email is missing")
    void shouldReturnBadRequestWhenLoginEmailIsMissing() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
                "",
                "password123"
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'email')].message").value("Email is required"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when login password is missing")
    void shouldReturnBadRequestWhenLoginPasswordIsMissing() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
                "john.doe@dealership.com",
                ""
        );

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[?(@.field == 'password')].message").value("Password is required"));
    }
}
