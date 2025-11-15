package com.quickserve.model;

/**
 * Delivery status tracking
 */
public enum DeliveryStatus {
    ASSIGNED,        // Delivery partner assigned
    PICKED_UP,       // Order picked up from restaurant
    IN_TRANSIT,      // On the way to customer
    DELIVERED,       // Successfully delivered
    FAILED           // Delivery failed
}
