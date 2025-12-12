package com.market.watchlistservice.service;
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
        return (FinQuoteTickEvent) redisTemplate.opsForValue().get("latest:" + symbol);
    }

    // Store previous close in Redis under a separate key
    public void savePreviousClose(String symbol, Double previousClose) {
        redisTemplate.opsForValue().set("previousClose:" + symbol, previousClose);
    }

    // Retrieve previous close from Redis
    public double getPreviousClose(String symbol) {
        Double value = (Double) redisTemplate.opsForValue().get("previousClose:" + symbol);
        return value != null ? value : 0.0;
    }
}
