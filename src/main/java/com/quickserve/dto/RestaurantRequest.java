package com.quickserve.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

/**
 * Request DTO for creating/updating restaurants
 */
public record RestaurantRequest(
        @NotBlank(message = "Restaurant name is required") String name,
        @NotBlank(message = "Address is required") String address,
        @NotNull(message = "Latitude is required") Double latitude,
        @NotNull(message = "Longitude is required") Double longitude,
        @NotNull(message = "Owner ID is required") Long ownerId,
        String cuisine,
        String description,
        String imageUrl,
        LocalTime openingTime,
        LocalTime closingTime
) {}
