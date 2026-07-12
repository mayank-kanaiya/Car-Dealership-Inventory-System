package com.incubyte.car_dealership_inventory_system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.incubyte.car_dealership_inventory_system.exception.ExpiredTokenException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String dummySecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
        long oneHourInMillis = 1000 * 60 * 60;

        jwtService = new JwtService(dummySecret, oneHourInMillis);
    }

    @Test
    @DisplayName("Should generate a valid JWT token format for a given email")
    void shouldGenerateValidToken() {
        String email = "admin@dealership.com";

        String token = jwtService.generateToken(email);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should successfully extract the correct email (subject) from a valid token")
    void shouldExtractEmailFromToken() {
        String expectedEmail = "user@dealership.com";
        String token = jwtService.generateToken(expectedEmail);

        String extractedEmail = jwtService.extractEmail(token);

        assertThat(extractedEmail).isEqualTo(expectedEmail);
    }

    @Test
    @DisplayName("Should return true when validating a correctly signed, unexpired token for the matching user")
    void shouldValidateTokenSuccessfully() {
        String email = "test@dealership.com";
        String token = jwtService.generateToken(email);

        boolean isValid = jwtService.isValidToken(token, email);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should return false when validating a token against a different user's email")
    void shouldFailValidationForDifferentUser() {
        String actualUser = "userA@dealership.com";
        String token = jwtService.generateToken(actualUser);

        String differentUser = "userB@dealership.com";
        boolean isValid = jwtService.isValidToken(token, differentUser);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should throw an ExpiredTokenException when attempting to validate an expired token")
    void shouldFailWhenTokenIsExpired() throws InterruptedException {
        JwtService fastExpiringJwtService = new JwtService("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970", 1);
        String email = "expired@dealership.com";
        String token = fastExpiringJwtService.generateToken(email);

        Thread.sleep(10);

        assertThatThrownBy(() -> fastExpiringJwtService.isValidToken(token, email))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessageContaining("Token has expired");
    }
}
