package com.quickserve.controller;

import com.quickserve.dto.RestaurantRequest;
import com.quickserve.model.Restaurant;
import com.quickserve.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller for Restaurant operations
 */
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        Restaurant restaurant = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(restaurant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/active")
    public ResponseEntity<List<Restaurant>> getActiveRestaurants() {
        List<Restaurant> restaurants = restaurantService.getActiveRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/open")
    public ResponseEntity<List<Restaurant>> getOpenRestaurants() {
        List<Restaurant> restaurants = restaurantService.getOpenRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/search/cuisine/{cuisine}")
    public ResponseEntity<List<Restaurant>> searchByCuisine(@PathVariable String cuisine) {
        List<Restaurant> restaurants = restaurantService.searchByCuisine(cuisine);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<Restaurant>> searchByName(@PathVariable String name) {
        List<Restaurant> restaurants = restaurantService.searchByName(name);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Restaurant>> getRestaurantsByOwner(@PathVariable Long ownerId) {
        List<Restaurant> restaurants = restaurantService.getRestaurantsByOwner(ownerId);
        return ResponseEntity.ok(restaurants);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable Long id,
                                                       @Valid @RequestBody RestaurantRequest request) {
        Restaurant restaurant = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(restaurant);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> toggleRestaurantStatus(@PathVariable Long id,
                                                        @RequestParam boolean isOpen) {
        restaurantService.toggleRestaurantStatus(id, isOpen);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }
}
