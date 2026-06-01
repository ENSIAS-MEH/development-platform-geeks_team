package com.devconnect.eventservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/** Redis cache configuration with per-cache TTLs. */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        configs.put("events", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
        configs.put("event-list", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(15)));

        return RedisCacheManager.builder(factory)
            .withInitialCacheConfigurations(configs)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)))
            .build();
    }
}
