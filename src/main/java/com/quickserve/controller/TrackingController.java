package com.quickserve.controller;

import com.quickserve.dto.LocationUpdate;
import com.quickserve.model.Delivery;
import com.quickserve.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * WebSocket controller for real-time delivery tracking
 */
@Controller
@RequiredArgsConstructor
public class TrackingController {

    private final DeliveryService deliveryService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * WebSocket endpoint for location updates
     */
    @MessageMapping("/location")
    @SendTo("/topic/tracking")
    public LocationUpdate handleLocationUpdate(@Valid LocationUpdate locationUpdate) {
        // Update delivery location in database
        deliveryService.updateLocation(locationUpdate);

        // Broadcast to all subscribers
        return locationUpdate;
    }

    /**
     * REST endpoint to get current delivery location
     */
    @GetMapping("/api/tracking/delivery/{deliveryId}")
    @ResponseBody
    public Delivery getDeliveryLocation(@PathVariable Long deliveryId) {
        return deliveryService.getDeliveryById(deliveryId);
    }

    /**
     * Send real-time update to specific order subscribers
     */
    public void sendLocationUpdate(Long orderId, LocationUpdate locationUpdate) {
        messagingTemplate.convertAndSend("/topic/order/" + orderId, locationUpdate);
    }
}
