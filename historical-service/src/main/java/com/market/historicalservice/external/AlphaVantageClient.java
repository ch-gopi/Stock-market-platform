package com.market.historicalservice.external;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Slf4j
@Component
public class AlphaVantageClient {

    @Value("${alphavantage.apikey}")
    private String apikey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    public Map<String, Object> getQuote(String symbol) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", symbol)
                .queryParam("apikey", apikey)
                .toUriString();

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
