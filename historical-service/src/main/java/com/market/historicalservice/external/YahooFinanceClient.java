package com.market.historicalservice.external;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class YahooFinanceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getHistorical(String symbol, String range) {
        String url = "https://query1.finance.yahoo.com/v8/finance/chart/"
                + symbol + "?interval=1d&range=" + range;

        return restTemplate.getForObject(url, Map.class);
    }
}
