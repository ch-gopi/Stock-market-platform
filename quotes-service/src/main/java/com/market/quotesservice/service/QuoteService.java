package com.market.quotesservice.service;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.quotesservice.dto.QuoteDto;
import com.market.quotesservice.dto.QuoteTickEvent;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
@Service
public class QuoteService {
    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${finnhub.api.key}")
    private String apiToken;

    public QuoteService(RedisTemplate<String, Object> redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "finnhub", fallbackMethod = "fallbackQuote")
    @Retry(name = "finnhub")
    @Bulkhead(name = "finnhub")
    public QuoteDto getTick(String symbol) {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + apiToken;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || response.isEmpty()) {
            throw new IllegalStateException("Empty response from Finnhub");
        }

        QuoteTickEvent tick = QuoteTickEvent.builder()
                .symbol(symbol)
                .open(((Number) response.get("o")).doubleValue())
                .high(((Number) response.get("h")).doubleValue())
                .low(((Number) response.get("l")).doubleValue())
                .price(((Number) response.get("c")).doubleValue())
                .volume(((Number) response.get("v")).longValue())
                .latestTradingDay(
                        Instant.ofEpochSecond(((Number) response.get("t")).longValue())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toString()
                )
                .previousClose(((Number) response.get("pc")).doubleValue())
                .change(((Number) response.get("d")).doubleValue())
                .changePercent(response.get("dp").toString())
                .build();

        // Cache in Redis
        FinQuoteTickEvent finTick = FinQuoteTickEvent.builder()
                .symbol(symbol)
                .price(((Number) response.get("c")).doubleValue())
                .timestamp(System.currentTimeMillis())
                .volume(((Number) response.get("v")).doubleValue())
                .build();

        redisTemplate.opsForValue().set("latest:" + symbol, finTick);
        redisTemplate.opsForValue().set("previousClose:" + symbol, tick.getPreviousClose());

        return toDto(tick);
    }

    // Fallback method when Finnhub fails
    public QuoteDto fallbackQuote(String symbol, Throwable t) {
        FinQuoteTickEvent cached = (FinQuoteTickEvent) redisTemplate.opsForValue().get("latest:" + symbol);
        if (cached != null) {
            QuoteDto dto = new QuoteDto();
            dto.setSymbol(symbol);
            dto.setPrice(cached.getPrice());
            dto.setVolume((long) cached.getVolume());
            dto.setChange(0.0);
            dto.setChangePercent(0.0);
            return dto;
        }
        // Safe default if no cache
        return new QuoteDto(symbol, 0.0, 0.0, 0.0, 0L);
    }

    private QuoteDto toDto(QuoteTickEvent tick) {
        QuoteDto dto = new QuoteDto();
        dto.setSymbol(tick.getSymbol());
        dto.setPrice(tick.getPrice());
        dto.setChange(tick.getChange());
        dto.setChangePercent(Double.parseDouble(tick.getChangePercent()));
        dto.setVolume(tick.getVolume());
        return dto;
    }
}
