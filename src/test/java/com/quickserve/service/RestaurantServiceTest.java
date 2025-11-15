package com.quickserve.service;

import com.quickserve.dto.RestaurantRequest;
import com.quickserve.dto.UserRequest;
import com.quickserve.model.Restaurant;
import com.quickserve.model.User;
import com.quickserve.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for RestaurantService
 */
@SpringBootTest
@Transactional
class RestaurantServiceTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private UserService userService;

    private User owner;

    @BeforeEach
    void setup() {
        UserRequest ownerRequest = new UserRequest(
                "Restaurant Owner",
                "owner@restaurant.com",
                "1234567890",
                "password",
                UserRole.RESTAURANT_OWNER,
                "Owner Address",
                40.7489,
                -73.9680
        );
        owner = userService.createUser(ownerRequest);
    }

    @Test
    void testCreateRestaurant() {
        RestaurantRequest request = new RestaurantRequest(
                "Italian Bistro",
                "123 Pizza Lane",
                40.7489,
                -73.9680,
                owner.getId(),
                "Italian",
                "Authentic Italian cuisine",
                "http://image.jpg",
                LocalTime.of(10, 0),
                LocalTime.of(23, 0)
        );

        Restaurant restaurant = restaurantService.createRestaurant(request);

        assertNotNull(restaurant.getId());
        assertEquals("Italian Bistro", restaurant.getName());
        assertEquals("Italian", restaurant.getCuisine());
        assertTrue(restaurant.getIsActive());
    }

    @Test
    void testSearchByCuisine() {
        RestaurantRequest request1 = new RestaurantRequest(
                "Chinese Dragon",
                "456 Noodle St",
                40.7500,
                -73.9700,
                owner.getId(),
                "Chinese",
                "Best Chinese food",
                "http://image1.jpg",
                LocalTime.of(11, 0),
                LocalTime.of(22, 0)
        );
        restaurantService.createRestaurant(request1);

        List<Restaurant> restaurants = restaurantService.searchByCuisine("Chinese");

        assertFalse(restaurants.isEmpty());
        assertTrue(restaurants.stream().anyMatch(r -> r.getName().equals("Chinese Dragon")));
    }

    @Test
    void testToggleRestaurantStatus() {
        RestaurantRequest request = new RestaurantRequest(
                "Test Restaurant",
                "789 Test Ave",
                40.7600,
                -73.9800,
                owner.getId(),
                "Mexican",
                "Mexican cuisine",
                "http://image2.jpg",
                LocalTime.of(9, 0),
                LocalTime.of(21, 0)
        );

        Restaurant restaurant = restaurantService.createRestaurant(request);
        assertTrue(restaurant.getIsOpen());

        restaurantService.toggleRestaurantStatus(restaurant.getId(), false);
        Restaurant updatedRestaurant = restaurantService.getRestaurantById(restaurant.getId());

        assertFalse(updatedRestaurant.getIsOpen());
    }
}
