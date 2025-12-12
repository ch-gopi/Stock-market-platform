package com.market.quotesservice.service;

import com.market.common.dto.FinQuoteTickEvent;
import com.market.quotesservice.dto.QuoteDto;
import com.market.quotesservice.dto.QuoteTickEvent;
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

    @Value("${finnhub.api.key}")
    private String apiToken;
    private final RedisTemplate<String, Object> redisTemplate;

    public QuoteService(RedisTemplate<String, Object> redisTemplate, RestTemplate restTemplate) {
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
    }


public QuoteDto getTick(String symbol) {


    // 🔎 Fallback: call Finnhub API if Redis miss
    String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + apiToken;
    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

    if (response == null || response.isEmpty()) {
        return null;
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
        FinQuoteTickEvent FinTick = FinQuoteTickEvent.builder()
                .symbol(symbol)
                .price(((Number) response.get("c")).doubleValue())       // current price
                .timestamp(System.currentTimeMillis())                   // now
                .volume(((Number) response.get("v")).doubleValue())      // volume
                .build();

        // Cache in Redis
        redisTemplate.opsForValue().set("latest:" + symbol, FinTick);
        redisTemplate.opsForValue().set("previousClose:" + symbol, tick.getPreviousClose());

    return toDto(tick);
}

public QuoteDto toDto(QuoteTickEvent tick) {
    QuoteDto dto = new QuoteDto();
    dto.setSymbol(tick.getSymbol());
    dto.setPrice(tick.getPrice());
    dto.setChange(tick.getChange());
    dto.setChangePercent(Double.parseDouble(tick.getChangePercent()));
    dto.setVolume(tick.getVolume());


    return dto;
}}