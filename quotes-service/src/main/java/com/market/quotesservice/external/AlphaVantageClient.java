package com.market.quotesservice.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class AlphaVantageClient {

    @Value("${alphavantage.apikey:demo}")
    private String apikey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    public Map<String, Object> getIntraday(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("function", "TIME_SERIES_INTRADAY")
                .queryParam("symbol", symbol)
                .queryParam("interval", "5min")
                .queryParam("apikey", apikey)
                .toUriString();

        return fetch(url);
    }

    public Map<String, Object> getDaily(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("function", "TIME_SERIES_DAILY_ADJUSTED")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apikey)
                .toUriString();

        return fetch(url);
    }

    private Map<String, Object> fetch(String url) {
        log.info("Calling Alpha Vantage: {}", url);
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            log.debug("Alpha Vantage response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Error calling Alpha Vantage", e);
            return null;
        }
    }
}
