package com.quickserve.repository;

import com.quickserve.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Restaurant entity operations
 */
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByIsActiveTrue();

    List<Restaurant> findByIsOpenTrue();

    List<Restaurant> findByCuisineContainingIgnoreCase(String cuisine);

    List<Restaurant> findByNameContainingIgnoreCase(String name);

    List<Restaurant> findByOwnerId(Long ownerId);
}
