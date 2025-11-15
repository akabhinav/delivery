# QuickServe - Architecture Documentation

## Overview

QuickServe follows a **clean, layered architecture** designed for extensibility, maintainability, and simplicity.

## Design Principles

### 1. Separation of Concerns
Each layer has a single, well-defined responsibility:
- **Models**: Define data structure
- **Repositories**: Handle data access
- **Services**: Implement business logic
- **Controllers**: Handle HTTP requests/responses

### 2. SOLID Principles

- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension, closed for modification
- **Liskov Substitution**: Repository interfaces enable easy testing
- **Interface Segregation**: Focused, specific interfaces
- **Dependency Inversion**: Depend on abstractions (Spring DI)

### 3. Simplicity First

- No over-engineering
- Clear naming conventions
- Minimal abstraction layers
- Easy for beginners to understand

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (REST Controllers + WebSocket)         │
│  - Handle HTTP requests                 │
│  - Validate input                       │
│  - Return responses                     │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Service Layer                   │
│  (Business Logic)                       │
│  - Process requests                     │
│  - Enforce business rules               │
│  - Coordinate operations                │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Repository Layer                │
│  (Data Access)                          │
│  - CRUD operations                      │
│  - Query methods                        │
│  - Database abstraction                 │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Database Layer                  │
│  (H2 / PostgreSQL / MySQL)              │
└─────────────────────────────────────────┘
```

## Module Structure

### 1. User Management
**Purpose**: Manage all types of users in the system

**Components**:
- User entity with role-based access
- UserRepository for data operations
- UserService for user management logic
- UserController for REST endpoints

**Roles**:
- CUSTOMER: Orders food
- RESTAURANT_OWNER: Manages restaurants
- DELIVERY_PARTNER: Delivers orders
- ADMIN: Platform administration

### 2. Restaurant Management
**Purpose**: Handle restaurant operations

**Components**:
- Restaurant entity with location data
- RestaurantRepository with custom queries
- RestaurantService for business logic
- RestaurantController for CRUD operations

**Features**:
- Search by cuisine
- Search by name
- Location-based queries
- Opening hours management
- Rating system

### 3. Menu Management
**Purpose**: Manage restaurant menus

**Components**:
- MenuItem entity
- MenuItemRepository
- MenuItemService
- MenuItemController

**Features**:
- Category-based organization
- Availability tracking
- Vegetarian filtering
- Price management

### 4. Order Management
**Purpose**: Process customer orders

**Components**:
- Order and OrderItem entities
- OrderRepository
- OrderService with business logic
- OrderController

**Features**:
- Order lifecycle management
- Automatic price calculation
- Delivery fee computation
- Tax calculation
- Special instructions

**Order States**:
```
PENDING → CONFIRMED → PREPARING → READY_FOR_PICKUP
    → PICKED_UP → ON_THE_WAY → DELIVERED
              ↓
          CANCELLED
```

### 5. Delivery Management
**Purpose**: Track and manage deliveries

**Components**:
- Delivery entity
- DeliveryRepository
- DeliveryService
- DeliveryController
- TrackingController (WebSocket)

**Features**:
- Partner assignment
- Real-time location tracking
- Status updates
- ETA calculation
- Distance tracking

**Delivery States**:
```
ASSIGNED → PICKED_UP → IN_TRANSIT → DELIVERED
                              ↓
                          FAILED
```

### 6. Payment Processing
**Purpose**: Handle payments and refunds

**Components**:
- Payment entity
- PaymentRepository
- PaymentService
- PaymentController

**Features**:
- Multiple payment methods
- Transaction tracking
- Refund processing
- Payment status management

**Payment Methods**:
- CREDIT_CARD
- DEBIT_CARD
- CASH_ON_DELIVERY
- DIGITAL_WALLET

### 7. Review & Rating
**Purpose**: Collect and display reviews

**Components**:
- Review entity
- ReviewRepository
- ReviewService
- ReviewController

**Features**:
- Restaurant ratings
- Delivery ratings
- Comments
- Automatic restaurant rating updates

### 8. Notification Service
**Purpose**: Send notifications to users

**Features**:
- Order confirmations
- Status updates
- Payment confirmations
- Delivery notifications
- SMS alerts

**Extensible for**:
- Email (SendGrid, AWS SES)
- SMS (Twilio, AWS SNS)
- Push notifications
- In-app notifications

### 9. Real-time Tracking
**Purpose**: Provide live delivery updates

**Technology**: WebSocket (STOMP over SockJS)

**Features**:
- Live location updates
- Order-specific channels
- Broadcast updates
- Low latency

## Data Models

### Core Entities

```java
User (id, name, email, phone, role, address, location)
  ↓ owns
Restaurant (id, name, address, location, cuisine, hours)
  ↓ has
MenuItem (id, name, price, category, availability)
  ↓ ordered in
Order (id, items, status, totals, delivery details)
  ↓ assigned to
Delivery (id, partner, status, location, timestamps)
  ↓ paid via
Payment (id, amount, method, status, transaction)
  ↓ reviewed in
Review (id, ratings, comment, timestamp)
```

### Relationships

- User → Restaurant (one-to-many: owner)
- Restaurant → MenuItem (one-to-many)
- User → Order (one-to-many: customer)
- Restaurant → Order (one-to-many)
- Order → OrderItem (one-to-many)
- Order → Delivery (one-to-one)
- Order → Payment (one-to-one)
- Order → Review (one-to-one)
- User → Delivery (one-to-many: partner)

## Request Flow

### Example: Creating an Order

```
1. Client → POST /api/orders
   ↓
2. OrderController.createOrder()
   ↓ validates request
   ↓
3. OrderService.createOrder()
   ↓ validates customer exists
   ↓ validates restaurant exists
   ↓ validates menu items exist
   ↓ calculates subtotal
   ↓ calculates delivery fee (distance-based)
   ↓ calculates tax
   ↓ calculates total
   ↓ saves order
   ↓ creates order items
   ↓
4. OrderRepository.save()
   ↓
5. Database persists order
   ↓
6. Response → Order object with ID and totals
```

## Extensibility Points

### Adding New Features

1. **New Entity**: Create in `model` package
2. **Data Access**: Create repository interface
3. **Business Logic**: Create service class
4. **API Endpoint**: Create controller class

### Example: Adding Promotions

```java
// 1. Create entity
@Entity
public class Promotion {
    @Id private Long id;
    private String code;
    private Double discountPercent;
    private LocalDateTime validUntil;
}

// 2. Create repository
public interface PromotionRepository
    extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);
}

// 3. Create service
@Service
public class PromotionService {
    public Promotion validatePromotion(String code) { ... }
}

// 4. Update OrderService
public Order createOrder(OrderRequest request, String promoCode) {
    // Apply promotion discount
}
```

## Technology Choices

### Why Spring Boot?
- Industry standard
- Convention over configuration
- Rich ecosystem
- Easy to learn
- Production-ready

### Why H2 for Development?
- No setup required
- Fast testing
- Easy to reset
- Web console for debugging

### Why Spring Data JPA?
- Reduces boilerplate
- Type-safe queries
- Automatic CRUD operations
- Easy to switch databases

### Why WebSocket?
- Real-time updates
- Bidirectional communication
- Low latency
- Browser support

## Scalability Considerations

### Current Architecture
- Single application server
- In-memory database
- Suitable for: Development, testing, small deployments

### Production Scaling

1. **Database**:
   - Switch to PostgreSQL/MySQL
   - Add connection pooling
   - Implement read replicas

2. **Application**:
   - Deploy multiple instances
   - Add load balancer
   - Stateless design (ready for scaling)

3. **Caching**:
   - Add Redis for frequently accessed data
   - Cache restaurant listings
   - Cache menu items

4. **Messaging**:
   - Add message queue (RabbitMQ/Kafka)
   - Async notification processing
   - Event-driven architecture

5. **Microservices** (if needed):
   - User Service
   - Restaurant Service
   - Order Service
   - Delivery Service
   - Payment Service

## Security Considerations

### Current Implementation
- Input validation
- Error handling
- Transaction management

### Production Additions Needed
- JWT authentication
- Role-based authorization
- Password hashing (BCrypt)
- HTTPS enforcement
- API rate limiting
- SQL injection prevention (JPA handles this)
- XSS protection
- CORS configuration

## Testing Strategy

### Unit Tests
- Service layer tests
- Repository tests
- Business logic validation

### Integration Tests
- Controller tests
- End-to-end flows
- Database integration

### Test Coverage Goals
- Service layer: 80%+
- Controller layer: 70%+
- Repository layer: 60%+

## Performance Optimization

### Database
- Index on frequently queried fields
- Lazy loading for relationships
- Query optimization

### Application
- Connection pooling
- Caching strategy
- Async processing for notifications
- Pagination for large datasets

### Monitoring
- Application metrics
- Database performance
- API response times
- Error rates

## Code Quality Standards

### Naming Conventions
- Classes: PascalCase
- Methods: camelCase
- Variables: camelCase
- Constants: UPPER_SNAKE_CASE

### Documentation
- JavaDoc for public APIs
- README for setup
- Architecture docs
- API documentation

### Best Practices
- Keep methods small
- Single responsibility
- DRY (Don't Repeat Yourself)
- Clear error messages
- Meaningful variable names

---

This architecture provides a solid foundation that is:
- ✅ Easy to understand
- ✅ Simple to modify
- ✅ Ready to extend
- ✅ Production-capable
- ✅ Well-documented
