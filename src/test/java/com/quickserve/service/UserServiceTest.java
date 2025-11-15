package com.quickserve.service;

import com.quickserve.dto.UserRequest;
import com.quickserve.model.User;
import com.quickserve.model.UserRole;
import com.quickserve.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UserService
 */
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateUser() {
        UserRequest request = new UserRequest(
                "John Doe",
                "john@example.com",
                "1234567890",
                "password123",
                UserRole.CUSTOMER,
                "123 Main St",
                40.7128,
                -74.0060
        );

        User user = userService.createUser(request);

        assertNotNull(user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals(UserRole.CUSTOMER, user.getRole());
    }

    @Test
    void testGetUserById() {
        UserRequest request = new UserRequest(
                "Jane Smith",
                "jane@example.com",
                "0987654321",
                "password456",
                UserRole.DELIVERY_PARTNER,
                "456 Oak Ave",
                40.7489,
                -73.9680
        );

        User createdUser = userService.createUser(request);
        User foundUser = userService.getUserById(createdUser.getId());

        assertEquals(createdUser.getId(), foundUser.getId());
        assertEquals("Jane Smith", foundUser.getName());
    }

    @Test
    void testDuplicateEmailThrowsException() {
        UserRequest request1 = new UserRequest(
                "User One",
                "duplicate@example.com",
                "1111111111",
                "password1",
                UserRole.CUSTOMER,
                "Address 1",
                40.0,
                -74.0
        );

        userService.createUser(request1);

        UserRequest request2 = new UserRequest(
                "User Two",
                "duplicate@example.com",
                "2222222222",
                "password2",
                UserRole.CUSTOMER,
                "Address 2",
                40.0,
                -74.0
        );

        assertThrows(RuntimeException.class, () -> userService.createUser(request2));
    }
}
