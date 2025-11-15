package com.quickserve.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request DTO for creating orders
 */
public record OrderRequest(
        @NotNull(message = "Customer ID is required") Long customerId,
        @NotNull(message = "Restaurant ID is required") Long restaurantId,
        @NotEmpty(message = "Order items cannot be empty") List<OrderItemRequest> items,
        String deliveryAddress,
        Double deliveryLatitude,
        Double deliveryLongitude,
        String specialInstructions
) {}
