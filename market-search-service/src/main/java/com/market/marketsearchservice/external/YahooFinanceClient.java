package com.market.marketsearchservice.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
public class YahooFinanceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_QUOTE_URL = "https://query1.finance.yahoo.com/v7/finance/quote";
    private static final String BASE_CHART_URL = "https://query1.finance.yahoo.com/v8/finance/chart";

    @Retryable(
            value = { HttpClientErrorException.TooManyRequests.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Cacheable("quotes")
    public Map<String, Object> getQuote(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_QUOTE_URL)
                .queryParam("symbols", symbol)
                .toUriString();

        try {
            log.info("Fetching quote for symbol: {}", symbol);
            return restTemplate.getForObject(url, Map.class);
        } catch (HttpClientErrorException.TooManyRequests e) {
            log.warn("Rate limit exceeded for symbol {}: {}", symbol, e.getMessage());
            throw new RuntimeException("External API rate limit exceeded. Please try again later.");
        } catch (Exception e) {
            log.error("Failed to fetch quote for symbol {}: {}", symbol, e.getMessage());
            throw new RuntimeException("Quote fetch failed for symbol: " + symbol);
        }
    }

    @Cacheable("historical")
    public Map<String, Object> getHistorical(String symbol, String range, String interval) {
        String url = String.format("%s/%s?range=%s&interval=%s",
                BASE_CHART_URL, symbol, range, interval);

        try {
            log.info("Fetching historical data for symbol: {}, range: {}, interval: {}", symbol, range, interval);
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            log.error("Failed to fetch historical data for symbol {}: {}", symbol, e.getMessage());
            throw new RuntimeException("Historical data fetch failed for symbol: " + symbol);
        }
    }
}
