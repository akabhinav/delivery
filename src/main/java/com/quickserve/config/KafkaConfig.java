package com.quickserve.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Configuration for Event-Driven Architecture
 * Handles async processing and event streaming for scalability
 */
@Configuration
public class KafkaConfig {

    // Topic names as constants
    public static final String USER_REGISTERED = "user.registered";
    public static final String USER_UPDATED = "user.updated";

    public static final String RESTAURANT_CREATED = "restaurant.created";
    public static final String RESTAURANT_UPDATED = "restaurant.updated";
    public static final String MENU_UPDATED = "restaurant.menu.updated";

    public static final String ORDER_CREATED = "order.created";
    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_READY = "order.ready";
    public static final String ORDER_PICKED_UP = "order.picked_up";
    public static final String ORDER_DELIVERED = "order.delivered";
    public static final String ORDER_CANCELLED = "order.cancelled";

    public static final String DELIVERY_ASSIGNED = "delivery.assigned";
    public static final String DELIVERY_LOCATION = "delivery.location";
    public static final String DELIVERY_COMPLETED = "delivery.completed";

    public static final String PAYMENT_INITIATED = "payment.initiated";
    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED = "payment.failed";

    public static final String NOTIFICATION_EMAIL = "notification.email";
    public static final String NOTIFICATION_SMS = "notification.sms";
    public static final String NOTIFICATION_PUSH = "notification.push";

    /**
     * Create Kafka topics with proper partitioning for scale
     * 32 partitions allows parallel processing across multiple consumers
     */

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(USER_REGISTERED)
                .partitions(32)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED)
                .partitions(32)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name(ORDER_CONFIRMED)
                .partitions(32)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic orderDeliveredTopic() {
        return TopicBuilder.name(ORDER_DELIVERED)
                .partitions(32)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic deliveryLocationTopic() {
        return TopicBuilder.name(DELIVERY_LOCATION)
                .partitions(32)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(PAYMENT_COMPLETED)
                .partitions(32)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic notificationEmailTopic() {
        return TopicBuilder.name(NOTIFICATION_EMAIL)
                .partitions(16)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic notificationSmsTopic() {
        return TopicBuilder.name(NOTIFICATION_SMS)
                .partitions(16)
                .replicas(3)
                .build();
    }
}
