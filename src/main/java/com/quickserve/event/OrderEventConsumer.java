package com.quickserve.event;

import com.quickserve.config.KafkaConfig;
import com.quickserve.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer for order events
 * Handles async processing like notifications, analytics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaConfig.ORDER_CREATED, groupId = "notification-service")
    public void handleOrderCreated(OrderEvent event) {
        log.info("Received order created event: {}", event.getOrderId());
        // Send notification to customer
        notificationService.sendOrderConfirmation("customer@example.com", event.getOrderId());
    }

    @KafkaListener(topics = KafkaConfig.ORDER_DELIVERED, groupId = "notification-service")
    public void handleOrderDelivered(OrderEvent event) {
        log.info("Received order delivered event: {}", event.getOrderId());
        // Send delivery confirmation
        // Trigger review request
        // Update analytics
    }

    @KafkaListener(topics = KafkaConfig.PAYMENT_COMPLETED, groupId = "analytics-service")
    public void handlePaymentCompleted(OrderEvent event) {
        log.info("Received payment completed event: {}", event.getOrderId());
        // Update revenue analytics
        // Update restaurant earnings
        // Update delivery partner earnings
    }
}
