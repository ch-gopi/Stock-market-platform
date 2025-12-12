package com.market.quotesservice.service;

import com.market.common.dto.FinQuoteTickEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuoteCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public QuoteCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveLatestTick(FinQuoteTickEvent tick) {
        redisTemplate.opsForValue().set("latest:" + tick.getSymbol(), tick);
    }

    public FinQuoteTickEvent getLatestTick(String symbol) {
        Object obj = redisTemplate.opsForValue().get("latest:" + symbol);


        return (obj instanceof FinQuoteTickEvent) ? (FinQuoteTickEvent) obj : null;
    }

    public void savePreviousClose(String symbol, Double previousClose) {
        redisTemplate.opsForValue().set("previousClose:" + symbol, previousClose);
    }

    public double getPreviousClose(String symbol) {
        Object obj = redisTemplate.opsForValue().get("previousClose:" + symbol);
        return (obj instanceof Double) ? (Double) obj : 0.0;
    }
}
