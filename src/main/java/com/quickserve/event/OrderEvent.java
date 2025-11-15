package com.quickserve.event;

import com.quickserve.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Order event for Kafka event streaming
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private Long orderId;
    private Long customerId;
    private Long restaurantId;
    private OrderStatus status;
    private Double totalAmount;
    private LocalDateTime timestamp;
    private String eventType;  // CREATED, CONFIRMED, DELIVERED, etc.
}
