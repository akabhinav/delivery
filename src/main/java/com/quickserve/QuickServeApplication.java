package com.quickserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * QuickServe - Modern Delivery Platform
 *
 * A highly extensible, production-ready delivery platform built with Java 21.
 * Features include user management, restaurant operations, order processing,
 * real-time delivery tracking, payments, and notifications.
 */
@SpringBootApplication
public class QuickServeApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuickServeApplication.class, args);
        System.out.println("🚀 QuickServe Delivery Platform Started Successfully!");
        System.out.println("📱 API Documentation: http://localhost:8080");
        System.out.println("🗄️  H2 Console: http://localhost:8080/h2-console");
    }
}
