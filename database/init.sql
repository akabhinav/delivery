-- Database initialization script for PostgreSQL

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS quickserve_db;

\c quickserve_db;

-- Enable extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";
CREATE EXTENSION IF NOT EXISTS "postgis";  -- For geo-spatial queries

-- Create indexes for high performance

-- User table indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_location ON users USING GIST (ST_MakePoint(longitude, latitude));

-- Restaurant table indexes
CREATE INDEX IF NOT EXISTS idx_restaurants_name ON restaurants(name);
CREATE INDEX IF NOT EXISTS idx_restaurants_cuisine ON restaurants(cuisine);
CREATE INDEX IF NOT EXISTS idx_restaurants_active_open ON restaurants(is_active, is_open);
CREATE INDEX IF NOT EXISTS idx_restaurants_rating ON restaurants(rating DESC);
CREATE INDEX IF NOT EXISTS idx_restaurants_location ON restaurants USING GIST (ST_MakePoint(longitude, latitude));
CREATE INDEX IF NOT EXISTS idx_restaurants_owner ON restaurants(owner_id);

-- Menu items indexes
CREATE INDEX IF NOT EXISTS idx_menu_items_restaurant ON menu_items(restaurant_id);
CREATE INDEX IF NOT EXISTS idx_menu_items_category ON menu_items(category);
CREATE INDEX IF NOT EXISTS idx_menu_items_available ON menu_items(is_available);
CREATE INDEX IF NOT EXISTS idx_menu_items_restaurant_available ON menu_items(restaurant_id, is_available);

-- Order table indexes (partitioned by time)
CREATE INDEX IF NOT EXISTS idx_orders_customer_created ON orders(customer_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_restaurant_created ON orders(restaurant_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_status_created ON orders(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_orders_created ON orders(created_at DESC);

-- Order items indexes
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_menu_item ON order_items(menu_item_id);

-- Delivery table indexes
CREATE INDEX IF NOT EXISTS idx_deliveries_order ON deliveries(order_id);
CREATE INDEX IF NOT EXISTS idx_deliveries_partner_status ON deliveries(delivery_partner_id, status);
CREATE INDEX IF NOT EXISTS idx_deliveries_status_created ON deliveries(status, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_deliveries_location ON deliveries USING GIST (ST_MakePoint(current_longitude, current_latitude));

-- Payment table indexes
CREATE INDEX IF NOT EXISTS idx_payments_order ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_transaction ON payments(transaction_id);
CREATE INDEX IF NOT EXISTS idx_payments_created ON payments(created_at DESC);

-- Review table indexes
CREATE INDEX IF NOT EXISTS idx_reviews_restaurant_created ON reviews(restaurant_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_reviews_customer ON reviews(customer_id);
CREATE INDEX IF NOT EXISTS idx_reviews_order ON reviews(order_id);

-- Materialized view for restaurant statistics (read optimization)
CREATE MATERIALIZED VIEW IF NOT EXISTS restaurant_stats AS
SELECT
    r.id,
    r.name,
    r.cuisine,
    COUNT(DISTINCT o.id) as total_orders,
    AVG(o.total_amount) as avg_order_value,
    COUNT(DISTINCT rev.id) as total_reviews,
    AVG(rev.restaurant_rating) as avg_rating,
    COUNT(DISTINCT CASE WHEN o.created_at > NOW() - INTERVAL '30 days' THEN o.id END) as orders_last_30_days
FROM restaurants r
LEFT JOIN orders o ON o.restaurant_id = r.id
LEFT JOIN reviews rev ON rev.restaurant_id = r.id
WHERE r.is_active = true
GROUP BY r.id, r.name, r.cuisine;

CREATE UNIQUE INDEX ON restaurant_stats(id);

-- Refresh materialized view every 15 minutes (setup via cron job or scheduler)

-- Function to calculate distance between two points (Haversine)
CREATE OR REPLACE FUNCTION calculate_distance(
    lat1 DOUBLE PRECISION,
    lon1 DOUBLE PRECISION,
    lat2 DOUBLE PRECISION,
    lon2 DOUBLE PRECISION
) RETURNS DOUBLE PRECISION AS $$
DECLARE
    R CONSTANT DOUBLE PRECISION := 6371; -- Earth radius in km
    dLat DOUBLE PRECISION;
    dLon DOUBLE PRECISION;
    a DOUBLE PRECISION;
    c DOUBLE PRECISION;
BEGIN
    dLat := RADIANS(lat2 - lat1);
    dLon := RADIANS(lon2 - lon1);

    a := SIN(dLat/2) * SIN(dLat/2) +
         COS(RADIANS(lat1)) * COS(RADIANS(lat2)) *
         SIN(dLon/2) * SIN(dLon/2);

    c := 2 * ATAN2(SQRT(a), SQRT(1-a));

    RETURN R * c;
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Partitioning setup for orders table (by month)
-- This is for very high scale - partition by month
-- Example: orders_2024_01, orders_2024_02, etc.

-- Grant permissions
GRANT ALL PRIVILEGES ON DATABASE quickserve_db TO quickserve;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO quickserve;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO quickserve;

-- Analyze tables for query optimization
ANALYZE users;
ANALYZE restaurants;
ANALYZE menu_items;
ANALYZE orders;
ANALYZE order_items;
ANALYZE deliveries;
ANALYZE payments;
ANALYZE reviews;
