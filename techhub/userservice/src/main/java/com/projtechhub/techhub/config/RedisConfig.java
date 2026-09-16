package com.projtechhub.techhub.config;

/**
 * @author pc
 **/


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

/**
 * Two separate Redis concerns configured here:
 *
 * 1. RedisTemplate<String, String> — used directly in JwtAuthenticationFilter
 *    for the token blacklist. Simple string key → string value ("1").
 *
 * 2. RedisCacheManager — used via @Cacheable/@CacheEvict annotations
 *    on service methods. Stores serialized Java objects as JSON.
 */
@Configuration
public class RedisConfig {

    /**
     * Used for manual Redis operations — specifically the JWT blacklist.
     * Key and value are both plain strings, no JSON serialization needed.
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Used for @Cacheable/@CacheEvict annotations.
     * Stores objects as JSON so they survive app restarts and are readable
     * in Redis CLI for debugging.
     *
     * Default TTL: 1 hour — individual caches can override this.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .disableCachingNullValues(); // never cache null — causes subtle bugs

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}