# QuickServe - Modern Delivery Platform

**A highly extensible, production-ready food delivery platform built with Java 21 and Spring Boot 3**

## Overview

QuickServe is a comprehensive delivery platform similar to DoorDash, featuring:

- User Management (Customers, Restaurant Owners, Delivery Partners, Admins)
- Restaurant Management
- Menu Management
- Order Processing
- Delivery Management with Real-time Tracking
- Payment Processing
- Rating & Review System
- Notifications
- WebSocket-based Real-time Updates

## Tech Stack

- **Java**: 21 (with preview features)
- **Spring Boot**: 3.2.0
- **Spring Data JPA**: Database operations
- **Spring WebSocket**: Real-time tracking
- **H2 Database**: In-memory database for local testing
- **Maven**: Build and dependency management
- **Lombok**: Reduce boilerplate code
- **Jakarta Validation**: Request validation

## Architecture

### Clean Layered Architecture

```
├── Model Layer (Domain Entities)
│   ├── User, Restaurant, MenuItem, Order, Delivery, Payment, Review
│   └── Enums: UserRole, OrderStatus, DeliveryStatus, PaymentStatus, PaymentMethod
│
├── Repository Layer (Data Access)
│   └── Spring Data JPA repositories
│
├── Service Layer (Business Logic)
│   ├── UserService, RestaurantService, MenuItemService
│   ├── OrderService, DeliveryService, PaymentService
│   ├── ReviewService, NotificationService
│   └── Clean, readable, and maintainable code
│
├── Controller Layer (REST APIs)
│   ├── UserController, RestaurantController, MenuItemController
│   ├── OrderController, DeliveryController, PaymentController
│   ├── ReviewController, TrackingController
│   └── Comprehensive REST endpoints
│
└── Configuration
    ├── WebSocketConfig (Real-time tracking)
    └── GlobalExceptionHandler (Error handling)
```

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- Any IDE (IntelliJ IDEA, Eclipse, VS Code)

## Getting Started

### 1. Build the Project

```bash
mvn clean package
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

or

```bash
java -jar target/quickserve-delivery-platform-1.0.0.jar
```

### 3. Access the Application

- **API Base URL**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:quickserve`
  - Username: `sa`
  - Password: (leave empty)

## API Documentation

### User Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email/{email}` | Get user by email |
| GET | `/api/users` | Get all users |
| GET | `/api/users/role/{role}` | Get users by role |
| GET | `/api/users/active` | Get active users |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

### Restaurant Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/restaurants` | Create a new restaurant |
| GET | `/api/restaurants/{id}` | Get restaurant by ID |
| GET | `/api/restaurants` | Get all restaurants |
| GET | `/api/restaurants/active` | Get active restaurants |
| GET | `/api/restaurants/open` | Get open restaurants |
| GET | `/api/restaurants/search/cuisine/{cuisine}` | Search by cuisine |
| GET | `/api/restaurants/search/name/{name}` | Search by name |
| GET | `/api/restaurants/owner/{ownerId}` | Get restaurants by owner |
| PUT | `/api/restaurants/{id}` | Update restaurant |
| PATCH | `/api/restaurants/{id}/status?isOpen=true` | Toggle restaurant status |
| DELETE | `/api/restaurants/{id}` | Delete restaurant |

### Menu Item APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/menu-items` | Create a new menu item |
| GET | `/api/menu-items/{id}` | Get menu item by ID |
| GET | `/api/menu-items/restaurant/{restaurantId}` | Get menu items by restaurant |
| GET | `/api/menu-items/restaurant/{restaurantId}/available` | Get available menu items |
| GET | `/api/menu-items/category/{category}` | Get menu items by category |
| GET | `/api/menu-items/vegetarian` | Get vegetarian menu items |
| PUT | `/api/menu-items/{id}` | Update menu item |
| PATCH | `/api/menu-items/{id}/availability?isAvailable=true` | Toggle availability |
| DELETE | `/api/menu-items/{id}` | Delete menu item |

### Order Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create a new order |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/customer/{customerId}` | Get orders by customer |
| GET | `/api/orders/restaurant/{restaurantId}` | Get orders by restaurant |
| GET | `/api/orders/status/{status}` | Get orders by status |
| PATCH | `/api/orders/{id}/status?status=CONFIRMED` | Update order status |
| PATCH | `/api/orders/{id}/cancel` | Cancel order |

### Delivery Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/deliveries/assign?orderId=1&deliveryPartnerId=2` | Assign delivery partner |
| GET | `/api/deliveries/{id}` | Get delivery by ID |
| GET | `/api/deliveries/order/{orderId}` | Get delivery by order ID |
| GET | `/api/deliveries/partner/{partnerId}` | Get deliveries by partner |
| GET | `/api/deliveries/status/{status}` | Get deliveries by status |
| PATCH | `/api/deliveries/location` | Update delivery location |
| PATCH | `/api/deliveries/{id}/picked-up` | Mark as picked up |
| PATCH | `/api/deliveries/{id}/delivered` | Mark as delivered |

### Payment APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments/process?orderId=1&paymentMethod=CREDIT_CARD` | Process payment |
| GET | `/api/payments/{id}` | Get payment by ID |
| GET | `/api/payments/order/{orderId}` | Get payment by order ID |
| GET | `/api/payments/status/{status}` | Get payments by status |
| POST | `/api/payments/{id}/refund` | Refund payment |

### Review & Rating APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/reviews` | Create a new review |
| GET | `/api/reviews/{id}` | Get review by ID |
| GET | `/api/reviews/restaurant/{restaurantId}` | Get reviews by restaurant |
| GET | `/api/reviews/customer/{customerId}` | Get reviews by customer |
| DELETE | `/api/reviews/{id}` | Delete review |

### Real-time Tracking

**WebSocket Endpoint**: `ws://localhost:8080/ws/tracking`

- Connect to `/topic/tracking` to receive real-time location updates
- Send location updates via `/app/location`

**REST Endpoint**:
- GET `/api/tracking/delivery/{deliveryId}` - Get current delivery location

## Sample API Requests

### 1. Create a Customer

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "1234567890",
    "password": "password123",
    "role": "CUSTOMER",
    "address": "123 Main St, New York, NY",
    "latitude": 40.7128,
    "longitude": -74.0060
  }'
```

### 2. Create a Restaurant

```bash
curl -X POST http://localhost:8080/api/restaurants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Italian Bistro",
    "address": "456 Pizza Lane, New York, NY",
    "latitude": 40.7489,
    "longitude": -73.9680,
    "ownerId": 1,
    "cuisine": "Italian",
    "description": "Authentic Italian cuisine",
    "imageUrl": "http://example.com/bistro.jpg",
    "openingTime": "10:00:00",
    "closingTime": "23:00:00"
  }'
```

### 3. Create a Menu Item

```bash
curl -X POST http://localhost:8080/api/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 1,
    "name": "Margherita Pizza",
    "description": "Classic pizza with tomato and mozzarella",
    "price": 12.99,
    "category": "Main Course",
    "imageUrl": "http://example.com/pizza.jpg",
    "isVegetarian": true,
    "isAvailable": true
  }'
```

### 4. Create an Order

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
      }
    ],
    "deliveryAddress": "123 Main St, New York, NY",
    "deliveryLatitude": 40.7128,
    "deliveryLongitude": -74.0060,
    "specialInstructions": "Ring the bell twice"
  }'
```

### 5. Process Payment

```bash
curl -X POST "http://localhost:8080/api/payments/process?orderId=1&paymentMethod=CREDIT_CARD"
```

## Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage
mvn clean test jacoco:report
```

## Key Features

### 1. Extensible Architecture
- Clean separation of concerns
- Easy to add new features
- Modular design
- Interface-based programming

### 2. Simple & Readable Code
- Clear naming conventions
- Comprehensive comments
- No unnecessary complexity
- Easy for freshers to understand

### 3. Production-Ready
- Global exception handling
- Input validation
- Transaction management
- Proper HTTP status codes

### 4. Real-time Features
- WebSocket-based tracking
- Live location updates
- Instant notifications

### 5. Complete Business Logic
- Order lifecycle management
- Delivery assignment
- Payment processing
- Rating and reviews
- Distance-based delivery fee calculation

## Database Schema

The application uses JPA entities with automatic schema generation. Key entities:

- **User**: All user types (customers, restaurant owners, delivery partners, admins)
- **Restaurant**: Restaurant information and location
- **MenuItem**: Menu items with pricing and availability
- **Order**: Order details with status tracking
- **OrderItem**: Individual items in an order
- **Delivery**: Delivery tracking with real-time location
- **Payment**: Payment processing and transaction history
- **Review**: Restaurant and delivery ratings

## Configuration

Application configuration is in `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:quickserve
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

  h2:
    console:
      enabled: true
      path: /h2-console
```

## Error Handling

The application includes comprehensive error handling:

- **Validation Errors**: Returns field-specific error messages
- **Business Logic Errors**: Returns appropriate HTTP status codes
- **Not Found Errors**: Returns 404 with helpful messages
- **Global Exception Handler**: Catches and formats all errors

## Future Enhancements

- JWT-based authentication and authorization
- Integration with external payment gateways (Stripe, PayPal)
- Email/SMS notifications (SendGrid, Twilio)
- Advanced search with Elasticsearch
- Caching with Redis
- API rate limiting
- Docker containerization
- Kubernetes deployment
- PostgreSQL for production
- Monitoring with Prometheus & Grafana

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Write tests
5. Submit a pull request

## License

MIT License - Feel free to use this project for learning or commercial purposes.

## Contact

For questions or suggestions, please open an issue on GitHub.

---

**Built with ❤️ using Java 21 and Spring Boot 3**
