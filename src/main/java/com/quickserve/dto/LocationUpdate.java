package com.quickserve.dto;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for real-time location updates
 */
public record LocationUpdate(
        @NotNull Long deliveryId,
        @NotNull Double latitude,
        @NotNull Double longitude
) {}
