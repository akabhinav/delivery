package com.quickserve.service;

import com.quickserve.dto.OrderItemRequest;
import com.quickserve.dto.OrderRequest;
import com.quickserve.model.*;
import com.quickserve.repository.OrderItemRepository;
import com.quickserve.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Order management operations
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    @Transactional
    public Order createOrder(OrderRequest request) {
        User customer = userService.getUserById(request.customerId());
        Restaurant restaurant = restaurantService.getRestaurantById(request.restaurantId());

        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(request.deliveryAddress());
        order.setDeliveryLatitude(request.deliveryLatitude());
        order.setDeliveryLongitude(request.deliveryLongitude());
        order.setSpecialInstructions(request.specialInstructions());

        // Calculate totals
        double subtotal = 0.0;
        for (OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = menuItemService.getMenuItemById(itemRequest.menuItemId());
            subtotal += menuItem.getPrice() * itemRequest.quantity();
        }

        double deliveryFee = calculateDeliveryFee(restaurant, request.deliveryLatitude(), request.deliveryLongitude());
        double tax = subtotal * 0.08; // 8% tax
        double totalAmount = subtotal + deliveryFee + tax;

        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTax(tax);
        order.setTotalAmount(totalAmount);

        order = orderRepository.save(order);

        // Create order items
        for (OrderItemRequest itemRequest : request.items()) {
            MenuItem menuItem = menuItemService.getMenuItemById(itemRequest.menuItemId());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setPrice(menuItem.getPrice());
            orderItem.setSubtotal(menuItem.getPrice() * itemRequest.quantity());

            orderItemRepository.save(orderItem);
        }

        return order;
    }

    private double calculateDeliveryFee(Restaurant restaurant, Double deliveryLat, Double deliveryLon) {
        if (deliveryLat == null || deliveryLon == null) {
            return 2.99; // Default delivery fee
        }

        // Calculate distance using Haversine formula (simplified)
        double distance = calculateDistance(
                restaurant.getLatitude(), restaurant.getLongitude(),
                deliveryLat, deliveryLon
        );

        return 2.99 + (distance * 0.50); // Base fee + per km charge
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for distance calculation
        double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public List<Order> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Cannot cancel order in status: " + order.getStatus());
        }
    }
}
