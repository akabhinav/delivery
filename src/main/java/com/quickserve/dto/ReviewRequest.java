package com.quickserve.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating reviews
 */
public record ReviewRequest(
        @NotNull(message = "Order ID is required") Long orderId,
        @NotNull(message = "Customer ID is required") Long customerId,
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5") Integer restaurantRating,
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating must be at most 5") Integer deliveryRating,
        String comment
) {}
