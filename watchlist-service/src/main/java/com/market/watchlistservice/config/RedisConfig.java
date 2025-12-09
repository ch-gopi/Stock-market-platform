package com.market.watchlistservice.config;

import com.market.watchlistservice.dto.QuoteTickEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, QuoteTickEvent> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, QuoteTickEvent> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<QuoteTickEvent> serializer =
                new Jackson2JsonRedisSerializer<>(QuoteTickEvent.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
