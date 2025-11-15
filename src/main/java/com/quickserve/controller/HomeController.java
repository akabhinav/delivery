package com.quickserve.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home controller providing API documentation
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "QuickServe Delivery Platform");
        response.put("version", "1.0.0");
        response.put("description", "Modern delivery platform built with Java 21 & Spring Boot 3");
        response.put("status", "Running");

        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("users", "/api/users");
        endpoints.put("restaurants", "/api/restaurants");
        endpoints.put("menu-items", "/api/menu-items");
        endpoints.put("orders", "/api/orders");
        endpoints.put("deliveries", "/api/deliveries");
        endpoints.put("payments", "/api/payments");
        endpoints.put("reviews", "/api/reviews");
        endpoints.put("h2-console", "/h2-console");

        response.put("endpoints", endpoints);
        return response;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "QuickServe Delivery Platform");
        return response;
    }
}
