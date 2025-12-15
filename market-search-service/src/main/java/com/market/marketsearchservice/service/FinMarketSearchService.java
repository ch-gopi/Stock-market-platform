package com.market.marketsearchservice.service;
import com.market.marketsearchservice.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    public List<StockSearchDto> searchStocks(String query) {
        String cacheKey = "search:" + query.toUpperCase();

        // 1. Try cache first
        StockSearchResultCache cached = (StockSearchResultCache) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null && cached.getResults() != null) {
            return cached.getResults();
        }

        // 2. Call Finnhub if not cached
        FinnhubSearchResponse searchResponse;
        try {
            searchResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("token", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(FinnhubSearchResponse.class)
                    .block();
        } catch (WebClientResponseException.TooManyRequests e) {
            // 3. If rate limited, return cached fallback if available
            if (cached != null && cached.getResults() != null) return cached.getResults();
            return List.of(); // or defaults
        }

        if (searchResponse == null || searchResponse.getResult() == null) {
            return List.of();
        }

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

                    // Fallback defaults if details are missing
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

        // 4. Wrap results in cache object
        StockSearchResultCache cacheWrapper = new StockSearchResultCache();
        cacheWrapper.setResults(results);

        // 5. Cache latest results with TTL (e.g. 300 seconds)
        redisTemplate.opsForValue().set(cacheKey, cacheWrapper, 300, TimeUnit.SECONDS);

        return results;
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
