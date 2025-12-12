/*
package com.market.watchlistservice.service;

import com.market.watchlistservice.dto.QuoteTickEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
@Service
public class QuoteCache {
    private final RedisTemplate<String, QuoteTickEvent> redisTemplate;

    public QuoteCache(RedisTemplate<String, QuoteTickEvent> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void update(QuoteTickEvent event) {
        redisTemplate.opsForValue().set(event.getSymbol(), event);
    }

    public QuoteTickEvent getLatest(String symbol) {
        return redisTemplate.opsForValue().get(symbol);
    }
}
*/
