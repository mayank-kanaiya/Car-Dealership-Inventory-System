package com.incubyte.car_dealership_inventory_system.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Abstraction over image storage, decoupling the rest of the application
 * from the specifics of any particular provider (e.g. Cloudinary, S3).
 * Implementations handle upload, URL retrieval, and deletion.
 */
public interface ImageService {

    /**
     * Uploads the provided image file to the configured storage backend.
     *
     * @param file the multipart image to upload
     * @return the publicly accessible URL of the uploaded image
     * @throws RuntimeException if the upload fails
     */
    String uploadImage(MultipartFile file);

    /**
     * Deletes the image identified by its URL from the storage backend.
     * Implementations should silently handle URLs that do not correspond
     * to removable assets (e.g. default placeholders).
     *
     * @param imageUrl the full URL of the image to delete
     */
    void deleteImage(String imageUrl);
}
