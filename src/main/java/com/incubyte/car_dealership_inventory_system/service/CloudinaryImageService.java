package com.incubyte.car_dealership_inventory_system.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryImageService implements ImageService {

    private final Cloudinary cloudinary;

    @Value("${vehicle.default-image:/images/default-vehicle.svg}")
    private String defaultImagePath;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "car-dealership/vehicles",
                            "resource_type", "image"
                    )
            );
            String url = result.get("secure_url").toString();
            log.info("Image uploaded successfully to Cloudinary: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.equals(defaultImagePath)) {
            return;
        }
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Image deleted successfully from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary: {}", imageUrl, e);
        }
    }

    private String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        int uploadIndex = -1;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals("upload")) {
                uploadIndex = i;
                break;
            }
        }
        if (uploadIndex == -1 || uploadIndex + 1 >= parts.length) {
            throw new RuntimeException("Invalid Cloudinary URL: " + imageUrl);
        }
        String pathWithVersionAndFile = String.join("/",
                java.util.Arrays.copyOfRange(parts, uploadIndex + 1, parts.length));
        int lastDotIndex = pathWithVersionAndFile.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return pathWithVersionAndFile.substring(0, lastDotIndex);
        }
        return pathWithVersionAndFile;
    }
}
