package com.market.marketsearchservice.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class AlphaVantageClient {

    @Value("${alphavantage.apikey:demo}") // fallback: demo key
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BASE_URL = "https://www.alphavantage.co/query";

    public Map<String, Object> searchSymbol(String keywords) {
        String url = BASE_URL +
                "?function=SYMBOL_SEARCH" +
                "&keywords=" + keywords +
                "&apikey=" + apiKey;

        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getIntraday(String symbol) {
        String url = BASE_URL +
                "?function=TIME_SERIES_INTRADAY" +
                "&symbol=" + symbol +
                "&interval=5min&apikey=" + apiKey;

        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getDaily(String symbol) {
        String url = BASE_URL +
                "?function=TIME_SERIES_DAILY_ADJUSTED" +
                "&symbol=" + symbol +
                "&apikey=" + apiKey;

        return restTemplate.getForObject(url, Map.class);
    }
}
