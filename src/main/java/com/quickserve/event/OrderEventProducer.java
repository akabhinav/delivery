package com.quickserve.event;

import com.quickserve.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Kafka producer for order events
 * Enables async, event-driven processing for scalability
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void publishOrderCreated(OrderEvent event) {
        log.info("Publishing order created event: {}", event.getOrderId());
        kafkaTemplate.send(KafkaConfig.ORDER_CREATED, event.getOrderId().toString(), event);
    }

    public void publishOrderConfirmed(OrderEvent event) {
        log.info("Publishing order confirmed event: {}", event.getOrderId());
        kafkaTemplate.send(KafkaConfig.ORDER_CONFIRMED, event.getOrderId().toString(), event);
    }

    public void publishOrderDelivered(OrderEvent event) {
        log.info("Publishing order delivered event: {}", event.getOrderId());
        kafkaTemplate.send(KafkaConfig.ORDER_DELIVERED, event.getOrderId().toString(), event);
    }
}
