package com.incubyte.car_dealership_inventory_system.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CloudinaryImageServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private CloudinaryImageService cloudinaryImageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cloudinaryImageService, "defaultImagePath", "/images/default-vehicle.svg");
    }

    // =========================================================================
    // uploadImage
    // =========================================================================

    @Test
    @DisplayName("Should upload image and return URL on success")
    void shouldUploadImageAndReturnUrl() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "image-content".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url",
                        "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test.jpg"));

        String url = cloudinaryImageService.uploadImage(file);

        assertNotNull(url);
        assertEquals("https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test.jpg", url);
        verify(uploader).upload(any(byte[].class), any(Map.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when upload fails with IOException")
    void shouldThrowWhenUploadFails() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", "image-content".getBytes());

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenThrow(new IOException("Upload failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> cloudinaryImageService.uploadImage(file)
        );

        assertEquals("Failed to upload image: Upload failed", exception.getMessage());
    }

    // =========================================================================
    // deleteImage
    // =========================================================================

    @Test
    @DisplayName("Should delete image from Cloudinary when URL is valid")
    void shouldDeleteImageFromCloudinary() throws Exception {
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test.jpg";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(any(String.class), any(Map.class)))
                .thenReturn(Map.of("result", "ok"));

        cloudinaryImageService.deleteImage(imageUrl);

        verify(uploader).destroy("car-dealership/vehicles/test", Map.of());
    }

    @Test
    @DisplayName("Should not delete when imageUrl is null")
    void shouldNotDeleteWhenImageUrlIsNull() throws Exception {
        cloudinaryImageService.deleteImage(null);

        verify(cloudinary, never()).uploader();
    }

    @Test
    @DisplayName("Should not delete when imageUrl is the default image")
    void shouldNotDeleteWhenImageUrlIsDefault() throws Exception {
        cloudinaryImageService.deleteImage("/images/default-vehicle.svg");

        verify(cloudinary, never()).uploader();
    }

    @Test
    @DisplayName("Should handle IOException during delete gracefully")
    void shouldHandleIOExceptionDuringDeleteGracefully() throws Exception {
        String imageUrl = "https://res.cloudinary.com/demo/image/upload/car-dealership/vehicles/test.jpg";

        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.destroy(any(String.class), any(Map.class)))
                .thenThrow(new IOException("Delete failed"));

        cloudinaryImageService.deleteImage(imageUrl);

        verify(uploader).destroy("car-dealership/vehicles/test", Map.of());
    }
}
