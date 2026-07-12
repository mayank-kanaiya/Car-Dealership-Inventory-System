package com.incubyte.car_dealership_inventory_system.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Wraps paginated results with metadata (page number, total elements, total pages).
 * Used to decouple API pagination from Spring Data's Page type.
 *
 * @param <T> type of elements in the current page
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    /**
     * Factory method that converts a Spring Data {@link Page} into a {@link PagedResponse}.
     */
    public static <T> PagedResponse<T> of(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
