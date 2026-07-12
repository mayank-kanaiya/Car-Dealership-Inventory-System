package com.incubyte.car_dealership_inventory_system.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    // The class we are testing. It does not exist yet!
    private JwtService jwtService;

    // @BeforeEach executes the annotated method before every single test in the class. Used for setting up common test state[cite: 43, 44].
    @BeforeEach
    void setUp() {
        // We initialize it with a dummy secret key and expiration time for testing.
        String dummySecret = "this-is-a-very-secure-secret-key-for-testing-purposes-only";
        long oneHourInMillis = 1000 * 60 * 60;
        
        jwtService = new JwtService(dummySecret, oneHourInMillis);
    }

    // @Test marks a standard method as a test case[cite: 41]. 
    // @DisplayName allows you to give your test a highly readable, descriptive name[cite: 42].
    @Test
    @DisplayName("Should generate a valid JWT token format for a given email")
    void shouldGenerateValidToken() {
        // Arrange
        String email = "admin@dealership.com";
        
        // Act
        String token = jwtService.generateToken(email);

        // Assert
        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        // A standard JWT has 3 parts separated by dots (Header.Payload.Signature)
        assertThat(token.split("\\.")).hasSize(3); 
    }

    @Test
    @DisplayName("Should successfully extract the correct email (subject) from a valid token")
    void shouldExtractEmailFromToken() {
        // Arrange
        String expectedEmail = "user@dealership.com";
        String token = jwtService.generateToken(expectedEmail);

        // Act
        String extractedEmail = jwtService.extractEmail(token);

        // Assert
        assertThat(extractedEmail).isEqualTo(expectedEmail);
    }

    @Test
    @DisplayName("Should return true when validating a correctly signed, unexpired token for the matching user")
    void shouldValidateTokenSuccessfully() {
        // Arrange
        String email = "test@dealership.com";
        String token = jwtService.generateToken(email);

        // Act
        boolean isValid = jwtService.isValidToken(token, email);

        // Assert
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false when validating a token against a different user's email")
    void shouldFailValidationForDifferentUser() {
        // Arrange
        String actualUser = "userA@dealership.com";
        String token = jwtService.generateToken(actualUser);
        
        // Act
        String differentUser = "userB@dealership.com";
        boolean isValid = jwtService.isValidToken(token, differentUser);
        
        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should throw an ExpiredTokenException when attempting to validate an expired token")
    void shouldFailWhenTokenIsExpired() throws InterruptedException {
        // Arrange: Create a JwtService with a 1-millisecond expiration time for this specific test
        JwtService fastExpiringJwtService = new JwtService("this-is-a-very-secure-secret-key-for-testing-purposes-only", 1);
        String email = "expired@dealership.com";
        String token = fastExpiringJwtService.generateToken(email);

        // Act: Wait for 10 milliseconds to ensure the token expires
        Thread.sleep(10);

        // Assert: Write the test, watch it fail, and implement the exception-handling logic[cite: 113].
        assertThatThrownBy(() -> fastExpiringJwtService.isValidToken(token, email))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessageContaining("Token has expired");
    }
}