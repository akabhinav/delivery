package com.quickserve.service;

import com.quickserve.dto.ReviewRequest;
import com.quickserve.model.Order;
import com.quickserve.model.Restaurant;
import com.quickserve.model.Review;
import com.quickserve.model.User;
import com.quickserve.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for Review and Rating management
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderService orderService;
    private final UserService userService;
    private final RestaurantService restaurantService;

    @Transactional
    public Review createReview(ReviewRequest request) {
        Order order = orderService.getOrderById(request.orderId());
        User customer = userService.getUserById(request.customerId());

        // Check if review already exists
        if (reviewRepository.findByOrderId(request.orderId()).isPresent()) {
            throw new RuntimeException("Review already exists for order: " + request.orderId());
        }

        Review review = new Review();
        review.setOrder(order);
        review.setCustomer(customer);
        review.setRestaurant(order.getRestaurant());
        review.setRestaurantRating(request.restaurantRating());
        review.setDeliveryRating(request.deliveryRating());
        review.setComment(request.comment());

        review = reviewRepository.save(review);

        // Update restaurant rating
        updateRestaurantRating(order.getRestaurant().getId());

        return review;
    }

    private void updateRestaurantRating(Long restaurantId) {
        List<Review> reviews = reviewRepository.findByRestaurantId(restaurantId);
        if (!reviews.isEmpty()) {
            double averageRating = reviews.stream()
                    .mapToInt(Review::getRestaurantRating)
                    .average()
                    .orElse(0.0);

            Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
            restaurant.setRating(Math.round(averageRating * 10.0) / 10.0);
            restaurant.setTotalReviews(reviews.size());
        }
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id: " + id));
    }

    public List<Review> getReviewsByRestaurant(Long restaurantId) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId);
    }

    public List<Review> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
