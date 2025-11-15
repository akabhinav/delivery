package com.quickserve.model;

/**
 * Order lifecycle status
 */
public enum OrderStatus {
    PENDING,           // Order placed, awaiting restaurant confirmation
    CONFIRMED,         // Restaurant confirmed the order
    PREPARING,         // Food is being prepared
    READY_FOR_PICKUP,  // Food is ready, waiting for delivery partner
    PICKED_UP,         // Delivery partner picked up the order
    ON_THE_WAY,        // Order is being delivered
    DELIVERED,         // Order delivered successfully
    CANCELLED          // Order was cancelled
}
