package com.quickserve.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Configuration for L2 Caching
 * Supports 200K+ RPS with proper cache strategies
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Configure Redis Template for manual cache operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JSON serializer for values
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }

    /**
     * Configure Cache Manager with different TTLs per cache
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()));

        // Custom TTLs for different caches
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // User profiles - 1 hour TTL
        cacheConfigurations.put("users", defaultConfig.entryTtl(Duration.ofHours(1)));

        // Restaurant data - 5 minutes TTL (frequently changing)
        cacheConfigurations.put("restaurants", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("restaurantList", defaultConfig.entryTtl(Duration.ofMinutes(2)));

        // Menu items - 5 minutes TTL
        cacheConfigurations.put("menuItems", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Active orders - 30 minutes TTL
        cacheConfigurations.put("orders", defaultConfig.entryTtl(Duration.ofMinutes(30)));

        // Delivery locations - 10 seconds TTL (real-time data)
        cacheConfigurations.put("deliveryLocations", defaultConfig.entryTtl(Duration.ofSeconds(10)));

        // Reviews - 10 minutes TTL
        cacheConfigurations.put("reviews", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
