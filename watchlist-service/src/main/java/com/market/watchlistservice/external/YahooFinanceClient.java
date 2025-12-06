package com.market.watchlistservice.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class YahooFinanceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getQuote(String symbol) {
        String url = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=" + symbol;
        return restTemplate.getForObject(url, Map.class);
    }
}
