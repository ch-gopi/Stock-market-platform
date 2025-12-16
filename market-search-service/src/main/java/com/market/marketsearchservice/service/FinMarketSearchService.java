package com.market.marketsearchservice.service;
import com.market.marketsearchservice.dto.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
@Service
public class FinMarketSearchService {

    private final WebClient webClient;
    private final String apiKey;
    private final RedisTemplate<String, Object> redisTemplate;

    public FinMarketSearchService(WebClient.Builder builder,
                                  @Value("${finnhub.api.key}") String apiKey,
                                  RedisTemplate<String, Object> redisTemplate) {
        this.webClient = builder.baseUrl("https://finnhub.io/api/v1").build();
        this.apiKey = apiKey;
        this.redisTemplate = redisTemplate;
    }

    @CircuitBreaker(name = "finnhubSearch", fallbackMethod = "fallbackSearch")
    @Retry(name = "finnhubSearch", fallbackMethod = "fallbackSearch")
    public List<StockSearchDto> searchStocks(String query) {
        String cacheKey = "search:" + query.toUpperCase();

        // 1. Try cache first
        StockSearchResultCache cached = (StockSearchResultCache) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null && cached.getResults() != null) {
            return cached.getResults();
        }

        // 2. Call Finnhub /search
        FinnhubSearchResponse searchResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", query)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubSearchResponse.class)
                .timeout(Duration.ofSeconds(3)) // avoid hanging
                .block();

        if (searchResponse == null || searchResponse.getResult() == null) {
            return List.of();
        }

        // 3. Map results with symbol details
        List<StockSearchDto> results = searchResponse.getResult().stream()
                .map(result -> {
                    String exchange = detectExchange(result.getSymbol());
                    FinnhubSymbolDetails details = null;

                    try {
                        List<FinnhubSymbolDetails> symbols = webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/stock/symbol")
                                        .queryParam("exchange", exchange)
                                        .queryParam("token", apiKey)
                                        .build())
                                .retrieve()
                                .bodyToFlux(FinnhubSymbolDetails.class)
                                .timeout(Duration.ofSeconds(3))
                                .collectList()
                                .block();

                        if (symbols != null) {
                            details = symbols.stream()
                                    .filter(d -> d.getSymbol().equals(result.getSymbol()))
                                    .findFirst()
                                    .orElse(null);
                        }
                    } catch (Exception e) {
                        System.err.println("Error fetching symbol details for " + result.getSymbol() + ": " + e.getMessage());
                    }

                    Double score = result.getMatchScore();

                    return new StockSearchDto(
                            result.getSymbol(),
                            result.getDescription(),
                            result.getType(),
                            details != null ? details.getMic() : "US",
                            details != null ? details.getMarketOpen() : "09:30",
                            details != null ? details.getMarketClose() : "16:00",
                            details != null ? details.getTimezone() : "America/New_York",
                            details != null ? details.getCurrency() : "USD",
                            score != null ? score : 1.0000
                    );
                })
                .toList();

        // 4. Cache results with TTL
        StockSearchResultCache cacheWrapper = new StockSearchResultCache();
        cacheWrapper.setResults(results);
        redisTemplate.opsForValue().set(cacheKey, cacheWrapper, 300, TimeUnit.SECONDS);

        return results;
    }

    // Fallback method for circuit breaker / retry
    public List<StockSearchDto> fallbackSearch(String query, Throwable t) {
        String cacheKey = "search:" + query.toUpperCase();
        StockSearchResultCache cached = (StockSearchResultCache) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null && cached.getResults() != null) {
            System.err.println("Finnhub unavailable, serving cached results for " + query);
            return cached.getResults();
        }
        System.err.println("Finnhub unavailable, no cache found for " + query);
        return List.of(); // safe default
    }


    private String detectExchange(String symbol) {
        if (symbol.endsWith(".TO")) return "TO";   // Toronto
        if (symbol.endsWith(".MX")) return "MX";   // Mexico
        if (symbol.endsWith(".VI")) return "VI";   // Vienna
        if (symbol.endsWith(".WA")) return "WA";   // Warsaw
        if (symbol.endsWith(".NE")) return "NE";   // Canada NEO
        if (symbol.endsWith(".L"))  return "L";    // London
        if (symbol.endsWith(".AS")) return "AS";   // Amsterdam
        if (symbol.endsWith(".RO")) return "RO";   // Bucharest
        if (symbol.endsWith(".SN")) return "SN";   // Santiago
        return "US"; // default
    }
}
