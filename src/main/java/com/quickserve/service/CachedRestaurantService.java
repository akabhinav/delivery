package com.quickserve.service;

import com.quickserve.model.Restaurant;
import com.quickserve.repository.RestaurantRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Cached Restaurant Service for high-scale performance
 * Uses Redis L2 cache to reduce database load
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CachedRestaurantService {

    private final RestaurantRepository restaurantRepository;

    /**
     * Cache restaurant details for 5 minutes
     * Key: restaurants::restaurantId
     */
    @Cacheable(value = "restaurants", key = "#id")
    @CircuitBreaker(name = "restaurantService", fallbackMethod = "getRestaurantFallback")
    public Restaurant getRestaurantById(Long id) {
        log.info("Fetching restaurant from database: {}", id);
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found: " + id));
    }

    /**
     * Cache active restaurants list for 2 minutes
     * Frequently accessed, high read load
     */
    @Cacheable(value = "restaurantList", key = "'active'")
    public List<Restaurant> getActiveRestaurants() {
        log.info("Fetching active restaurants from database");
        return restaurantRepository.findByIsActiveTrue();
    }

    /**
     * Cache search results by cuisine
     */
    @Cacheable(value = "restaurantList", key = "'cuisine:' + #cuisine")
    public List<Restaurant> searchByCuisine(String cuisine) {
        log.info("Searching restaurants by cuisine from database: {}", cuisine);
        return restaurantRepository.findByCuisineContainingIgnoreCase(cuisine);
    }

    /**
     * Evict cache when restaurant is updated
     */
    @CacheEvict(value = {"restaurants", "restaurantList"}, allEntries = true)
    public Restaurant updateRestaurant(Restaurant restaurant) {
        log.info("Updating restaurant and evicting cache: {}", restaurant.getId());
        return restaurantRepository.save(restaurant);
    }

    /**
     * Fallback method for circuit breaker
     */
    public Restaurant getRestaurantFallback(Long id, Exception ex) {
        log.error("Circuit breaker activated for restaurant: {}", id, ex);
        // Return cached data or default
        Restaurant fallback = new Restaurant();
        fallback.setId(id);
        fallback.setName("Service Temporarily Unavailable");
        return fallback;
    }
}
