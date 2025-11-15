# QuickServe API Testing Guide

## Complete End-to-End Testing Scenario

This guide walks you through testing all features of QuickServe platform.

## Prerequisites

- Application running on http://localhost:8080
- curl or Postman installed
- Or use the H2 console at http://localhost:8080/h2-console

## Step-by-Step Testing

### Step 1: Create Users

#### Create a Customer

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Alice Customer",
    "email": "alice@customer.com",
    "phone": "5551234567",
    "password": "customer123",
    "role": "CUSTOMER",
    "address": "123 Customer St, New York, NY 10001",
    "latitude": 40.7128,
    "longitude": -74.0060
  }'
```

Expected Response: User object with ID 1

#### Create a Restaurant Owner

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bob Owner",
    "email": "bob@restaurant.com",
    "phone": "5559876543",
    "password": "owner123",
    "role": "RESTAURANT_OWNER",
    "address": "456 Restaurant Ave, New York, NY 10002",
    "latitude": 40.7489,
    "longitude": -73.9680
  }'
```

Expected Response: User object with ID 2

#### Create a Delivery Partner

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Charlie Driver",
    "email": "charlie@delivery.com",
    "phone": "5555555555",
    "password": "driver123",
    "role": "DELIVERY_PARTNER",
    "address": "789 Delivery Rd, New York, NY 10003",
    "latitude": 40.7580,
    "longitude": -73.9855
  }'
```

Expected Response: User object with ID 3

### Step 2: Create Restaurants

#### Create Italian Restaurant

```bash
curl -X POST http://localhost:8080/api/restaurants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Bella Italia",
    "address": "100 Pizza Street, New York, NY 10010",
    "latitude": 40.7489,
    "longitude": -73.9680,
    "ownerId": 2,
    "cuisine": "Italian",
    "description": "Authentic Italian cuisine with fresh ingredients",
    "imageUrl": "https://example.com/bella-italia.jpg",
    "openingTime": "10:00:00",
    "closingTime": "23:00:00"
  }'
```

Expected Response: Restaurant object with ID 1

#### Create Chinese Restaurant

```bash
curl -X POST http://localhost:8080/api/restaurants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Golden Dragon",
    "address": "200 Noodle Lane, New York, NY 10011",
    "latitude": 40.7500,
    "longitude": -73.9700,
    "ownerId": 2,
    "cuisine": "Chinese",
    "description": "Traditional Chinese dishes",
    "imageUrl": "https://example.com/golden-dragon.jpg",
    "openingTime": "11:00:00",
    "closingTime": "22:00:00"
  }'
```

Expected Response: Restaurant object with ID 2

### Step 3: Add Menu Items

#### Add Pizza to Italian Restaurant

```bash
curl -X POST http://localhost:8080/api/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 1,
    "name": "Margherita Pizza",
    "description": "Classic pizza with tomato sauce, mozzarella, and fresh basil",
    "price": 12.99,
    "category": "Main Course",
    "imageUrl": "https://example.com/margherita.jpg",
    "isVegetarian": true,
    "isAvailable": true
  }'
```

#### Add Pasta

```bash
curl -X POST http://localhost:8080/api/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 1,
    "name": "Carbonara Pasta",
    "description": "Creamy pasta with bacon and parmesan",
    "price": 14.99,
    "category": "Main Course",
    "imageUrl": "https://example.com/carbonara.jpg",
    "isVegetarian": false,
    "isAvailable": true
  }'
```

#### Add Dessert

```bash
curl -X POST http://localhost:8080/api/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 1,
    "name": "Tiramisu",
    "description": "Classic Italian dessert",
    "price": 6.99,
    "category": "Dessert",
    "imageUrl": "https://example.com/tiramisu.jpg",
    "isVegetarian": true,
    "isAvailable": true
  }'
```

#### Add Chinese Items

```bash
curl -X POST http://localhost:8080/api/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 2,
    "name": "Kung Pao Chicken",
    "description": "Spicy stir-fried chicken",
    "price": 13.99,
    "category": "Main Course",
    "imageUrl": "https://example.com/kungpao.jpg",
    "isVegetarian": false,
    "isAvailable": true
  }'
```

### Step 4: Browse Restaurants

#### Get All Active Restaurants

```bash
curl http://localhost:8080/api/restaurants/active
```

#### Search by Cuisine

```bash
curl http://localhost:8080/api/restaurants/search/cuisine/Italian
```

#### Get Restaurant Menu

```bash
curl http://localhost:8080/api/menu-items/restaurant/1/available
```

### Step 5: Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "items": [
      {
        "menuItemId": 1,
        "quantity": 2
      },
      {
        "menuItemId": 3,
        "quantity": 1
      }
    ],
    "deliveryAddress": "123 Customer St, New York, NY 10001",
    "deliveryLatitude": 40.7128,
    "deliveryLongitude": -74.0060,
    "specialInstructions": "Please ring the doorbell twice"
  }'
```

Expected Response: Order object with calculated totals

### Step 6: Restaurant Confirms Order

```bash
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=CONFIRMED"
```

### Step 7: Process Payment

```bash
curl -X POST "http://localhost:8080/api/payments/process?orderId=1&paymentMethod=CREDIT_CARD"
```

Expected Response: Payment object with transaction ID and COMPLETED status

### Step 8: Update Order Status

```bash
# Mark as preparing
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=PREPARING"

# Mark as ready for pickup
curl -X PATCH "http://localhost:8080/api/orders/1/status?status=READY_FOR_PICKUP"
```

### Step 9: Assign Delivery Partner

```bash
curl -X POST "http://localhost:8080/api/deliveries/assign?orderId=1&deliveryPartnerId=3"
```

Expected Response: Delivery object with ASSIGNED status

### Step 10: Track Delivery

#### Mark as Picked Up

```bash
curl -X PATCH http://localhost:8080/api/deliveries/1/picked-up
```

#### Update Delivery Location (Real-time)

```bash
curl -X PATCH http://localhost:8080/api/deliveries/location \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryId": 1,
    "latitude": 40.7300,
    "longitude": -74.0000
  }'
```

#### Get Current Location

```bash
curl http://localhost:8080/api/tracking/delivery/1
```

#### Mark as Delivered

```bash
curl -X PATCH http://localhost:8080/api/deliveries/1/delivered
```

### Step 11: Submit Review

```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "customerId": 1,
    "restaurantRating": 5,
    "deliveryRating": 5,
    "comment": "Excellent food and fast delivery!"
  }'
```

### Step 12: View Reports

#### Get Customer Orders

```bash
curl http://localhost:8080/api/orders/customer/1
```

#### Get Restaurant Orders

```bash
curl http://localhost:8080/api/orders/restaurant/1
```

#### Get Restaurant Reviews

```bash
curl http://localhost:8080/api/reviews/restaurant/1
```

#### Get Delivery Partner Deliveries

```bash
curl http://localhost:8080/api/deliveries/partner/3
```

## Testing Additional Features

### Test Order Cancellation

```bash
# Create a new order first, then cancel it
curl -X PATCH http://localhost:8080/api/orders/2/cancel
```

### Test Payment Refund

```bash
curl -X POST http://localhost:8080/api/payments/1/refund
```

### Toggle Restaurant Status

```bash
# Close restaurant
curl -X PATCH "http://localhost:8080/api/restaurants/1/status?isOpen=false"

# Open restaurant
curl -X PATCH "http://localhost:8080/api/restaurants/1/status?isOpen=true"
```

### Toggle Menu Item Availability

```bash
# Make item unavailable
curl -X PATCH "http://localhost:8080/api/menu-items/1/availability?isAvailable=false"

# Make item available
curl -X PATCH "http://localhost:8080/api/menu-items/1/availability?isAvailable=true"
```

### Filter Orders by Status

```bash
curl http://localhost:8080/api/orders/status/DELIVERED
```

### Get Active Users by Role

```bash
curl http://localhost:8080/api/users/role/DELIVERY_PARTNER
```

## Expected Order Flow

1. **PENDING** - Order created
2. **CONFIRMED** - Restaurant confirmed
3. **PREPARING** - Food is being prepared
4. **READY_FOR_PICKUP** - Food is ready
5. **PICKED_UP** - Delivery partner picked up
6. **ON_THE_WAY** - Out for delivery
7. **DELIVERED** - Successfully delivered

## Tips for Testing

1. Always create users before creating dependent entities
2. Check the H2 console to verify data
3. Note down IDs from responses to use in subsequent requests
4. Test error cases (invalid IDs, duplicate emails, etc.)
5. Use Postman collections for easier testing

## Postman Collection

Import these requests into Postman for easier testing. Save time by creating a collection with all the above requests and use variables for IDs.

## WebSocket Testing

For testing real-time tracking:

1. Use a WebSocket client (wscat, Postman WebSocket)
2. Connect to `ws://localhost:8080/ws/tracking`
3. Subscribe to `/topic/tracking`
4. Send location updates via `/app/location`

---

Happy Testing! 🚀
