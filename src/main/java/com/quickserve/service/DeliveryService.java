package com.quickserve.service;

import com.quickserve.dto.LocationUpdate;
import com.quickserve.model.*;
import com.quickserve.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for Delivery management and tracking
 */
@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderService orderService;
    private final UserService userService;

    @Transactional
    public Delivery assignDeliveryPartner(Long orderId, Long deliveryPartnerId) {
        Order order = orderService.getOrderById(orderId);
        User deliveryPartner = userService.getUserById(deliveryPartnerId);

        if (deliveryPartner.getRole() != UserRole.DELIVERY_PARTNER) {
            throw new RuntimeException("User is not a delivery partner");
        }

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setDeliveryPartner(deliveryPartner);
        delivery.setStatus(DeliveryStatus.ASSIGNED);

        // Calculate estimated time (simplified)
        delivery.setEstimatedTimeMinutes(30);

        // Update order status
        orderService.updateOrderStatus(orderId, OrderStatus.PICKED_UP);

        return deliveryRepository.save(delivery);
    }

    public Delivery getDeliveryById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
    }

    public Delivery getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId));
    }

    public List<Delivery> getDeliveriesByPartner(Long partnerId) {
        return deliveryRepository.findByDeliveryPartnerIdOrderByCreatedAtDesc(partnerId);
    }

    public List<Delivery> getDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.findByStatus(status);
    }

    @Transactional
    public Delivery updateLocation(LocationUpdate locationUpdate) {
        Delivery delivery = getDeliveryById(locationUpdate.deliveryId());
        delivery.setCurrentLatitude(locationUpdate.latitude());
        delivery.setCurrentLongitude(locationUpdate.longitude());
        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery markAsPickedUp(Long deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setStatus(DeliveryStatus.PICKED_UP);
        delivery.setPickedUpAt(LocalDateTime.now());

        // Update order status
        orderService.updateOrderStatus(delivery.getOrder().getId(), OrderStatus.ON_THE_WAY);

        return deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery markAsDelivered(Long deliveryId) {
        Delivery delivery = getDeliveryById(deliveryId);
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(LocalDateTime.now());

        // Update order status
        orderService.updateOrderStatus(delivery.getOrder().getId(), OrderStatus.DELIVERED);

        return deliveryRepository.save(delivery);
    }
}
