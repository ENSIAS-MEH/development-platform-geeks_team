package com.techhub.teamservice.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

/**
 * RedisConfig
 *
 * <p>Configures Redis as a second-level cache layer for Team lookups.
 * Uses JSON serialization (not Java serialization) for cross-language
 * compatibility and human-readable keys in Redis CLI.
 *
 * <p>Cache TTLs are deliberately separate per region so the team list
 * cache can expire faster than individual team details.
 */
@Configuration
@EnableCaching
@Profile("!test")
public class RedisConfig {

    @Value("${app.redis.default-ttl:3600}")
    private long defaultTtlSeconds;

    // ── Cache region names (use constants to avoid typos) ────────────────

    public static final String CACHE_TEAMS        = "teams";
    public static final String CACHE_TEAM_MEMBERS = "team-members";
    public static final String CACHE_INVITATIONS  = "invitations";

    // ── ObjectMapper for Redis only — NOT exposed as a bean so Spring Boot's
    //    JacksonAutoConfiguration still creates the web ObjectMapper separately.
    //    Exposing it as @Bean would trigger @ConditionalOnMissingBean and cause
    //    the type-annotated Redis mapper to be used for HTTP responses too.

    private ObjectMapper buildRedisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    // ── RedisTemplate (raw key-value access, e.g. for rate-limiting) ─────

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(buildRedisObjectMapper());

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();

        return template;
    }

    // ── CacheManager (Spring @Cacheable / @CacheEvict) ───────────────────

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(buildRedisObjectMapper());

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(defaultTtlSeconds))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer))
                .disableCachingNullValues();

        // Per-region TTL overrides
        Map<String, RedisCacheConfiguration> regionConfigs = Map.of(
                CACHE_TEAMS,        defaultConfig.entryTtl(Duration.ofMinutes(30)),
                CACHE_TEAM_MEMBERS, defaultConfig.entryTtl(Duration.ofMinutes(15)),
                CACHE_INVITATIONS,  defaultConfig.entryTtl(Duration.ofMinutes(5))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(regionConfigs)
                .transactionAware()
                .build();
    }
}

