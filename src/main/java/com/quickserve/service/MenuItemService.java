package com.quickserve.service;

import com.quickserve.dto.MenuItemRequest;
import com.quickserve.model.MenuItem;
import com.quickserve.model.Restaurant;
import com.quickserve.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Menu Item management operations
 */
@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantService restaurantService;

    @Transactional
    public MenuItem createMenuItem(MenuItemRequest request) {
        Restaurant restaurant = restaurantService.getRestaurantById(request.restaurantId());

        MenuItem menuItem = new MenuItem();
        menuItem.setRestaurant(restaurant);
        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setCategory(request.category());
        menuItem.setImageUrl(request.imageUrl());
        menuItem.setIsVegetarian(request.isVegetarian() != null ? request.isVegetarian() : false);
        menuItem.setIsAvailable(request.isAvailable() != null ? request.isAvailable() : true);

        return menuItemRepository.save(menuItem);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found with id: " + id));
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getMenuItemsByRestaurant(Long restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public List<MenuItem> getAvailableMenuItems(Long restaurantId) {
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId);
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category);
    }

    public List<MenuItem> getVegetarianMenuItems() {
        return menuItemRepository.findByIsVegetarianTrue();
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setCategory(request.category());
        menuItem.setImageUrl(request.imageUrl());
        if (request.isVegetarian() != null) {
            menuItem.setIsVegetarian(request.isVegetarian());
        }
        if (request.isAvailable() != null) {
            menuItem.setIsAvailable(request.isAvailable());
        }
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public void toggleAvailability(Long id, boolean isAvailable) {
        MenuItem menuItem = getMenuItemById(id);
        menuItem.setIsAvailable(isAvailable);
        menuItemRepository.save(menuItem);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }
}
