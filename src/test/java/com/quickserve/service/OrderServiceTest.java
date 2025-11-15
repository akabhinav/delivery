package com.quickserve.service;

import com.quickserve.dto.*;
import com.quickserve.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OrderService
 */
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuItemService menuItemService;

    private User customer;
    private Restaurant restaurant;
    private MenuItem menuItem;

    @BeforeEach
    void setup() {
        // Create customer
        UserRequest customerRequest = new UserRequest(
                "Test Customer",
                "customer@test.com",
                "1234567890",
                "password",
                UserRole.CUSTOMER,
                "Customer Address",
                40.7128,
                -74.0060
        );
        customer = userService.createUser(customerRequest);

        // Create restaurant owner
        UserRequest ownerRequest = new UserRequest(
                "Restaurant Owner",
                "owner@test.com",
                "0987654321",
                "password",
                UserRole.RESTAURANT_OWNER,
                "Owner Address",
                40.7489,
                -73.9680
        );
        User owner = userService.createUser(ownerRequest);

        // Create restaurant
        RestaurantRequest restaurantRequest = new RestaurantRequest(
                "Test Restaurant",
                "123 Food St",
                40.7489,
                -73.9680,
                owner.getId(),
                "Italian",
                "Best Italian food",
                "http://image.jpg",
                LocalTime.of(9, 0),
                LocalTime.of(22, 0)
        );
        restaurant = restaurantService.createRestaurant(restaurantRequest);

        // Create menu item
        MenuItemRequest menuItemRequest = new MenuItemRequest(
                restaurant.getId(),
                "Margherita Pizza",
                "Classic pizza",
                12.99,
                "Main Course",
                "http://pizza.jpg",
                true,
                true
        );
        menuItem = menuItemService.createMenuItem(menuItemRequest);
    }

    @Test
    void testCreateOrder() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(menuItem.getId(), 2);

        OrderRequest orderRequest = new OrderRequest(
                customer.getId(),
                restaurant.getId(),
                List.of(orderItemRequest),
                "Delivery Address",
                40.7128,
                -74.0060,
                "Ring the bell"
        );

        Order order = orderService.createOrder(orderRequest);

        assertNotNull(order.getId());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(customer.getId(), order.getCustomer().getId());
        assertEquals(restaurant.getId(), order.getRestaurant().getId());
        assertTrue(order.getSubtotal() > 0);
        assertTrue(order.getTotalAmount() > order.getSubtotal());
    }

    @Test
    void testUpdateOrderStatus() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(menuItem.getId(), 1);
        OrderRequest orderRequest = new OrderRequest(
                customer.getId(),
                restaurant.getId(),
                List.of(orderItemRequest),
                "Delivery Address",
                40.7128,
                -74.0060,
                null
        );

        Order order = orderService.createOrder(orderRequest);
        Order updatedOrder = orderService.updateOrderStatus(order.getId(), OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, updatedOrder.getStatus());
    }

    @Test
    void testCancelOrder() {
        OrderItemRequest orderItemRequest = new OrderItemRequest(menuItem.getId(), 1);
        OrderRequest orderRequest = new OrderRequest(
                customer.getId(),
                restaurant.getId(),
                List.of(orderItemRequest),
                "Delivery Address",
                40.7128,
                -74.0060,
                null
        );

        Order order = orderService.createOrder(orderRequest);
        orderService.cancelOrder(order.getId());

        Order cancelledOrder = orderService.getOrderById(order.getId());
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
    }
}
