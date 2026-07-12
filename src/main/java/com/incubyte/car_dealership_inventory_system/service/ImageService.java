package com.incubyte.car_dealership_inventory_system.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile file);
    void deleteImage(String imageUrl);
}
