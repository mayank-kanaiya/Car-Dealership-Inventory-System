package com.incubyte.car_dealership_inventory_system.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implements {@link ImageService} backed by Cloudinary. Uploads are placed in
 * the {@code car-dealership/vehicles} folder, and deletions are skipped for
 * default placeholder images that live outside Cloudinary.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryImageService implements ImageService {

    private final Cloudinary cloudinary;

    @Value("${vehicle.default-image:/images/default-vehicle.svg}")
    private String defaultImagePath;

    @Value("${cloudinary.folder:car-dealership/vehicles}")
    private String uploadFolder;

    @Value("${cloudinary.allowed-formats:image/jpeg,image/png,image/webp,image/gif}")
    private String allowedFormatsCsv;

    @Value("${cloudinary.max-file-size:5242880}")
    private long maxFileSize;

    /**
     * Uploads the given image to the Cloudinary {@code car-dealership/vehicles}
     * folder and returns the secure CDN URL.
     *
     * @param file the multipart image to upload
     * @return the Cloudinary secure URL of the uploaded asset
     * @throws RuntimeException if the upload fails (e.g. network error, size limit)
     */
    @Override
    public String uploadImage(MultipartFile file) {
        Set<String> allowedTypes = Arrays.stream(allowedFormatsCsv.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("File size exceeds maximum allowed: " + maxFileSize + " bytes");
        }
        if (!allowedTypes.contains(file.getContentType())) {
            throw new RuntimeException("File type not allowed: " + file.getContentType()
                    + ". Allowed: " + allowedFormatsCsv);
        }

        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", uploadFolder,
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

    /**
     * Deletes the image from Cloudinary identified by the given URL.
     * No-op if the URL is null or points to the default placeholder image,
     * since those are not Cloudinary-managed assets.
     *
     * @param imageUrl the full Cloudinary URL of the image to delete
     */
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

    /**
     * Parses a Cloudinary URL to extract the public ID used by the delete API.
     * Expected format:
     * {@code https://res.cloudinary.com/<cloud>/image/upload/v1234567890/car-dealership/vehicles/file.jpg}
     * The method locates the {@code upload} segment, strips the version prefix
     * and file extension, and returns the remaining path as the public ID
     * (e.g. {@code v1234567890/car-dealership/vehicles/file}).
     *
     * @param imageUrl the full Cloudinary URL
     * @return the public ID suitable for {@code cloudinary.uploader().destroy()}
     * @throws RuntimeException if the URL does not match the expected Cloudinary format
     */
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
