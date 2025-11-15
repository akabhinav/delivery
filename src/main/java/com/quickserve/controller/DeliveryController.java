package com.quickserve.controller;

import com.quickserve.dto.LocationUpdate;
import com.quickserve.model.Delivery;
import com.quickserve.model.DeliveryStatus;
import com.quickserve.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for Delivery operations
 */
@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/assign")
    public ResponseEntity<Delivery> assignDeliveryPartner(@RequestParam Long orderId,
                                                          @RequestParam Long deliveryPartnerId) {
        Delivery delivery = deliveryService.assignDeliveryPartner(orderId, deliveryPartnerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(delivery);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        Delivery delivery = deliveryService.getDeliveryById(id);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Delivery> getDeliveryByOrderId(@PathVariable Long orderId) {
        Delivery delivery = deliveryService.getDeliveryByOrderId(orderId);
        return ResponseEntity.ok(delivery);
    }

    @GetMapping("/partner/{partnerId}")
    public ResponseEntity<List<Delivery>> getDeliveriesByPartner(@PathVariable Long partnerId) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByPartner(partnerId);
        return ResponseEntity.ok(deliveries);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Delivery>> getDeliveriesByStatus(@PathVariable DeliveryStatus status) {
        List<Delivery> deliveries = deliveryService.getDeliveriesByStatus(status);
        return ResponseEntity.ok(deliveries);
    }

    @PatchMapping("/location")
    public ResponseEntity<Delivery> updateLocation(@Valid @RequestBody LocationUpdate locationUpdate) {
        Delivery delivery = deliveryService.updateLocation(locationUpdate);
        return ResponseEntity.ok(delivery);
    }

    @PatchMapping("/{id}/picked-up")
    public ResponseEntity<Delivery> markAsPickedUp(@PathVariable Long id) {
        Delivery delivery = deliveryService.markAsPickedUp(id);
        return ResponseEntity.ok(delivery);
    }

    @PatchMapping("/{id}/delivered")
    public ResponseEntity<Delivery> markAsDelivered(@PathVariable Long id) {
        Delivery delivery = deliveryService.markAsDelivered(id);
        return ResponseEntity.ok(delivery);
    }
}
