package com.techhub.community.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
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

@Configuration
public class RedisConfig {

        /**
         * Creates an ObjectMapper that can handle Java 8 date/time types (Instant,
         * etc.)
         * and includes type information for proper deserialization from Redis.
         */
        private GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                mapper.activateDefaultTyping(
                                LaissezFaireSubTypeValidator.instance,
                                ObjectMapper.DefaultTyping.NON_FINAL,
                                JsonTypeInfo.As.PROPERTY);
                return new GenericJackson2JsonRedisSerializer(mapper);
        }

        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
                RedisTemplate<String, Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(jsonRedisSerializer());
                template.setHashKeySerializer(new StringRedisSerializer());
                template.setHashValueSerializer(jsonRedisSerializer());
                template.afterPropertiesSet();
                return template;
        }

        @Bean
        public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
                GenericJackson2JsonRedisSerializer serializer = jsonRedisSerializer();

                RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(10))
                                .serializeKeysWith(
                                                RedisSerializationContext.SerializationPair
                                                                .fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(
                                                RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                                .disableCachingNullValues();

                // Specific TTL for popular-posts cache (longer TTL since they change less
                // often)
                RedisCacheConfiguration popularPostsConfig = defaultConfig.entryTtl(Duration.ofMinutes(5));

                return RedisCacheManager.builder(connectionFactory)
                                .cacheDefaults(defaultConfig)
                                .withCacheConfiguration("popularPosts", popularPostsConfig)
                                .withCacheConfiguration("groupDetails", defaultConfig.entryTtl(Duration.ofMinutes(15)))
                                .build();
        }
}
