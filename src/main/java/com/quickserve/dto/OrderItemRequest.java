package com.quickserve.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO for order items
 */
public record OrderItemRequest(
        @NotNull(message = "Menu item ID is required") Long menuItemId,
        @Positive(message = "Quantity must be positive") Integer quantity
) {}
