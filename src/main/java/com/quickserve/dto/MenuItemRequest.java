package com.quickserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for creating/updating menu items
 */
public record MenuItemRequest(
        @NotNull(message = "Restaurant ID is required") Long restaurantId,
        @NotBlank(message = "Item name is required") String name,
        String description,
        @Positive(message = "Price must be positive") Double price,
        String category,
        String imageUrl,
        Boolean isVegetarian,
        Boolean isAvailable
) {}
