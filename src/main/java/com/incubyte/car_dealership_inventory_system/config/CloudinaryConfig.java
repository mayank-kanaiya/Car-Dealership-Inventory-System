package com.incubyte.car_dealership_inventory_system.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Map;

/**
 * Creates the {@link Cloudinary} client bean from application properties.
 *
 * <p>The cloud name, API key, and API secret are read from
 * {@code cloudinary.cloud-name}, {@code cloudinary.api-key}, and
 * {@code cloudinary.api-secret} respectively.</p>
 */
@org.springframework.context.annotation.Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
