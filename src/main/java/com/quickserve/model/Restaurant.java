package com.quickserve.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Restaurant entity representing food establishments on the platform
 */
@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;  // Restaurant owner

    private String cuisine;  // Type of food (Italian, Chinese, etc.)
    private String description;
    private String imageUrl;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Boolean isOpen = true;
    private Boolean isActive = true;

    private Double rating = 0.0;
    private Integer totalReviews = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
